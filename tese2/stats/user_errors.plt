set terminal postscript size 8cm,8cm eps color
set title "Task errors" font ",20"
set xlabel "Tasks"
set ylabel "Number of errors"

set grid
set tics scale 0
set key below

set xrange [-0.5:4.5]
set yrange [0:3]
set xtics rotate by 30 right

set style line 1 lc rgb '#0060ad' lt 1 lw 4 pt 7 ps 1.5   # --- blue
set style line 2 lc rgb '#dd181f' lt 1 lw 4 pt 5 ps 1.5   # --- red
set style line 3 lc rgb '#00ad60' lt 1 lw 4 pt 7 ps 1.5   # --- green

plot 'user/int_errors.dat' using 0:1:2:xticlabels(3) w yerrorbars ls 1 title "Confidence interval and mean"
     
