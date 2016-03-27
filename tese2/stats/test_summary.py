# top -b -d 1 > top.txt
# ifstat -t -i wlan0
import re
import sys
import json


shift = int(sys.argv[1]) if len(sys.argv)>0 else 0 


summary_time = None


sum = 0
len = 0

for line in sys.stdin:
	tokens = line.split()
	time = long(tokens[0])
	value = float(tokens[1])

	if summary_time is None:
		summary_time = time
		
	sum = sum + value
	len = len + 1
	
	if (time - shift) % 60 == 0:
		new_value = sum/len
	
		print str(summary_time) + " " + str(new_value)
		print str(time) + " " + str(new_value)
		sum = 0
		len = 0
			
		summary_time = time

