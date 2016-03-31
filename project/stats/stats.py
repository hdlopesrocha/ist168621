import psutil
import time
import json
import datetime
from threading import Timer
from time import sleep

next_call = time.time()

ivalues={}

print datetime.datetime.now().strftime('%d/%m/%Y-%H:%M:%S')
print "TIME\tNETW\tINTERFACE\tRECV[B/s]\tSENT[B/s]"
print "TIME\tPROC\tPID\tNAME\tRES[B]\tVIRT[B]\tCPU[%]"

i = 0
while True:
	net = psutil.net_io_counters(pernic=True)
	ts = datetime.datetime.now().strftime('%d/%m/%Y-%H:%M:%S')
	ts = i
	for interface in net:
		sample = net[interface]
		recv = 0
		sent = 0
		
		if interface in ivalues:
			ivalue = ivalues[interface]
			recv = sample.bytes_recv - ivalue.bytes_recv
			sent = sample.bytes_sent - ivalue.bytes_sent
		
		ivalues[interface] = sample
		print ts,"NETW",interface,recv,sent
	for proc in psutil.process_iter():

		cpu = proc.cpu_percent()
		mem = proc.memory_info()
		print ts,"PROC",proc.pid,proc.name(),mem[0],mem[1],cpu

	
	next_call = next_call+1;
	time.sleep(next_call - time.time())
	i = i + 1
