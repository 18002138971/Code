# coding:utf-8

import commands
import os

class PackageInfo:

    def __init__(self, package_name, raw_data_dir):
        self.package_name = package_name
        self.average_memory_size = 0
        self.max_memory_size = 0;
        self.memory_list = []
        self._raw_data_file = None
        self._create_raw_data(raw_data_dir)

    def _create_raw_data(self, raw_data_dir):
        result_dir_is_exist = os.path.exists(raw_data_dir)
        if not result_dir_is_exist:
            print " create %s directory " % raw_data_dir
            os.mkdir(raw_data_dir)
        self._raw_data_file = open(raw_data_dir + '/' + self.package_name, 'w+')

    def _get_process_mem(self, serial_num):
        """获取进程当前占用内存大小.

        参数:
            无.

        返回值:
            进程占用内存大小，如进程不存在，则返回0

        """
        # print "package " + self.package_name + " pid is " + pid
        (status, run_process_memory) = commands.getstatusoutput("adb -s " + serial_num +
                                                                " shell dumpsys meminfo " + self.package_name +
                                                                " | grep 'TOTAL:' | awk '{print $2}'" )
        if run_process_memory != '' and run_process_memory.isdigit():
            run_process_memory = int(run_process_memory) / 1024
            # print "Now process " + self.package_name + " memory is " + str(run_process_memory)
            return run_process_memory
        return 0

    def collect_memory_info(self, serial_num, all_package_raw_data):
        process_memory = self._get_process_mem(serial_num)
        if process_memory > self.max_memory_size:
            self.max_memory_size = process_memory
        size = len(self.memory_list)
        self.average_memory_size = (self.average_memory_size * size + process_memory)/(size + 1)
        self.memory_list.append(process_memory)
        all_package_raw_data.write("package : " + self.package_name + "       memory size : " + str(process_memory) + " MB\n")
        all_package_raw_data.flush()
        self._raw_data_file.write("package : " + self.package_name + "        memory size : " + str(process_memory) + " MB\n")
        self._raw_data_file.flush()

