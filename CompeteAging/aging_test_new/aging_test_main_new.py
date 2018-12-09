#!/usr/bin/env python
# coding:utf-8

import sys
import os
import threading
import datetime
import time
from aging_info import AgingInfo
from aging_excel_tool import AgingExcelTool
from argparse import ArgumentParser
from untils import devicemanager
from untils import commons as c

'''
竞品老化测试运行老化测试脚本，同时进行内存监控
'''

KEY_TEST_NUM = 'test_num'

AGING_TEST_DIR = '/sdcard/aging_test/'
PROC_AGING_FILE_NAME = 'proc_aging_num'

CMD_ADB_START = 'adb -s '
CMD_MKDIR = ' shell mkdir '
CMD_L_S = ' shell ls '
CMD_SET_TEST_NUM = ' shell "echo \"%s\" > ' + AGING_TEST_DIR + PROC_AGING_FILE_NAME + '"'
CMD_GET_TEST_NUM = ' shell cat ' + AGING_TEST_DIR + PROC_AGING_FILE_NAME


class NewAgingTestMain:
    AGING_RESULT_FILE = "aging_result.xlsx"
    testpackage = "com.miui.agingtesting.jingpin"  # 竞品测试包名
    phone = ''
    result_dir = ''

    def __init__(self, serial_num, time_interval, statistics_times, result_dir, test_num): 
        """初始化NewAgingTestMain."""
        self.phone = serial_num
        self.result_dir = result_dir  # /home/jenkins/agingtest/mem_result
        self.statistics_times = statistics_times
        self.time_interval = time_interval
        #resultDir = os.path.join(self.result_dir, self.getDay(),self.getTime())  # /home/jenkins/agingtest/mem_result/result_3shddsjh/20170816/103021
        #os.makedirs(self.result_dir)
        self.create_result_dir()
        self.aging_info = AgingInfo(serial_num, time_interval, statistics_times, self.result_dir)
        #self.aging_excel_tool = AgingExcelTool(self.AGING_RESULT_FILE, resultDir)
        #self.dir = resultDir
        self.test_num = test_num.strip()


    def create_result_dir(self):
        data_dir_is_exist = os.path.exists(self.result_dir)
        if not data_dir_is_exist:
            print 'mkdir : ' + self.result_dir
            os.makedirs(self.result_dir)
        else:
            print 'File ：' + self.result_dir + ' exists'
        os.chdir(self.result_dir)

    def testLoop(self):
        #threads = []
        mthread = threading.Thread(target=self.test, args=())
        #test2 = threading.Thread(target=self.dataMonitor, args=())
        #threads.append(test1)
        #threads.append(test2)
        #for t in threads:
        mthread.start()
        #for t in threads:
        mthread.join()

    def start(self):
        """老化测试入口."""
        print "Now begin run aging test and collect phone info"
        ######################
        # 老化测试的脚本可以在此通过子线程开启
        #####################
        # threads = []
        # test1 = threading.Thread(target=self.test, args=())
        # test2 = threading.Thread(target=self.dataMonitor, args=())
        # threads.append(test1)
        # threads.append(test2)
        # for t in threads:
        #    t.start()
        # for t in threads:
        #    t.join()

        adb_start = CMD_ADB_START + self.phone

        last_test_num = '0'

        os.system(adb_start + CMD_MKDIR + AGING_TEST_DIR)

        pipe = os.popen(adb_start + CMD_L_S + AGING_TEST_DIR)
        lines = pipe.readlines()

        waitWork = False

        print 'lines ', lines
        for line in lines:
            if PROC_AGING_FILE_NAME == line.strip():
                pipe = os.popen(adb_start + CMD_GET_TEST_NUM)
                new_lines = pipe.readlines()
                print 'new_lines ', new_lines
                for line in new_lines:
                    if line.strip().find(KEY_TEST_NUM) >= 0:
                        read_test_num = line.split('#')[1].strip()
                        if read_test_num == self.test_num:
                            print 'collectting meminfo, wait !!'
                            for i in range(0, int(self.statistics_times)):
                                 print 'collectting meminfo '+ str(i+1) +' time !!!'
                                 test2 = threading.Thread(target=self.dataMonitor, args=())
                                 test2.start()
                                 test2.join()
                                 print 'collectting meminfo waitting ' + str(self.time_interval) + ' min !!!'
                                 time.sleep(int(self.time_interval) * 60)
                            print 'Test number enough, wait !!'
                            waitWork = True
                            break
                        else:
                            if not read_test_num.isdigit():
                                print 'Error !! Test number config file error, not digit : ' + read_test_num + ' wait !!'
                                waitWork = True
                                break
                            elif not self.test_num.isdigit():
                                print 'Error !! Input test number error, not digit : ' + self.test_num + ' wait !!'
                                waitWork = True
                                break
                            else:
                                if int(read_test_num) > int(self.test_num):
                                    print 'Error !! Saved test number [' + read_test_num + '] more than ' \
                                          + ' input test number [' + self.test_num + '] , need reset phone , wait !!'
                                    waitWork = True
                                    break
                                else:
                                    print 'last test num : ', read_test_num
                                    last_test_num = read_test_num

        if waitWork:
            eventObj = threading.Event()
            eventObj.clear()
            eventObj.wait()
        for i in range(0, 2):
            self.testLoop()
        print "two loops is over,begin idle 2 hours...."
        sys.stdout.flush()
        time.sleep(60 * 60 * 2)
        print "all done"

        set_number = str(int(last_test_num) + 1)
        os.system('adb' + CMD_SET_TEST_NUM % (KEY_TEST_NUM + '#' + set_number))
        print 'set test number ' + set_number

        sys.stdout.flush()

    def dataMonitor(self):
        d = devicemanager.devices(self.phone)
        meminfo = d.get_meminfo()
        proc_meminfo = d.get_proc_meminfo()
        bugreport = d.get_bugreport()
        firstName = self.aging_info.device + "_"\
        + self.aging_info.miui_version + "_"\
        + self.aging_info.ram_size + "_"\
        + self.aging_info.android_version + "_"\
        + self.aging_info.cpu_info + "_"\
        + self.getDay() +"_"\
        + self.getTime()+"_"
        firstNamePut = "内部代号:" + self.aging_info.device +"\n"\
        + "MIUI版本号:" + self.aging_info.miui_version +"\n"\
        + "手机内存:" + self.aging_info.ram_size +"\n"\
        + "Android版本号:" + self.aging_info.android_version + "\n"\
        + "CPU型号:" + self.aging_info.cpu_info + "\n"\
        + "Date&&Time:" + self.getDay() +"/" +self.getTime()+"\n"
 
    #保存内存信息到文件
        proc_meminfo_file = self.result_dir+"/"+firstName+"proc_meminfo.txt"
        dumpfile = self.result_dir+"/"+firstName+"dumpmem.txt"
        logfile = self.result_dir+"/"+firstName+"bugreport.txt"
        
        df = open(dumpfile,"w")
        df.write(firstNamePut)
        df.write(meminfo)
        df.close()
        
        pf = open(proc_meminfo_file,"w")
        pf.write(proc_meminfo)
        pf.close()
        
        log = open(logfile,"w")
        log.write(bugreport)
        log.close
        # 开始采集运行时输出的信息
        #self.aging_info.collect_information()
        # 结果输出至excel文件
        #self.aging_excel_tool.transfer_data_to_excel(self.aging_info)
        #self.aging_excel_tool.close()
        #os.system("adb -s %s push %s /sdcard/aging_test" % (self.phone, self.result_dir))

        
    '''def _get_phone_information(self):
        """获取手机基本信息."""
        (status, fingerprint) = commands.getstatusoutput("adb -s " + self.phone +
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
        (status, self.phone) = commands.getstatusoutput("adb -s " + self.phone +
                                                         " shell getprop ro.product.name | tr -d '\r'")
        print("内部代号： " +  self.device)
        (status, model) = commands.getstatusoutput("adb -s " + self.phone +
                                                        " shell getprop ro.product.model | tr -d '\r'")
        print("手机型号： " +  model)
        (status, compile_mode) = commands.getstatusoutput("adb -s " + self.phone +
                                                               " shell getprop ro.build.type | tr -d '\r'")
        print("编译模式： " +  compile_mode)
        (status, total_memory) = commands.getstatusoutput("adb -s " + self.phone +
                                                          " shell dumpsys meminfo|grep 'Total RAM:'|awk '{print $3}'")
        # print "total_memory is " + total_memory
        if 'K' in total_memory and ',' in total_memory:
            total_memory = total_memory.replace('K', '').replace(',', '')
        if total_memory != '' and total_memory.isdigit():
            ram_size = str(round(float(total_memory)/1024/1024, 0)) + 'GB'
            print("手机内存： " +  ram_size)'''
    # 执行case
    def test(self):
        os.system(
            "adb -s %s shell am instrument -w -r   -e package %s -e debug false com.miui.marmot.test/android.support.test.runner.AndroidJUnitRunner" % (
                self.phone, self.testpackage))

    def getDay(self):
        now = datetime.datetime.now()
        return now.strftime("%Y%m%d")

    def getTime(self):
        now = datetime.datetime.now()
        return now.strftime("%H%M%S")


def main():
    # 接收参数
    # 手机序列号
    serial = sys.argv[1]
    # 统计时间间隔
    time_interval = sys.argv[2]
    # 统计次数
    statistics_times = sys.argv[3]
    # 结果输出目录,eg: /home/jenkins/agingtest/mem_result
    result_dir = os.path.join(sys.argv[4], "aging_result_" + sys.argv[1])
    # 测试轮数
    test_num = sys.argv[5]
    # 创建老化测试主类，并启动测试
    NewAgingTestMain(serial, time_interval, statistics_times, result_dir, test_num).start()


if __name__ == '__main__':
    main()
