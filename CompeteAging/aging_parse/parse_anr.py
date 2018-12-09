#!/usr/bin/env python
# encoding: utf-8

import re
import copy
import datetime
import tools
import time

class RepAddr(object):
    pattern_s = [
        r'(@(\w+))',
        r'(pid=(\d+))',
        r'(uid=(\d+))',
        r'(\.java:(\d+)\))',
        r'(\+(\d+)\))',
        r'(fault addr (\w+))'
        ]
    pattern = re.compile('|'.join(pattern_s))

    def __init__(self):
        pass

    def replace(self, s):
        cs = []
        res = self.pattern.findall(s)
        for r in res:
            c = filter(lambda x: x!='', r)
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
    mark_anrs_start = "anr since start"
    mark_anr_start_new = "happen time     :"
    mark_anr_start_old = "record time     :"
    mark_stack_trace = "<<stack trace info>>"
    mark_package_end = "-----------------------------------"
    mark_new_item = ">>>>"

    def __init__(self):
        self.in_package = False
        self.in_anrs = False
        self.in_anr = False
        self.in_stack_trace = False
        self.in_stack = False

    def update(self, line):
        if line.startswith(self.mark_package_start):
            self.in_package = True
        elif line.startswith(self.mark_anrs_start) and self.in_package:
            self.in_anrs = True
        elif (line.startswith(self.mark_anr_start_new) or line.startswith(self.mark_anr_start_old)) and self.in_anrs:
            self.in_anr = True
        elif line.startswith(self.mark_stack_trace) and self.in_anr:
            self.in_stack_trace = True
        elif self.mark_new_item in line and self.in_anr:
            self.end_anr()
        elif self.mark_package_end in line and self.in_package:
            self.end_package()
        else:
            pass

    def end_stack(self):
        self.in_stack_trace = False

    def end_anr(self):
        self.in_anr = False
        self.end_stack()

    def end_anrs(self):
        self.in_anrs = False
        self.end_anr()

    def end_package(self):
        self.in_package = False
        self.end_anrs()

    def reset(self):
        self.end_package()


class ANR(object):
    def __init__(self):
        self.id = None
        self.package_name = None
        self.happen_time = None
        self.package_version = None
        self.package_dir = None
        self.summary = None
        self.stack_trace = None

    def reset(self):
        self.id = None
        self.package_name = None
        self.happen_time = None
        self.package_version = None
        self.package_dir = None
        self.summary = None
        self.stack_trace = None

    def add_summary(self, summary):
        if summary.startswith("["):
            summary = summary[summary.index(self.package_name):]
            summary = summary[summary.index(',') + 1:]
            summary = summary[summary.index(',') + 1:]
            summary = summary[:len(summary) - 1]
            self.summary = summary
        else:
            self.summary = summary

    def add_stack_trace(self, stack):
        if self.stack_trace is None:
            self.stack_trace = ""
        self.stack_trace += stack + "\n"


def get_file_lines(filename):
    with open(filename) as f:
        data = f.read()
    lines = data.splitlines()
    return lines


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


def parse_anrs_old(lines):
    total_running_seconds = 0
    anrs = []
    a = ANR()
    s = State()
    package_name = None
    happen_time = None
    package_total_anr = 0
    package_record_anr = 0
    for line in lines:
        s.update(line)
        if "system crash/anr records summary" in line:
            total_running_seconds += get_running_seconds_for_line(line)
        if s.in_package:
            if line.startswith("package name"):
                package_name = line.split(":")[1].strip()
            elif line.startswith("anr since start"):
                # anr since start : 5 times
                package_total_anr = int(line.split()[4])
            elif s.in_anr:
                if line.startswith("record time     :"):
                    a.package_name = package_name
                    a.happen_time = ' '.join(line.split()[3:]).strip()
                elif s.in_stack_trace:
                    if "stack trace info" not in line:
                        a.add_stack_trace(line)
                elif line.startswith("summary"):
                    words = line.split(":")
                    subtype_summary = words[1:]
                    subtype_summary = ":".join(subtype_summary)
                    subtype_summary = subtype_summary.strip()
                    a.add_summary(subtype_summary)
            else:
                if a.package_name is not None:
                    anrs.append(copy.copy(a))
                    a.reset()
                    s.end_anr()
                    package_record_anr += 1
        else:
            if package_name is not None and package_total_anr != 0:
                if a.package_name is not None:
                    package_record_anr += 1
                    anrs.append(copy.copy(a))
                    a.reset()
                a.package_name = package_name
                a.happen_time = happen_time
                unknown_a_num = package_total_anr - package_record_anr
                aa = copy.copy(a)
                anrs.extend([aa] * unknown_a_num)
                a.reset()
                package_total_anr = 0
                package_record_anr = 0
                s.end_package()
                package_name = None
                happen_time = None
    return anrs, total_running_seconds


