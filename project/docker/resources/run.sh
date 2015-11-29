crontab /root/cron
cron -f &
/usr/bin/mongod &
service kurento-repo start
service kurento-media-server-6.0 start
./activator clean ~run -Dhttp.port=9080 -Dhttps.port=9443
