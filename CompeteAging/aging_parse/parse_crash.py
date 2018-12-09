#!/usr/bin/env python
# encoding: utf-8

import re
import copy
import datetime
import itertools
import tools
import os
import time

class RepAddr(object):
    pattern_s = [
        r'(@(\w+))',
        r'(pid=(\d+))',
        r'(uid=(\d+))',
        r'(\.java:(\d+)\))',
        r'(\+(\d+)\))',
        r'(fault addr (\w+))',
        r'(allocate a (\d+))',
        r'((\d+) free bytes)',
        r'(SourceFile:(\d+))',
        r'(Unknown URI content//[a-zA-Z_/\.]+\+?([\s\d]+))',
        r'((-?\d{5,}))',
        r'(\W([0-9a-fA-F]{8})\W)',
        r'((0x[\da-fA-F]+))',
        r'(near (\"\w+\") syntax error)',
        r'(set pinyin=(\'\'\w+\'\'))',
        r'(where _id=(\d+)\s)',
        r'((\d+\.\d+E?\d+))',
        r'((\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}))',
        r'(-(\d+)\.apk)',
        r'(row (\d+))',
        r'(col (\d+))'
    ]
    pattern = re.compile('|'.join(pattern_s))

    def __init__(self):
        pass

    def replace(self, s):
        cs = []
        res = self.pattern.findall(s)
        for r in res:
            c = filter(lambda x: x != '', r)
            cs.append(c)
        target = s
        ns = {}
        i = 0
        for c in cs:
            if len(c) == 2:
                i += 1
                k = 'N' + str(i)
                ns[k] = c[1]
                v = c[0].replace(c[1], '[%s]' % k)
                target = target.replace(c[0], v, 1)
        return target, ns


repaddr = RepAddr()


class State(object):
    mark_package_start = "package name"
    mark_crashes_start = "crash since start"
    mark_crash_start = "crash proc name"
    mark_stack_trace = "stack trace info"
    mark_c_stack = "backtrace"
    mark_java_stack = "at"
    mark_package_end = "-----------------------------------"
    mark_new_item = ">>>>"

    def __init__(self):
        self.in_package = False
        self.in_crashes = False
        self.in_crash = False
        self.in_stack_trace = False
        self.in_c_stack = False
        self.in_java_stack = False

    def update(self, line):
        if line.startswith(self.mark_package_start):
            self.in_package = True
        elif line.startswith(self.mark_crashes_start) and self.in_package:
            self.in_crashes = True
        elif self.mark_crash_start in line and self.in_crashes:
            self.in_crash = True
        elif self.mark_stack_trace in line and self.in_crash:
            self.in_stack_trace = True
        elif self.mark_new_item in line and self.in_crash:
            self.end_crash()
        elif self.mark_package_end in line and self.in_package:
            self.end_package()
        else:
            pass

    def end_stack(self):
        self.in_stack_trace = False
        self.in_c_stack = False
        self.in_java_stack = False

    def end_crash(self):
        self.in_crash = False
        self.end_stack()

    def end_crashes(self):
        self.in_crashes = False
        self.end_crash()

    def end_package(self):
        self.in_package = False
        self.end_crashes()

    def reset(self):
        self.end_package()