def parse_anrs_new(lines):
    total_running_seconds = 0
    anrs = []
    a = ANR()
    s = State()
    package_name = None
    happen_time = None
    package_version = None
    package_dir = None
    subtype_total_anr= 0
    subtype_record_anr = 0
    subtype_name = None
    subtype_summary = None
    for line in lines:
        s.update(line)
        if "system crash/anr records summary" in line:
            total_running_seconds += get_running_seconds_for_line(line)
        if s.in_package:
            if line.startswith("package name"):
                package_name = line.split(":")[1].strip()
            elif line.startswith("package version   :"):
                package_version = line.split(":")[1].strip()
            elif line.startswith("package dir       :"):
                package_dir = line.split(":")[1].strip()
            elif s.in_anrs:
                if line.startswith("subtype     :"):
                    if subtype_name is not None:
                        if (subtype_total_anr - subtype_record_anr) > 0:
                            other_a_num = subtype_total_anr - subtype_record_anr
                            a.package_name = package_name
                            a.package_version = package_version
                            a.package_dir = package_dir
                            a.add_summary(subtype_summary)
                            aa = copy.copy(a)
                            anrs.extend([aa] * other_a_num)
                            a.reset()
                        subtype_summary = None
                        subtype_total_anr = 0
                        subtype_record_anr = 0
                    subtype_name = line.split(':')[1].strip()
                elif line.startswith("happened    :"):
                    subtype_total_anr = int(line.split()[2])
                elif line.startswith('summary     :'):
                    words = line.split(":")
                    subtype_summary = words[1:]
                    subtype_summary = ":".join(subtype_summary)
                    subtype_summary = subtype_summary.strip()
                elif s.in_anr:
                    if line.startswith("happen time     :"):
                        a.package_name = package_name
                        a.package_version = package_version
                        a.package_dir = package_dir
                        a.happen_time =  ' '.join(line.split()[3:]).strip()
                    elif s.in_stack_trace:
                        if "stack trace info" not in line:
                            a.add_stack_trace(line)
                elif line.startswith('>>>>') and a.package_name is not None:
                    a.add_summary(subtype_summary)
                    anrs.append(copy.copy(a))
                    a.reset()
                    subtype_record_anr += 1
        else:
            if package_name is not None:
                if a.package_name is not None:
                    a.add_summary(subtype_summary)
                    anrs.append(copy.copy(a))
                    subtype_record_anr += 1
                    a.reset()
                if (subtype_total_anr - subtype_record_anr) > 0:
                    a.package_name = package_name
                    a.happen_time = happen_time
                    a.package_version = package_version
                    a.package_dir = package_dir
                    a.add_summary(subtype_summary)
                    unknown_a_num = subtype_total_anr - subtype_record_anr
                    aa = copy.copy(a)
                    anrs.extend([aa] * unknown_a_num)
                    a.reset()
                package_name = None
                happen_time = None
                package_version = None
                package_dir = None
                subtype_name = None
                subtype_summary = None
                subtype_total_anr = 0
                subtype_record_anr = 0
                s.end_package()

    return anrs, total_running_seconds


def is_new_version(lines):
    for line in lines:
        if line.startswith('subtype     : Subtype'):
            return True
    return False


def parse_anrs(lines):
    if is_new_version(lines):
        return parse_anrs_new(lines)
    else:
        return parse_anrs_old(lines)


def anr_to_desc(a):
    desc = "%s ++ %s" % (a.package_name, a.summary)
    desc, ns = repaddr.replace(desc)
    return desc, ns

def anrs_to_desc(anrs,parsefile):
    anrTime=0
    anr={}
    for a in anrs:
        sys = False
        t=False
        desc, ns = anr_to_desc(a)
        id = tools.md5_str(desc)
        a.id = id
        if a.happen_time is None:
            a.happen_time= ''
        data = "anr: %s ++ %s" % (a.package_name, a.happen_time)
        print data
        if tools.xstr(a.happen_time).split(' ')[0].strip()== time.strftime("%Y-%m-%d"):
            t=True
        with open(parsefile, 'r') as f:
            for eachLine in f.readlines():
                if data.strip()== eachLine.strip():
                    sys = True

        if sys==False and t==True:
            with open(parsefile, 'a') as f:
                anrTime=anrTime+1
                f.write(data)
                f.write('\n')
    return anrTime


def deal_anr_log(filepath,parsefile):
    lines = get_file_lines(filepath)
    (anrs, total_running_seconds) = parse_anrs(lines)
    return anrs_to_desc(anrs,parsefile)







