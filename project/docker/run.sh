rm nohup.out
nohup docker run -p 9080:9080 -p 8888 -p 9443:9443 -p 27017 -p 7676:7676 -p 9022:22 hrocha/server &
tail -f nohup.out
