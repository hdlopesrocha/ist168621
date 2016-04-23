set terminal postscript size 16cm,8cm eps
set title "Memory Usage" font ",20"
set xlabel "Time [seconds]"
set ylabel "Memory [MB]"

set grid
set tics scale 0
     set key below
set xrange [-10:850]
set yrange [0:1800]

set xtics 60
set ytics 200


set style line 1 lt 1 lc rgb "blue" lw 3 
set style line 2 lt 1 lc rgb "red" lw 2
set style line 3 lt 1 lc rgb "green" lw 3
set style line 4 lt 1 lc rgb "magenta" lw 2

start = 68
y_unit = 0.000001

do for [t=0:14] {
	set arrow from t*60,0 to t*60,1800 nohead lc rgb 'yellow' lw 2
}

plot [-10:850] \
	'test_ram_fixed/res_7_mongod.dat' u ($1-start):($2*y_unit) with lines ls 1 title "MongoDB",\
	'test_ram_fixed/res_67_kurento-media-server.dat' u ($1-start):($2*y_unit) with lines ls 2 title "Kurento Media Server",\
	'test_ram_fixed/res_35_kurento-repo.dat' u ($1-start):($2*y_unit) with lines ls 3 title "Kurento Repository", \
	'test_ram_fixed/res_233_bash.dat' u ($1-start):($2*y_unit) with lines ls 4 title "Play Framework"

