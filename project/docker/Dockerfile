FROM ubuntu
MAINTAINER hrocha
WORKDIR /root
COPY resources /root
RUN apt-get update
RUN apt-get install -y git maven nano software-properties-common wget unzip cron
RUN unzip ssh.zip
RUN unzip kurento-repository.zip
RUN /bin/sh /root/kurento-repository/bin/install.sh
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
RUN echo "deb http://repo.mongodb.org/apt/ubuntu trusty/mongodb-org/3.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.0.list
RUN add-apt-repository ppa:webupd8team/java
RUN echo "deb http://ubuntu.kurento.org trusty kms6" | sudo tee /etc/apt/sources.list.d/kurento.list
RUN wget -O - http://ubuntu.kurento.org/kurento.gpg.key | sudo apt-key add -
RUN apt-get update
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections
RUN apt-get install -y oracle-java8-installer
RUN apt-get install -y mongodb-org
RUN apt-get install -y oracle-java8-set-default
RUN apt-get install -y kurento-server
RUN mkdir -p /data/db
RUN apt-get clean
RUN git clone git@github.com:hdlopesrocha/ist168621.git
WORKDIR /root/ist168621/project/httpserver
RUN ./activator clean compile
#RUN echo "* * * * * /bin/sh /root/pull.sh" > /etc/cron.d/gitpull
EXPOSE 8888 9080 9443 27017 7676
CMD echo "Welcome to Hyper Server"
ENTRYPOINT ["/bin/sh","/root/run.sh"]
