# otel-test

project -> otel-lib -> logback -> kafka -> elastic

## Local

1. Kafka
  - bin/zookeeper-server-start.sh config/zookeeper.properties
  - ./bin/kafka-server-start.sh config/server.properties
  - bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic spans

2. Elasticsearch
  - docker run --rm --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e "xpack.security.enabled=false" -t docker.elastic.co/elasticsearch/elasticsearch:8.8.0

3. Kibana
  - docker run --rm -it -p 5601:5601 docker.elastic.co/kibana/kibana:8.8.0

4. Logstash
  - use the logshipper.conf file located in the root directory
  - docker run --rm -it --add-host host.docker.internal:host-gateway -v /Users/t719845/Desktop/tools/logstash/helm/pipeline/:/usr/share/logstash/pipeline/ -v /Users/t719845/Desktop/tools/logstash/helm/config/logstash.yml:/usr/share/logstash/config/logstash.yml -p 127.0.0.1:5014:5014/udp logstash:7.17.5