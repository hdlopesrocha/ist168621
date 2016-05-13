set terminal postscript size 8cm,8cm eps
set title "User ages" font ",20"
set xlabel "Age intervals [years]"
set ylabel "Amount of users"

set grid
set tics scale 0
set key below

set xrange [-0.5:3.5]
set yrange [0:10]
set xtics 1

set boxwidth 0.5
set style fill transparent solid 0.5
set style line 1 lc rgb '#0060ad' lt 1 lw 4 pt 7 ps 1.5   # --- blue
set style line 2 lc rgb '#dd181f' lt 1 lw 4 pt 5 ps 1.5   # --- red
set style line 3 lc rgb '#00ad60' lt 1 lw 4 pt 7 ps 1.5   # --- green

plot 'user/int_ages.dat' using 0:1:xticlabels(2) with boxes ls 3 title "Count" 
