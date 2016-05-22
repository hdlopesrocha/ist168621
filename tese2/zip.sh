rm *.pdf
rm *.zip
cd stats
sh replot.sh
cd ..
make clean
make thesis
sh xthesis.sh
mv xthesis.pdf abstract.pdf
zip -r METI-68621-Henrique-Rocha.zip thesis.pdf abstract.pdf 

