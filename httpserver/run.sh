service kurento-media-server-6.0 stop
service kurento-media-server-6.0 start
nohup ./activator clean ~run -Dhttp.port=2080 -Dhttps.port=2443 &