class Crash(object):
    def __init__(self):
        self.package_name = None
        self.happen_time = None
        self.package_version = None
        self.package_dir = None
        self.exception = None
        self.message = None
        self.c_stack = None
        self.java_stack = None
        self.stack_trace = None
        self.id = None

    def reset(self):
        self.package_name = None
        self.happen_time = None
        self.package_version = None
        self.package_dir = None
        self.exception = None
        self.message = None
        self.c_stack = None
        self.java_stack = None
        self.stack_trace = None
        self.id = None

    def add_message(self, message):
        self.message = message

    def add_java_stack(self, stack):
        if self.java_stack is None:
            self.java_stack = []
        self.java_stack.append(stack)

    def filter_java_stack(self):
        if self.java_stack is None:
            return
        stacks = []
        stack_section = []
        for s in self.java_stack:
            if s.startswith("Caused by"):
                stacks.append(stack_section)
                stack_section = []
            else:
                stack_section.append(s)
        stacks.append(stack_section)
        stacks.reverse()
        stacks = list(itertools.chain.from_iterable(stacks))
        valid_index = -1
        for i, stack in enumerate(stacks):
            if stack.startswith(self.package_name) or stack.startswith("com.miui") or stack.startswith("com.xiaomi"):
                valid_index = i
                break
        if valid_index == -1:
            valid_index = 0
        if len(stacks) - valid_index > 3:
            self.java_stack = "-".join(stacks[valid_index: valid_index + 3])
        else:
            self.java_stack = "-".join(stacks[valid_index:])

    def add_c_stack(self, stack):
        if self.c_stack is None:
            self.c_stack = []
        self.c_stack.append(stack)

    def replace_app_lib(self, s):
        target = s
        pattern = r'/app-lib/(([\w\.]+)-\d+)/'
        m = re.search(pattern, s)
        if m is not None:
            target = re.sub(m.group(1), m.group(2), s)
        return target

    def filter_c_stack(self):
        if self.c_stack is None:
            return
        signal_stacks = []
        common_libstacks = []
        privat_libstacks = []
        for s in self.c_stack:
            # cannot get valid
            if s == "----":
                self.c_stack = s
                return
            # signal 11 (SIGSEGV), code 1 (SEGV_MAPERR), fault addr 22ac7774
            elif s.startswith("signal"):
                signal_stacks.append(s)
            else:
                # /data/app-lib/com.taobao.taobao-1/libcocklogic.so
                # ==> /data/app-lib/com.taobao.taobao/libcocklogic.so
                if s.startswith('/data/app-lib/'):
                    s = self.replace_app_lib(s)
                m = re.search(r'/([\w\-\.]+\.so)', s)
                # /system/lib/libmedia.so (android::Visualizer::periodicCapture()+96)
                if m is not None:
                    libname = m.group(1)
                    if libname in ['libdvm.so', 'libc.so', 'libjavacore.so']:
                        common_libstacks.append(s)
                    else:
                        privat_libstacks.append(s)
        stacks = signal_stacks + privat_libstacks + common_libstacks
        self.c_stack = '-'.join(stacks[:3])

    def add_stack_trace(self, stack):
        if self.stack_trace is None:
            self.stack_trace = ""
        self.stack_trace += stack + "\n"


def get_file_lines(filename):
    with open(filename) as f:
        data = f.read()
    lines = data.splitlines()
    return lines


def get_klo_ppid(line):
    pattern = r'klobugreport_FeedbackBackgroundService klo_ppid:(\d+)'
    m = re.search(pattern, line)
    klo_ppid = None
    if m:
        klo_ppid = m.group(1)
    return klo_ppid


def get_app_process_pid(line):
    pattern = r'pid: (\d+), tid: (\d+), name: app_process'
    m = re.search(pattern, line)
    app_process_pid = None
    if m:
        app_process_pid = m.group(1)
    return app_process_pid


def get_running_seconds_for_line(line):
    time_formatter = "%Y-%m-%d %H:%M:%S"
    pattern = r"---system crash/anr records summary \( (.+) to (.+) \)---"
    m = re.search(pattern, line)
    running_seconds = 0
    if m is not None:
        try:
            beginTime = m.group(1)
            endTime = m.group(2)
            date_pattern = r'\d+-\d+-\d+ \d+:\d+:\d+'
            m_begin = re.search(date_pattern, beginTime)
            m_end = re.search(date_pattern, endTime)
            if m_begin and m_end:
                pass
            else:
                date_pattern = r'\d+-\d+ \d+:\d+:\d+'
                m_begin = re.search(date_pattern, beginTime)
                m_end = re.search(date_pattern, endTime)
                if m_begin and m_end:
                    beginTime = '2015-' + beginTime
                    endTime = '2015-' + endTime
            begtime = datetime.datetime.strptime(beginTime, time_formatter)
            endtime = datetime.datetime.strptime(endTime, time_formatter)
            running_seconds = (endtime - begtime).total_seconds()
        except Exception as e:
            print e
    return running_seconds


