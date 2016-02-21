# top -b -d 1 > top.txt
# ifstat -t -i wlan0
import re
import sys
import json

data = {}
timestamp = -1
current_date = ""
i = 0

for line in sys.stdin:
	if i > 2:
		#TIME PID USER PROGRAM DEV SENT RECEIVED UNIT

		
		tokens = line.split()
		if len(tokens) == 8:
			date = tokens[0]
			pid = int(tokens[1])
			name = tokens[3]
			recv = float(tokens[5].replace(',','.'))
			sent = float(tokens[6].replace(',','.'))


			if date != current_date:
				timestamp = timestamp +1
				current_date = date		
		

			print timestamp,line, date, current_date
			
			if pid not in data:		
				data[pid] = {}
			if 'name' not in data[pid]:
				data[pid]['name'] = name
			if 'samples' not in data[pid]:
				data[pid]['samples'] = []

			obj = {'sent':sent,'recv':recv,'time':timestamp}
			data[pid]['samples'].append(obj)

	i=i+1

#print json.dumps(data, indent=4, sort_keys=True)


fields = ['sent','recv']


for field in fields:
	print ""
	print "======================= " + field + " ======================="
	print ""
	for p in data:
		proc = data[p]
		line = str(p)+" "+proc['name'] +" * "


		for sample in proc['samples']:
			x = sample['time']
			y = sample[field]
			line = line + "("+str(x)+","+str(y)+")"

		print line

