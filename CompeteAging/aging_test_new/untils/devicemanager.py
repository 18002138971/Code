# coding=utf-8
from untils import adbmanager
import time

class devices(object):
	"""docstring for device"""
	def __init__(self, serial_number):
		super(devices, self).__init__()
		self.DEVICE_ID = serial_number
		self.adb = adbmanager.adb(self.DEVICE_ID)
	"""
	" 方法实现设备的重启操作通过adb reboot
	" wait参数是否需要等待MIUI是否完全重启完成，默认不检查
	"""
	def reboot_device(self,wait=False):
		print("Start reboot deviced:%s"%self.DEVICE_ID)
		self.adb.run_shell_cmd("reboot")
		if wait:
			time.sleep(10)
			self.check_reboot_status()

	def check_reboot_status(self):
		
		while True:
				status = self.adb.run_shell_cmd("getprop sys.boot_completed")
				if "1" in status and len(status.strip("\n").strip())==1:
					print "boot completed"
					break
				else:
					print "boot not completed wait,after five seconds check"
					time.sleep(5)

	def get_propinfo(self):
		simple_rule = [
			"ro.build.product",
			"ro.build.version.incremental"
			]
		propinfos = {}
		result = self.adb.run_shell_cmd("getprop")
		for line in result.split("\n"):
			l = line.strip().split(":")
			key = l[0][1:-1]
			if key in simple_rule:
				propinfos[key] = l[1].strip()[1:-1]
		return propinfos

	def get_meminfo(self):
		return self.adb.run_shell_cmd("dumpsys -t 90 meminfo")
		
	def get_proc_meminfo(self):
		return self.adb.run_shell_cmd("cat /proc/meminfo")
		
	def get_bugreport(self):
		return self.adb.run_adb_cmd("bugreport")
		
	 
	def get_packages(self):
		return self.adb.run_shell_cmd("dumpsys package packages")

	def unlock_screen(self):
		self.adb.run_shell_cmd("input keyevent KEYCODE_WAKEUP")
		self.adb.run_shell_cmd("input keyevent KEYCODE_WAKEUP")
		time.sleep(1)
		self.adb.run_shell_cmd("input swipe 600 1000 600 100")
		time.sleep(3)

	# def get_subresult_folder():
	# 	info = []
	# 	name_list = [info["product"],info["version"],get_form_time,]
	# 	return info["product"]

