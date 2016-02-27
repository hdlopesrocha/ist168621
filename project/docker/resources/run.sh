crontab /root/cron.txt
mongod &
cron -f &
nohup python stats.py > stats.txt &
service kurento-repo start
service kurento-media-server-6.0 start
cd /root/ist168621
git pull
cd project/httpserver
./activator clean ~run -Dhttp.port=9080 -Dhttps.port=9443
