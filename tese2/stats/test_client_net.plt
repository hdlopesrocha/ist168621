set terminal postscript size 16cm,8cm eps
set title "Network usage" font ",20"
set xlabel "Time [seconds]"
set ylabel "Transfer rate [kbps]"
set grid
set tics scale 0
set key below
set xrange [-10:790]
set yrange [0:1000]

set xtics 60
set ytics 100


set style line 1 lt 1 lc rgb "blue" lw 3 
set style line 2 lt 1 lc rgb "red" lw 2
set style line 3 lt 1 lc rgb "green" lw 3
set style line 4 lt 1 lc rgb "magenta" lw 2

start = 0
y_unit = 0.001

do for [t=0:13] {
	set arrow from t*60,0 to t*60,1000 nohead lc rgb 'yellow' lw 2
}

plot [-10:790] \
	'client/send.dat' u ($1-start):($2*y_unit) with lines ls 1 title "Sent",\
	'client/recv.dat' u ($1-start):($2*y_unit) with lines ls 2 title "Recv",\