def parse_crashes_old(lines):
    # summary_pattern = re.compile(r"summary\s+:\s+\[\d+,\d+,\S+,\d+,(.+),(.+),.+,.+\]")
    total_running_seconds = 0
    cs = []
    c = Crash()
    s = State()
    package_name = None
    happen_time = None
    package_total_crash = 0
    package_record_crash = 0
    bugreport_url = None
    pkg_fc_count = 0
    for line in lines:
        s.update(line)
        if "system crash/anr records summary" in line:
            total_running_seconds += get_running_seconds_for_line(line)
        if s.in_package:
            if line.startswith("package name"):
                package_name = line.split(":")[1].strip()
                package_name = rename_pkgName(package_name)
            elif line.startswith("crash since start"):
                # crash since start : 5 times
                if pkg_fc_count >= 10:
                    continue
                package_total_crash = int(line.split()[4])
                package_total_crash = 10 if package_total_crash > 10 else package_total_crash
            elif s.in_crash:
                if line.startswith("crash proc name"):
                    c.package_name = package_name
                if line.startwith("record time     :"):
                    c.happen_time = ' '.join(line.split()[3:]).strip()
                elif s.in_stack_trace:
                    if "stack trace info" not in line:
                        c.add_stack_trace(line)
                    if line.startswith("cannot get valid"):
                        c.add_java_stack('----')
                        c.add_c_stack('----')
                    elif line.strip().startswith("at"):
                        # at com.xiaomi.market.sdk.t.c(Unknown Source)
                        c.add_java_stack(" ".join(line.split()[1:]))
                    elif line.strip().startswith("Caused by"):
                        c.add_java_stack("Caused by")
                    elif line.strip().startswith("#"):
                        # #00 pc 00020464 /system/lib/libc.so (write+12)
                        c.add_c_stack(" ".join(line.split()[3:]))
                    elif line.strip().startswith("signal "):
                        c.add_c_stack(line.strip())
                elif line.startswith("summary"):
                    # summary : [21560,0,com.android.thememanager,8961605,Native crash,Segmentation fault,unknown,0]
                    words = line.split(",")
                    if len(words) >= 8:
                        c.exception = words[4]
                        c.add_message(words[5])
                    elif line.endswith("(null)"):
                        c.exception = "----"
                        c.add_message("----")
                    if c.exception == 'Native crash':
                        c.exception = 'Native Crash'
            else:
                if c.package_name is not None:
                    c.filter_java_stack()
                    c.filter_c_stack()
                    cs.append(copy.copy(c))
                    c.reset()
                    s.end_crash()
                    package_record_crash += 1
                    pkg_fc_count += 1
        else:
            if package_name is not None and package_total_crash != 0:
                if c.package_name is not None:
                    package_record_crash += 1
                    c.filter_java_stack()
                    c.filter_c_stack()
                    cs.append(copy.copy(c))
                    c.reset()
                c.reset()
                package_total_crash = 0
                package_record_crash = 0
                s.end_package()
                package_name = None
                happen_time = None
                pkg_fc_count = 0
    return cs, total_running_seconds


def parse_crashes_new(lines):
    total_running_seconds = 0
    cs = []
    c = Crash()
    s = State()
    package_name = None
    happen_time = None
    package_version = None
    package_dir = None
    subtype_total_crash = 0
    subtype_record_crash = 0
    subtype_name = None
    subtype_exception = None
    subtype_message = None
    bugreport_url = None
    is_target_pro = False
    klo_ppid = None
    pkg_fc_count = 0
    for line in lines:
        s.update(line)
        if "system crash/anr records summary" in line:
            total_running_seconds += get_running_seconds_for_line(line)
        if "klobugreport_FeedbackBackgroundService klo_ppid:" in line:
            klo_ppid = get_klo_ppid(line)
        if s.in_package:
            if line.startswith("package name"):
                package_name = line.split(":")[1].strip()
                package_name = rename_pkgName(package_name)
                if package_name == 'app_process' or package_name == '/system/bin/app_process':
                    is_target_pro = True
                else:
                    is_target_pro = False
            elif line.startswith("package version   :"):
                package_version = line.split(":")[1].strip()
            elif line.startswith("package dir       :"):
                package_dir = line.split(":")[1].strip()
            elif s.in_crashes:
                if pkg_fc_count >= 10:
                    continue
                if line.startswith("subtype     :"):
                    if subtype_name is not None:
                        if (subtype_total_crash - subtype_record_crash) > 0:
                            c.reset()
                            pkg_fc_count += 1
                        subtype_exception = None
                        subtype_message = None
                        subtype_total_crash = 0
                        subtype_record_crash = 0
                    subtype_name = line.split(':')[1].strip()
                elif line.startswith("happened    :"):
                    # happened    : 3 times
                    subtype_total_crash = int(line.split()[2])
                    subtype_total_crash = 10 if subtype_total_crash > 10 else subtype_total_crash
                elif line.startswith('summary     :'):
                    summary = ''.join(line.split(':')[1:]).strip()
                    words = summary.split(',')
                    if len(words) == 0:
                        subtype_exception = '----'
                        subtype_message = '----'
                    if len(words) == 1:
                        subtype_exception = words[0]
                        subtype_message = '----'
                    elif len(words) == 2:
                        subtype_exception = words[0]
                        message = words[1]
                        subtype_message = message
                    elif len(words) >= 4:
                        subtype_exception = words[0]
                        message = ''.join(words[1:-2]).strip()
                        subtype_message = message
                    if subtype_exception == 'Native crash':
                        subtype_exception = 'Native Crash'
                elif s.in_crash:
                    if line.startswith("crash proc name"):
                        c.package_name = package_name
                        c.package_version = package_version
                        c.package_dir = package_dir
                    if line.startswith("happen time     :"):
                        c.happen_time = ' '.join(line.split()[3:]).strip()
                    elif s.in_stack_trace:
                        if "stack trace info" not in line:
                            c.add_stack_trace(line)
                        if "pid" in line:
                            app_process_pid = get_app_process_pid(line)
                            if is_target_pro and klo_ppid and app_process_pid and (klo_ppid != app_process_pid):
                                subtype_name = None
                                subtype_exception = None
                                subtype_message = None
                                subtype_total_crash = 0
                                subtype_record_crash = 0
                                c.reset()
                                continue
                        if line.startswith("cannot get valid"):
                            c.add_java_stack('----')
                            c.add_c_stack('----')
                        elif line.strip().startswith("at"):
                            # at com.xiaomi.market.sdk.t.c(Unknown Source)
                            c.add_java_stack(" ".join(line.split()[1:]))
                        elif line.strip().startswith("Caused by"):
                            c.add_java_stack("Caused by")
                        elif line.strip().startswith("#"):
                            # #00 pc 00020464 /system/lib/libc.so (write+12)
                            c.add_c_stack(" ".join(line.split()[3:]))
                        elif line.strip().startswith("signal "):
                            c.add_c_stack(line.strip())
                elif line.startswith('>>>>') and c.package_name is not None:
                    c.exception = subtype_exception
                    c.message = subtype_message
                    c.filter_java_stack()
                    c.filter_c_stack()
                    cs.append(copy.copy(c))
                    c.reset()
                    subtype_record_crash += 1
                    pkg_fc_count += 1
            else:
                pkg_fc_count = 0
        else:
            if package_name is not None:
                if c.package_name is not None:
                    c.message = subtype_message
                    c.exception = subtype_exception
                    c.filter_java_stack()
                    c.filter_c_stack()
                    cs.append(copy.copy(c))
                    subtype_record_crash += 1
                    c.reset()
                    pkg_fc_count += 1
                if (subtype_total_crash - subtype_record_crash) > 0:
                    c.reset()
                    pkg_fc_count += 1
                package_name = None
                happen_time = None
                package_version = None
                package_dir = None
                subtype_name = None
                subtype_exception = None
                subtype_message = None
                subtype_total_crash = 0
                subtype_record_crash = 0
                s.end_package()
                pkg_fc_count = 0

    return cs, total_running_seconds


