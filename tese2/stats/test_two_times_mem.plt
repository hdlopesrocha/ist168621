set terminal postscript size 16cm,8cm eps color
set title "Memory Usage" font ",20"
set xlabel "Time [seconds]"
set ylabel "Memory [MB]"

set grid
set tics scale 0
     set key below
set xrange [-10:1690]
set yrange [0:1400]

set xtics 120


set style line 1 lt 1 lc rgb "#0060ad" lw 4  # --- blue
set style line 2 lt 1 lc rgb "#dd181f" lw 4  # --- red
set style line 3 lt 1 lc rgb "#00ad60" lw 4  # --- green
set style line 4 lt 1 lc rgb "#ffb400" lw 4  # --- orange


start = 72
y_unit = 0.000001

do for [t=0:27] {
	set arrow from t*60,0 to t*60,1400 nohead lc rgb 'yellow' lw 2
}

plot  \
	'test_two_times3/res_7_mongod.dat' u ($1-start):($2*y_unit) with lines ls 1 title "MongoDB",\
	'test_two_times3/res_68_kurento-media-server.dat' u ($1-start):($2*y_unit) with lines ls 2 title "Kurento Media Server",\
	'test_two_times3/res_35_kurento-repo.dat' u ($1-start):($2*y_unit) with lines ls 3 title "Kurento Repository", \
	'test_two_times3/res_234_bash.dat' u ($1-start):($2*y_unit) with lines ls 4 title "Play Framework"

