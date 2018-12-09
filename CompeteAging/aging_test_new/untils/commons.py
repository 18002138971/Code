# coding=utf-8
import os
import datetime

def mkdirs(way):
	if not os.path.exists(way):
		os.makedirs(way)
	return os.path.abspath(way)

def mkdirs_in_run_result(subway=None):
	resutl_path="/run_results/"
	if not subway == None:
		resutl_path = resutl_path+subway
	pwd = os.getcwd()+resutl_path
	abspath = mkdirs(pwd)
	return abspath

def get_lists_from_folder(path,byKey=None):
	lists = os.listdir(path)
	re_lists = []
	for l in lists:
		if byKey is not None:
			if byKey in l:
				re_lists.append(os.path.join(path,l))
		else:
			re_lists.append(os.path.join(path,l))
	return re_lists

def get_form_time(form="%y%m%d"):
	now = datetime.datetime.now()
	return now.strftime(form)




		