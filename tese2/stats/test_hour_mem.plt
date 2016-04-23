set terminal postscript size 16cm,8cm eps

set title "Memory Usage" font ",20"
set xlabel "Time [seconds]"
set ylabel "Memory [MB]"

set grid
set tics scale 0
set key below

set xrange [0:3600]
set yrange [0:4000]

set xtics 500
set style line 1 lt 1 lc rgb "blue" lw 3 
set style line 2 lt 1 lc rgb "red" lw 2
set style line 3 lt 1 lc rgb "green" lw 3
set style line 4 lt 1 lc rgb "magenta" lw 2

start = 50
y_unit = 0.000001

plot [0:3600] \
	'test_hour/res_8_mongod.dat' u ($1-start):($2*y_unit) with lines ls 1 title "MongoDB",\
	'test_hour/res_91_kurento-media-server.dat' u ($1-start):($2*y_unit) with lines ls 2 title "Kurento Media Server",\
	'test_hour/res_36_kurento-repo.dat' u ($1-start):($2*y_unit) with lines ls 3 title "Kurento Repository", \
	'test_hour/res_228_bash.dat' u ($1-start):($2*y_unit) with lines ls 4 title "Play Framework"

