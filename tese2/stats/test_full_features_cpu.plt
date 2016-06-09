set terminal postscript size 16cm,8cm eps color
set title "CPU usage" font ",20"
set xlabel "Time [seconds]"
set ylabel "CPU [percentage]"

set grid
set tics scale 0
     set key below
set xrange [-10:850]
set yrange [0:1200]

set xtics 60
set ytics 200



set style line 1 lt 1 lc rgb "#0060ad" lw 4  # --- blue
set style line 2 lt 1 lc rgb "#dd181f" lw 4  # --- red
set style line 3 lt 1 lc rgb "#00ad60" lw 4  # --- green
set style line 4 lt 1 lc rgb "#ffb400" lw 4  # --- orange


start = 122
y_unit = 1.0

do for [t=0:13] {
	set arrow from t*60,0 to t*60,1200 nohead lc rgb 'yellow' lw 2
}

plot \
	'test_full_features/cpu_7_mongod.dat' u ($1-start):($2*y_unit) with lines ls 1 title "MongoDB",\
	'test_full_features/cpu_67_kurento-media-server.dat' u ($1-start):($2*y_unit) with lines ls 2 title "Kurento Media Server",\
	'test_full_features/cpu_35_kurento-repo.dat' u ($1-start):($2*y_unit) with lines ls 3 title "Kurento Repository", \
	'test_full_features/cpu_233_bash.dat' u ($1-start):($2*y_unit) with lines ls 4 title "Play Framework"

