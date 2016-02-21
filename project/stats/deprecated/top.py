# top -b -d 1 > top.txt
# ifstat -t -i wlan0
import re
import sys
import json
data = {}
timestamp = -1



for line in sys.stdin:
	if "load average" in line:
		timestamp = timestamp + 1
	else:
		#PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND
		tokens = line.split()
		if len(tokens) >0 and tokens[0].isdigit():
			pid = int(tokens[0])
			res = tokens[5].replace(',','.')
			
			
			if res.endswith('g'):
				res=float(res[:-1])*1000000
			elif res.endswith('m'):
				res=float(res[:-1])*1000	
			else :
				res = float(res)
		
			res=res/1024
		
			cpu = float(tokens[8].replace(',','.'))
			pmem = float(tokens[9].replace(',','.'))
			cmd = tokens[11]

			if pid not in data:		
				data[pid] = {}
			if 'name' not in data[pid]:
				data[pid]['name'] = cmd
			if 'change' not in data[pid]:
				data[pid]['change'] = False
			if 'samples' not in data[pid]:
				data[pid]['samples'] = []

			obj = {'pmem':pmem,'cpu':cpu,'time':timestamp,'mem':res}
			data[pid]['samples'].append(obj)
			data[pid]['change'] = data[pid]['change'] or cpu > 0


#print json.dumps(data, indent=4, sort_keys=True)

fields = ['cpu','pmem','mem']


for field in fields:
	print ""
	print "======================= " + field + " ======================="
	print ""
	for p in data:
		proc = data[p]
		line = str(p)+" "+proc['name'] +" * "


		if proc['change']:
			for sample in proc['samples']:
				x = sample['time']
				y = sample[field]
				line = line + "("+str(x)+","+str(y)+")"

			print line

