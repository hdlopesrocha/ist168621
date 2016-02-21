import psutil
import time
import json
import datetime
from threading import Timer
from time import sleep

data = {}
next_call = time.time()

for i in range(0,10):
	print (psutil.net_io_counters(pernic=True))
	ts = datetime.datetime.now()
	for proc in psutil.process_iter():
		if proc.pid not in data:
			data[proc.pid] = {}
		if 'name' not in data[proc.pid]:
			data[proc.pid]['name'] = proc.name()
		if 'change' not in data[proc.pid]:
			data[proc.pid]['change'] = False
		if 'samples' not in data[proc.pid]:
			data[proc.pid]['samples'] = []
		cpu = proc.cpu_percent()
		mem = proc.memory_info()
		obj = {'mem1':mem[0],'mem2':mem[1],'cpu':cpu,'time':i}
		data[proc.pid]['samples'].append(obj)
		data[proc.pid]['change'] = data[proc.pid]['change'] or cpu > 0
	next_call = next_call+1;
	time.sleep(next_call - time.time())

#print json.dumps(data, indent=4, sort_keys=True)
fields = ['cpu','mem1','mem2']

for field in fields:
	print ("")
	print ("======================= " + field + " =======================")
	print ("")
	for p in data:
		proc = data[p]
		line = str(p)+" "+proc['name'] +" * "
		if proc['change']:
			for sample in proc['samples']:
				x = sample['time']
				y = sample[field]
				line = line + "("+str(x)+","+str(y)+")"
			print (line)
