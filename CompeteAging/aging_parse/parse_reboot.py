#!/usr/bin/env python
# -*- coding: utf-8 -*-

#######################################################################################
from sys import exit
import datetime
import re
import copy
from argparse import ArgumentParser
import time
from os.path import isfile

class State(object):
    mark_reboot_start = "kernel reboot     :"
    mark_happen_time = "record time_stamp"

    mark_kmsg_stack = "kmsg stack trace  :"
    mark_stack_start = ">>>"

    # three types reboots info can be got
    # 1.normal kpanic: process+pc+backtrace
    mark_pc_at = "PC is at"
    mark_process = "stack limit ="
    # 2.active kpanic: panic("xxx"): panicInfo+backtrace
    mark_active_panic = "Kernel panic - not syncing:"
    # 3.end mark
    mark_reboot_end = "-----------------------------------------"

    def __init__(self):
        self.in_reboot = False
        self.in_reboot_end = False

    def update(self, line):
        # enter kernel reboot
        if line.startswith(self.mark_reboot_start): #kernel reboot
            self.in_reboot = True
            self.in_reboot_end = False

        # exit kernel reboot
        elif self.in_reboot and self.mark_reboot_end in line:
            self.in_reboot_end = True

        else:
            pass

    def reset(self):
        self.in_reboot = False
        self.in_reboot_end = False


class RebootRecod(object):
    def __init__(self):
        self.reboot_type = None
        self.happen_time = None
        self.pc_at = None
        self.process = None
        self.backtrace = None
        self.active_panic_info = None

    def reset(self):
        self.reboot_type = None
        self.happen_time = None
        self.pc_at = None
        self.process = None
        self.backtrace = None
        self.active_panic_info = None

    def add_reboot_type(self, line):
        self.reboot_type = line.split(':')[1].strip()

    def add_happen_time(self, line):
        self.happen_time = ' '.join(line.split()[3:]).strip()

    def add_pc_at(self, line):
        PC_AT_LINE = re.compile(r'PC is at (.*)')
        pc_at = PC_AT_LINE.search(line)
        if pc_at:
            self.pc_at = pc_at.group(1)

    def add_process(self, line):
        PROCESS_LINE = re.compile(r'Process (.*?) ')
        process = PROCESS_LINE.search(line)
        if process:
            self.process = process.group(1)

    def add_backtrace(self, line):
        if self.backtrace is None:
            self.backtrace = ''
        self.backtrace += line + '\n'

    def add_active_panic_info(self, line):
        ACTIVE_PANIC_LINE = re.compile(r'Kernel panic - not syncing: (.*)')
        active_panic_info = ACTIVE_PANIC_LINE.search(line)
        if active_panic_info:
            self.active_panic_info = active_panic_info.group(1)


def parse_reboot_records(lines):
    # 记录某个bugreport内的reboot record
    # 过滤掉本次monkey之前的record
    rs = []
    t = RebootRecod()
    s = State()

    for line in lines: #一行一行的找reboot中内容
        s.update(line) #找到reboot
        if s.in_reboot:
            # reboot start
            if line.startswith(s.mark_reboot_start): #kernel reboot
                t.add_reboot_type(line) #类型

            elif line.startswith(s.mark_happen_time): #record time_stamp
                t.add_happen_time(line)  #时间

            elif s.mark_kmsg_stack in line: #kmsg stack trace
                continue
            elif s.mark_stack_start in line: #>>>
                continue

            elif s.mark_pc_at in line: #PC is at
                t.add_pc_at(line)
                t.add_backtrace(line)
            elif s.mark_process in line: #stack limit =
                t.add_process(line)
                t.add_backtrace(line)
            elif s.mark_active_panic in line: #Kernel panic - not syncing
                t.add_active_panic_info(line)
                t.add_backtrace(line)
            # reboot end
            elif s.in_reboot_end:
                '''
                print t.reboot_type
                print t.happen_time
                print t.pc_at
                print t.process
                print t.active_panic_info
                print t.backtrace
                '''
                rs.append(copy.copy(t))
                s.reset()
                t.reset()

            else:
                t.add_backtrace(line)
    return rs


def get_file_lines(filename):
    with open(filename) as f:
        data = f.read()
    lines = data.splitlines()
    return lines

def rs_to_desc(rs,parsefile):
    rebootTime=0
    reboot={}
    for r in rs:
        sys = False
        t = False
        data = 'reboot: ' + r.reboot_type + ' ++ ' + r.happen_time
        print data
        if r.happen_time.split(' ')[0].strip()== time.strftime("%Y-%m-%d"):
            t=True
        with open(parsefile, 'r') as f:
            for eachLine in f.readlines():
                if data.strip() == eachLine.strip():
                    sys = True
        if sys == False and t==True:
            with open(parsefile, 'a') as f:
                rebootTime = rebootTime + 1
                f.write(data)
                f.write('\n')
    return rebootTime


def deal_reboot_log(file_name,parsefile):
    lines = get_file_lines(file_name) #读bugreport
    rs = parse_reboot_records(lines)
    return rs_to_desc(rs,parsefile)



