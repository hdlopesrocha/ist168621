j=$(find . -name "*.java" | xargs cat | grep "[a-Z0-9{}]" | wc -l)
if [ $j != "0" ]; then
	echo "java: $j"
fi

j=$(find . -name "*.html" | xargs cat | grep "[a-Z0-9{}]" | wc -l)
if [ $j != "0" ]; then
        echo "html: $j"
fi

j=$(find . -name "*.css" | xargs cat | grep "[a-Z0-9{}]" | wc -l)
if [ $j != "0" ]; then
        echo "css: $j"
fi

j=$(find . -name "*.js" | xargs cat | grep "[a-Z0-9{}]" | wc -l)
if [ $j != "0" ]; then
        echo "js: $j"
fi