def is_new_version(lines):
    for line in lines:
        if line.startswith('subtype     : Subtype'):
            return True
    return False


def parse_crashes(lines):
    if is_new_version(lines):
        return parse_crashes_new(lines)
    else:
        print 'In parse crashes old'
        return parse_crashes_old(lines)


def crash_to_desc(c):
    # desc = "%s ++ %s ++ %s ++ %s ++ %s" % (c.package_name, c.exception, c.message, c.c_stack, c.java_stack)
    desc = "%s ++ %s ++ %s ++ %s" % (c.exception, c.message, c.c_stack, c.java_stack)
    desc, ns = repaddr.replace(desc)
    rename_desc(c)
    desc = c.package_name + ' ++ ' + desc
    return desc, ns


def rename_desc(c):
    pkg_name = c.package_name
    pattern = r"(\/data\/data\/)([^\/]+)(\/?.*)"
    m = re.search(pattern, pkg_name)
    if m is not None:
        c.package_name = m.group(2)


def rename_pkgName(pkgName):
    pkg_name = pkgName
    pattern = r"(\/data\/data\/)([^\/]+)(\/?.*)"
    m = re.search(pattern, pkg_name)
    if m is not None:
        pkg_name = m.group(2)
    else:
        pass
    return pkg_name


def cs_to_desc(cs,parsefile):
    fcTime=0
    for c in cs:
        sys = False
        t= False
        desc, ns = crash_to_desc(c)
        id = tools.md5_str(desc)
        c.id = id
        data= 'crash: '+ tools.xstr(c.package_name) + ' ++ ' + tools.xstr(c.happen_time)
        print data
        if tools.xstr(c.happen_time).split(' ')[0].strip()==time.strftime("%Y-%m-%d"): #取当天的日志
            t=True
        with open(parsefile, 'r') as f:
            #判断是否已经存在记录
            for eachLine in f.readlines():
                if data.strip()== eachLine.strip():
                    sys = True
        #只记录当天和没有重复的
        if sys==False and t==True:
            with open(parsefile, 'a') as f:
                fcTime=fcTime+1
                f.write(data)
                f.write('\n')
    return fcTime

def deal_fc_log(filepath,parsefile):
    lines = get_file_lines(filepath)
    cs, total_running_seconds = parse_crashes(lines)
    return cs_to_desc(cs,parsefile)


