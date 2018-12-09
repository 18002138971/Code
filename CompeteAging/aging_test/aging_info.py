# coding:utf-8

import commands
import time
import os
import shutil

from package_info import PackageInfo

class AgingInfo:

    PACKAGE_LIST_CONFIG = "package_list.conf"
    _RAW_DATA_DIR_NAME = "raw_data"
    _ALL_PACKAGES_RAW_DATA = "all_packages"
    _PACKAGES_RAW_DIR_NAME = "packages"


    def __init__(self, serial_num, time_interval, statistics_times, result_dir):
        self.serial_num = serial_num
        self.time_interval = time_interval
        self.statistics_times = statistics_times
        self.result_dir = result_dir
        self.device = ''
        self.model = ''
        self.miui_version = ''
        self.android_version = ''
        self.cpu_info = ''
        self.compile_mode = ''
        self.ram_size = ''
        self.package_info_list = []
        self.package_count = 0
        self.phone_used_memory_list= []
        self.phone_free_memory_list= []
        self.origin_used_memory = 0
        self.max_used_memory = 0
        self.all_packages_raw_data = None
        self.packages_raw_data_dir = None
        self._create_raw_data(result_dir)
        self.get_package_from_config_file()
        #获取手机基本信息
        self._get_phone_information()


    def _create_raw_data(self,result_dir):
        #原始数据总路径：result_dir/_RAW_DATA_DIR_NAME
        raw_data_dir = result_dir + '/' +self._RAW_DATA_DIR_NAME
        raw_data_dir_is_exist = os.path.exists(raw_data_dir)
        if raw_data_dir_is_exist:
            shutil.rmtree(raw_data_dir)
        os.mkdir(raw_data_dir)
        self.all_packages_raw_data = open(raw_data_dir+ '/' + self._ALL_PACKAGES_RAW_DATA, 'a')
        self.packages_raw_data_dir = raw_data_dir + '/' + self._PACKAGES_RAW_DIR_NAME
        packages_raw_data_dir_is_exist = os.path.exists(self.packages_raw_data_dir)
        if packages_raw_data_dir_is_exist:
            shutil.rmtree(self.packages_raw_data_dir)
        os.mkdir(self.packages_raw_data_dir)

    def get_package_from_config_file(self):
        f = open(self.PACKAGE_LIST_CONFIG, "r")
        while True:
            line = f.readline()
            if line:
                # print "package_name is " + line.strip()
                self.package_info_list.append(PackageInfo(line.strip(), self.packages_raw_data_dir))
            else:
                break
        f.close()
        self.package_count = len(self.package_info_list)

    def _get_phone_information(self):
        """获取手机基本信息."""
        (status, fingerprint) = commands.getstatusoutput("adb -s " + self.serial_num +
                                                         " shell getprop ro.build.fingerprint")
        if fingerprint.strip() != '':
            split_line = fingerprint.split('/')
            if len(split_line) > 5 and split_line[2].strip() != '' and split_line[4].strip() != '':
                self.android_version = split_line[2].split(':')[1]
                self.cpu_info = split_line[3]
                self.miui_version = split_line[4].split(':')[0]
                print "MIUI 版本号：" +  self.miui_version
                print "Android 版本号： " + self.android_version
                print "CPU 型号： " + self.cpu_info
        (status, self.device) = commands.getstatusoutput("adb -s " + self.serial_num +
                                                         " shell getprop ro.product.name | tr -d '\r'")
        print("内部代号： " +  self.device)
        (status, self.model) = commands.getstatusoutput("adb -s " + self.serial_num +
                                                        " shell getprop ro.product.model | tr -d '\r'")
        print("手机型号： " +  self.model)
        (status, self.compile_mode) = commands.getstatusoutput("adb -s " + self.serial_num +
                                                               " shell getprop ro.build.type | tr -d '\r'")
        print("编译模式： " +  self.compile_mode)
        (status, total_memory) = commands.getstatusoutput("adb -s " + self.serial_num +
                                                          " shell dumpsys meminfo|grep 'Total RAM:'|awk '{print $3}'")
        # print "total_memory is " + total_memory
        if 'K' in total_memory and ',' in total_memory:
            total_memory = total_memory.replace('K', '').replace(',', '')
        if total_memory != '' and total_memory.isdigit():
            self.ram_size = str(round(float(total_memory)/1024/1024, 0)) + 'GB'
            print("手机内存： " +  self.ram_size)

    def _get_phone_memory(self):
        """获取手机当前占用内存大小.

        参数:
            无.

        返回值:
            进程占用内存大小

        """
        (status, mem_total) = commands.getstatusoutput("adb -s " + self.serial_num + " shell cat /proc/meminfo " +
                                                                " | grep MemTotal | awk '{print $2}' | tr -d '\r'" )
        (status, mem_free) = commands.getstatusoutput("adb -s " + self.serial_num + " shell cat /proc/meminfo " +
                                                       " | grep MemFree | awk '{print $2}' | tr -d '\r'" )
        (status, buffers) = commands.getstatusoutput("adb -s " + self.serial_num + " shell cat /proc/meminfo " +
                                                       " | grep Buffers | awk '{print $2}' | tr -d '\r'" )
        #这个地方值得注意，不同的手机品牌cached值后面后缀不一样，具体不好说
        (status, cached) = commands.getstatusoutput("adb -s " + self.serial_num + " shell cat /proc/meminfo " +
                                                       " | grep Cached | awk '{print $2}' | tr -d '\r'" )
        cached = cached.split('\n')[0]
        # print "mem_total is " + mem_total
        # print "mem_free is " + mem_free
        # print "buffers is " + buffers
        # print "cached is " + cached
        if int(mem_total) > 0 and int(mem_free) > 0 and int(buffers) > 0 and int(cached) > 0:
            memory_used = (int(mem_total) - int(mem_free) - int(buffers) - int(cached)) / 1024
            self.phone_used_memory_list.append(memory_used)
            if self.origin_used_memory == 0:
                self.origin_used_memory = memory_used
            if self.max_used_memory < memory_used:
                self.max_used_memory = memory_used
            memory_free = int(mem_total) / 1024 - memory_used
            self.phone_free_memory_list.append(memory_free)
        else:
            self.phone_used_memory_list.append(-1)
            self.phone_free_memory_list.append(-1)

    def collect_information(self):
        for i in range(int(self.statistics_times)):
            print "collect memory info " + str(i+1) + " times"
            self.all_packages_raw_data.write("####################")
            self.all_packages_raw_data.write("collect memory info "+ str(i+1) + " times")
            self.all_packages_raw_data.write("####################\n")
            self.all_packages_raw_data.flush()
            time.sleep(int(self.time_interval) * 60)
            self._get_phone_memory()
            for package_info in self.package_info_list:
                package_info.collect_memory_info(self.serial_num, self.all_packages_raw_data)
