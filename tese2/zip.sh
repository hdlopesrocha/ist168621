rm *.pdf
rm *.zip
make clean
make thesis
sh xthesis.sh
mv xthesis.pdf abstract.pdf
zip -r METI-68621-Henrique-Rocha.zip thesis.pdf abstract.pdf 

