#!/usr/bin/env python
# -*- coding: utf-8 -*-

#######################################################################################
import os
import tempfile
import sys
import time
from subprocess import Popen
import hashlib
import config

#######################################################################################
class Command(object):
    def __init__(self):
        self.logf_name = "%s%s%s" % (tempfile.gettempdir(), os.sep, "%s%f" % ("apktest.log.", time.time()));
        self.wf_name = "%s%s%s" % (tempfile.gettempdir(), os.sep, "%s%f" % ("apktest.log.wf.", time.time()));
        self.logf = open(self.logf_name, 'w+')
        self.wf = open(self.wf_name, 'w+')

    def run_cmd(self, cmd, stdout='', stderr='', timeout=10):
        if not isinstance(timeout, (int, long, float)):
            print 'ERROR: invalid "timeout" value in runCmd()'
            sys.exit(0)
        if not hasattr(stdout, 'fileno') or not hasattr(stdout, 'write'):
            stdout = self.logf;
        if not hasattr(stderr, 'fileno') or not hasattr(stderr, 'write'):
            stderr = self.wf;
        start_time = time.time()
        process = Popen(cmd, stdout=stdout, stderr=stderr, shell=True)
        while True:
            time.sleep(1)
            process.poll()
            if process.returncode == None:
                if start_time + timeout < time.time():
                    return False
                else:
                    continue
            else:
                stdout.seek(0)
                stderr.seek(0)
                return process.returncode, stdout.read(), stderr.read()


c = Command()

# 获取手机时间，防止因手机不联网导致的手机时间和当前时间不一致
def get_device_time():
    device_time = os.popen("adb shell 'date +\"%Y-%m-%d %H:%M:%S\"'").read().strip()
    return device_time


def get_device_child_file_name(file_path):
    file_names = os.popen("adb shell ls " + file_path).read()
    return file_names


def connect_adb():
    ret = c.run_cmd("adb wait-for-device")
    if ret == False:
        print "##Error:please reconnect the device"
        os.system("adb wait-for-device")
        print "##Connection Recovery"


def kill_device_process(process_name):
    if process_name is None or not process_name:
        return
    # 根据包名杀死进程
    print c.run_cmd("adb shell am kill %s" % process_name)
    lines = os.popen("adb shell ps|grep '%s'" % process_name).read()
    for line in lines.splitlines():
        pid = line.split()[1]
        c.run_cmd("adb shell kill %s" % pid)


def kill_recent_task():
    os.system("adb shell am broadcast -a com.android.systemui.taskmanager.Clear")


def md5_str(s):
    m = hashlib.md5()
    m.update(s)
    return m.hexdigest()


def get_file_lines(filename):
    lines = []
    try:
        with open(filename) as f:
            data = f.read()
            lines = data.splitlines()
    except Exception as e:
        print e
        lines = []
    return lines


def get_device_file_lines(filename):
    lines = []
    try:
        lines = os.popen("adb shell cat '%s'" % filename).read().splitlines()
    except Exception as e:
        print e
        lines = []
    return lines

def get_file_line(filename):
    lines = []
    try:
        lines = os.popen("cat '%s'" % filename).read().splitlines()
    except Exception as e:
        print e
        lines = []
    return lines

def xstr(s):
    if s is None:
        return ''
    return str(s)


def get_version(packagename):
    version_code = ''
    version_name = ''
    try:
        version_code = os.popen(
            "adb shell dumpsys package '%s'|grep versionCode|awk '{print $1}'" % packagename).read().split('=')[
            1].strip()
        version_name = os.popen(
            "adb shell dumpsys package '%s'|grep versionName" % packagename).read().split('=')[1].strip()
    except Exception as e:
        print e
    return version_code, version_name

def rm_app_file(file_name):
    os.system("adb shell rm -rf " + config.CONST_APP_PATH + file_name)

def push_app_file(file_name, file_path=config.CONST_APP_PATH):
    os.system("adb shell mkdir -p " + file_path)
    os.system("adb push " + file_name + " " + file_path)

def new_app_file(file_name, file_path=config.CONST_APP_PATH):
    os.system("adb shell mkdir -p " + file_path)
    os.system("adb shell touch " + file_path + file_name)

def bgexec_file(file_name, file_path=config.CONST_APP_PATH):
    os.system("adb shell 'sh " + file_path + file_name + " &'")

def clear_device():
    dirs = ['/sdcard/downloaded_rom',
            '/sdcard/ramdump',
            '/sdcard/miliao',
            '/sdcard/MiMarket',
            '/sdcard/baidu',
            '/sdcard/sina',
            '/sdcard/netease',
            '/sdcard/autohomemain',
            '/sdcard/soufun',
            '/sdcard/wandoujia',
            '/sdcard/VideoCache',
            '/sdcard/BaiduMap',
            '/sdcard/Pictures',
            '/sdcard/Download',
            '/sdcard/UCDownloads',
            ]
    for dir in dirs:
        try:
            c.run_cmd("adb shell rm -rf %s" % (dir))
        except Exception as e:
            print e

def clear_new_app(backup_packages_list):
    now_packages = get_all_app()
    new_packages = list(set(now_packages) - set(backup_packages_list))
    for package in new_packages:
        try:
            c.run_cmd("adb uninstall %s" % (package.strip().split(":")[1]))
        except Exception as e:
            print e

def get_all_app():
    packages = []
    try:
        packages = os.popen("adb shell pm list packages").read().splitlines()
    except Exception as e:
        print e
        packages = []
    return packages

def save_all_app(backup_app):
    try:
        os.popen("adb shell pm list packages -f >> " + backup_app)
    except Exception as e:
        print e




