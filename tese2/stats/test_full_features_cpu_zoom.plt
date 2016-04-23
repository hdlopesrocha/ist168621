set terminal postscript size 16cm,8cm eps
set title "CPU usage" font ",20"
set xlabel "Time [seconds]"
set ylabel "CPU [percentage]"

set grid
set tics scale 0
     set key below
set xrange [-10:850]
set yrange [0:100]

set xtics 60
set ytics 20


set style line 1 lt 1 lc rgb "blue" lw 3 
set style line 2 lt 1 lc rgb "red" lw 2
set style line 3 lt 1 lc rgb "green" lw 3
set style line 4 lt 1 lc rgb "magenta" lw 2

start = 122
y_unit = 1.0

do for [t=0:14] {
	set arrow from t*60,0 to t*60,100 nohead lc rgb 'yellow' lw 2
}

plot [-10:850] \
	'test_full_features/cpu_7_mongod.dat' u ($1-start):($2*y_unit) with lines ls 1 title "MongoDB",\
	'test_full_features/cpu_67_kurento-media-server.dat' u ($1-start):($2*y_unit) with lines ls 2 title "Kurento Media Server",\
	'test_full_features/cpu_35_kurento-repo.dat' u ($1-start):($2*y_unit) with lines ls 3 title "Kurento Repository", \
	'test_full_features/cpu_233_bash.dat' u ($1-start):($2*y_unit) with lines ls 4 title "Play Framework"
