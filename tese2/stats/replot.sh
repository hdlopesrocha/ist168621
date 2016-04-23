for f in *.plt
do
	echo "gnuplot < $f" 
	gnuplot < $f > ${f%.plt}.eps
done
