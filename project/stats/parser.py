# top -b -d 1 > top.txt
# ifstat -t -i wlan0
import re
import sys
import json
data_proc = {}
data_netw = {}
timestamp = -1


i = 0
for line in sys.stdin:
	if i>2:
		tokens = line.split()
		#print tokens
		if len(tokens) >2:
			time = int(tokens[0])
			tipo = tokens[1]
			if tipo == "NETW" and len(tokens) ==5:
				inter = tokens[2]
				recv = float(tokens[3])/1000
				sent = float(tokens[4])/1000
		
				if inter not in data_netw:		
					data_netw[inter] = {}
				if 'samples' not in data_netw[inter]:
					data_netw[inter]['samples'] = []
				obj = {'recv':recv,'sent':sent,'time':time}
				data_netw[inter]['samples'].append(obj)
				
			elif tipo == "PROC" and len(tokens) ==7:
				pid = int(tokens[2])
				name = tokens[3]
				res = float(tokens[4])/1000000
				virt = float(tokens[5])/1000000
				cpu = float(tokens[6])
			
			
				if pid not in data_proc:		
					data_proc[pid] = {}
				if 'name' not in data_proc[pid]:
					data_proc[pid]['name'] = name
				if 'samples' not in data_proc[pid]:
					data_proc[pid]['samples'] = []

				obj = {'virt':virt,'cpu':cpu,'time':time,'res':res}
				data_proc[pid]['samples'].append(obj)

	i = i + 1

for field in ['cpu','virt','res']:
	print ""
	print "======================= " + field + " ======================="
	print ""
	for p in data_proc:
		proc = data_proc[p]
		line = str(p)+" "+proc['name'] +" * "

		if len(proc['samples'])>10:
			for sample in proc['samples']:
				x = sample['time']
				y = sample[field]
				line = line + "("+str(x)+","+str(y)+")"
			print line

for field in ['sent','recv']:
	print ""
	print "======================= " + field + " ======================="
	print ""
	for p in data_netw:
		proc = data_netw[p]
		line = p+" * "

		if len(proc['samples'])>10:
			for sample in proc['samples']:
				x = sample['time']
				y = sample[field]
				line = line + "("+str(x)+","+str(y)+")"
			print line

