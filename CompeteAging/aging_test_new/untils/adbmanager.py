# coding=utf-8
import subprocess

class adb(object):
	"""docstring for adb"""
	def __init__(self, serial_number):
		super(adb, self).__init__()
		self.DEVICE_ID = serial_number

	def run_shell_cmd(self,cmd_strs):
		cmd = ["adb","-s",self.DEVICE_ID,"shell"] + cmd_strs.split()
		p = self.run_cmd(cmd)
		(stdoutdata, stderrdata) = p.communicate()
		if stderrdata!=None:
			return stderrdata
		else:
			return stdoutdata
			
	def run_adb_cmd(self,cmd_strs):
		cmd = ["adb","-s",self.DEVICE_ID] + cmd_strs.split()
		p = self.run_cmd(cmd)
		(stdoutdata, stderrdata) = p.communicate()
		if stderrdata!=None:
			return stderrdata
		else:
			return stdoutdata
			

	def run_cmd(self,cmd):
		print "Run CMD:%s"%" ".join(cmd)
		p = subprocess.Popen(args=cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
		return p

	def adb_push(self,froms,tos):
		cmd = ["adb","-s",self.DEVICE_ID,"push",froms,tos]
		result = False
		p = self.run_cmd(cmd)
		(stdoutdata, stderrdata) = p.communicate()
		if stderrdata!=None:
			print "Happen error",stderrdata
		elif stdoutdata.find("error")>0:
			print "Happen error",stdoutdata
		else:
			result = True
		return result

	def adb_pull(self,froms,tos):
		cmd = ["adb","-s",self.DEVICE_ID,"pull",froms,tos]
		result = False
		p = self.run_cmd(cmd)
		(stdoutdata, stderrdata) = p.communicate()
		if stderrdata!=None:
			print "Happen error",stderrdata
		elif stdoutdata.find("error")>0:
			print "Happen error",stdoutdata
		else:
			result = True
		return result

		