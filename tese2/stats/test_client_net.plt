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



set style line 1 lt 1 lc rgb "#0060ad" lw 4  # --- blue
set style line 2 lt 1 lc rgb "#dd181f" lw 4  # --- red
set style line 3 lt 1 lc rgb "#00ad60" lw 4  # --- green
set style line 4 lt 1 lc rgb "#ffb400" lw 4  # --- orange


start = 0
y_unit = 0.001

do for [t=0:13] {
	set arrow from t*60,0 to t*60,1000 nohead lc rgb 'yellow' lw 2
}

plot \
	'client/send.dat' u ($1-start):($2*y_unit) with lines ls 1 title "Sent",\
	'client/recv.dat' u ($1-start):($2*y_unit) with lines ls 2 title "Recv",\

