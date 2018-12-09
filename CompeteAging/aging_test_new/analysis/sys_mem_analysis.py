# coding=utf-8
import os,re

#Total RAM: 1,901,984K (status normal)
#Free RAM:   665,501K (  240,685K cached pss +   386,436K cached kernel +    38,380K free)
#期望结果：开机剩余内存：6G内存 50%,4G内存 50%，3G内存 40%， 2G内存 30%， 1G内存 25%优于竞品
def analyDumpsysMinfo(file):
	#folder_path = os.path.dirname(file)
	total_ram = None
	free_ram = None
	with open(file) as f:
		for line in f.readlines():
			line = line.strip("\n").strip()
			if "Total RAM" in line:
				total_ram = line.split(":")[1].strip().split(" ")[0].strip()
			if "Free RAM" in line:
				free_ram = line.split(":")[1].strip().split(" ")[0].strip()

	total = float(re.sub("\D", "", total_ram))
	free = float(re.sub("\D", "", free_ram))
	per = "%.2f%%"%(free/total * 100)

	results = {"total":total_ram,"free":free,"per":per}
	return results


