sudo kill &(cat target/universal/stage/RUNNING_PID)
rm target/universal/stage/RUNNING_PID
./activator clean stage
nohup target/universal/stage/bin/httpserver -Dhttp.port=2080 -Dhttps.port=2443 -Dplay.crypto.secret="t;OM?qC@?i@0i?WWmEB;KKnLl>quYm6153QYoR3rqiKsQpYH1w<:HjMd83Y:vYqZ" &

