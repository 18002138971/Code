#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import os
import parse_crash
import parse_anr
import parse_reboot
import time
from time import sleep

'''
竞品老化测试统计FC、ANR、REBOOT次数
'''

class Bugresult:
    bugreport_dir=''
    filePath=''
    fc={}
    anr={}
    reboot={}
    device=''
    serial=''
    bugreportName = ''

    def __init__(self, path,serial_num):
        self.serial = serial_num
        self.device =self.getName()
        self.fcTime=0 #统计本轮次数
        self.anrTime = 0
        self.rebootTime = 0
        self.report_dir = os.path.join(path,'report_' + self.serial)  # /home/jenkins/agingtest/bugreport/report_27662a80/

    def initfile(self):
        if not os.path.isdir(self.report_dir):
            os.makedirs(self.report_dir)
        self.bugreport_dir = os.path.join(self.report_dir, time.strftime("%Y_%m_%d"))
        if not os.path.isdir(self.bugreport_dir):
            os.makedirs(self.bugreport_dir)
        self.bugreportName = "bugreport_" + time.strftime("%H%M%S") + '.txt'
        self.bugreportfile = os.path.join(self.bugreport_dir, self.bugreportName)
        self.filePath = os.path.join(self.bugreport_dir,'logParse_' + time.strftime("%Y%m%d") + '.txt')  # 记录分析详情的txt文档

    def getResult(self):
        self.initfile()
        self.getBugreport()
        self.initTxt()
        self.writeTxt()
        self.getTotal()
        os.system("adb -s %s push %s /sdcard/aging_test/report/" % (self.serial, self.filePath))

    def getBugreport(self):
        os.system(
            "adb -s %s bugreport > %s" % (self.serial, self.bugreportfile))
        sleep(60)

    def writeTxt(self):
        with open (self.filePath,'a') as f:
            f.write("第"+str(self.getTurn())+"轮"+'  '+self.bugreportName+'\n')
        self.fcTime = parse_crash.deal_fc_log(self.bugreportfile,self.filePath)
        self.anrTime = parse_anr.deal_anr_log(self.bugreportfile, self.filePath)
        self.rebootTime = parse_reboot.deal_reboot_log(self.bugreportfile, self.filePath)
        with open (self.filePath,'a') as f:
            if self.fcTime==0 and self.anrTime==0 and self.rebootTime==0:
                f.write("NULL"+"\n")
            f.write("\n")
        print 'FC:'+str(self.fcTime)+'\n'
        print 'ANR:' + str(self.anrTime) + '\n'
        print 'REBOOT:'+str(self.rebootTime)+ '\n'

    def getTotal(self):
        f=open(self.filePath,'r+')
        temp=f.readlines()
        temp[3]="FC:"+str(int(temp[3].split(':')[1])+self.fcTime)+'\n'
        temp[4]="ANR:"+str(int(temp[4].split(':')[1])+self.anrTime)+'\n'
        temp[5]= "REBOOT:" + str(int(temp[5].split(':')[1]) + self.rebootTime)+'\n'
        f=open(self.filePath,'w+')
        f.writelines(temp)

    def initTxt(self):
        if not os.path.isfile(self.filePath):
            with open(self.filePath, 'w') as f:
                f.write('**********'+time.strftime("%Y-%m-%d")+"手机FC、ANR、REBOOT统计情况"+'**********'+'\n')
                f.write("设备名称:" + self.device + '\n')
                f.write("序列号:"+self.serial+'\n')
                f.write("FC:0"+'\n')
                f.write("ANR:0" + '\n')
                f.write("REBOOT:0" + '\n')
                f.write('*********************************************************'+ '\n')

    def getName(self):
        #r = os.popen('adb -s %s shell cat /system/build.prop | grep "product.name"' % self.serial).read().split('=')[1]
        # bug fix: 之前的获取方式存在权限问题，导致获取不到 deviceName，这里改成直接用 adb devices -l 的方式来截取
        cmd = 'adb devices -l | grep {}'.format(self.serial)
        r = os.popen(cmd).read().split('product:')
        print 'devices info get :{}'.format(r)
        deviceName = "Unknown"
        if len(r)>=2:
            temp = r[1].split()
            deviceName = temp[0] if len(temp) >=2 else "Unknown"
        print "deviceName: " + deviceName
        return deviceName.strip()

    def getTurn(self):#获取轮次数，通过*****判断到第几轮了
        num=1
        with open (self.filePath,'r') as f:
            for eachline in f.readlines():
                if eachline.find("第")>-1:
                    num=num+1
        return num

if __name__ == '__main__':
    m=Bugresult(sys.argv[1],sys.argv[2]) #python python.py /home/jenkins/agingtest
    m.getResult()
