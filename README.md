# SDN-Project-Kafka-Deployment

## Developing Kafka deployment and publisher/consumer for ReST based services
1. Deploy kafka

2. Assume as application is exposing data using ReST services - for that any standard API available on open network can be used.

3. There is a publisher that calls this ReST api to read the data (ensure that the ReST APIs are returning data in JSON Format) and put it on kafka topic using Java APIs.

4. There are three consumers who are reading this data from this topic. i) one of them is printing it locally ii) Sending it using ReST apis to a remote applications. Iii) Storing the data in ElasticSerach DB.



# Setup Instruction
  **1. Clone this repository**

  ```
  git clone https://github.com/slynid/SDN-Project-Kafka-Deployment.git
  ```

  ```
  cd SDN-Project-Kafka-Deployment
  ```
  **2. Install Apache kafka**<br>
  *Prerequisites*<br>
  System must have java installed otherwise install java using below command
  ```
  sudo apt update
  sudo apt install default-jdk
  ```
  *Download Apache Kafka and extract it by using below command*
  ```
  wget http://www-us.apache.org/dist/kafka/2.2.1/kafka_2.12-2.2.1.tgz

  tar xzf kafka_2.12-2.2.1.tgz

  ```
  *Start Kafka Server*<br>
  Kafka uses ZooKeeper, so first, start a ZooKeeper server on your system. You can use the script available with Kafka to get start single-node ZooKeeper instance.
  ```
  cd kafka_2.12-2.2.1

  bin/zookeeper-server-start.sh config/zookeeper.properties
  ```
  Now start the Kafka server:

  ```
  bin/kafka-server-start.sh config/server.properties
 ```
 *Create a Topic in Kafka*
 ```
 bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic CURRENCY_EXCHANGE_RATE

```
After executing above command you will get output like this if it executes successfully.
```
Created topic "CURRENCY_EXCHANGE_RATE".
```
Now you can see the created topic on Kafka by running the list topic command:
```
bin/kafka-topics.sh --list --zookeeper localhost:2181


CURRENCY_EXCHANGE_RATE
```
After installing kafka come out from this directory or open new terminal with this same repository folder
```
cd ..
```
 **3. Install Elasticsearch**<br>

*Download the archive*<br>
```bash
$ wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-0.90.7.zip
```
```bash
$ unzip elasticsearch-0.90.7.zip
```

*Configuration files*<br>
Go to the config folder in elasticsearch-0.90.7 . Edit the file elasticsearch.yml

```bash
sudo vi /etc/elasticsearch/elasticsearch.yml
```

Then find the line that specifies network.bind_host, then uncomment it and change the value to localhost so it looks like the following:
```bash
network.bind_host: localhost
```
Then insert the following line somewhere in the file, to disable dynamic scripts:
```bash
script.disable_dynamic: true
```
Save and exit. 


Move out from the config directory and run

```bash
$ ./bin/elasticsearch
 ```
Elasticsearch should now be running on port 9200. Test elastic search using


```bash
$ curl -X GET 'http://localhost:9200'
```
You should see the following response
```
{
  "ok" : true,
  "status" : 200,
  "name" : "Xavin",
  "version" : {
    "number" : "0.90.7",
    "build_hash" : "36897d07dadcb70886db7f149e645ed3d44eb5f2",
    "build_timestamp" : "2013-11-13T12:06:54Z",
    "build_snapshot" : false,
    "lucene_version" : "4.5.1"
  },
  "tagline" : "You Know, for Search"
}
```
If you see a response similar to the one above, Elasticsearch is working properly. Alternatively, you can query your install of Elasticsearch from a browser by visiting http://localhost:9200. 


**4. Consumer Application**<br>
Go To Spring-Boot-Kafka-Consumer directory and build the application using maven and before building you need to change log file path wherever you want.

```
cd Spring-Boot-Kafka-Consumer
vim src/main/resources/logback-spring.xml
```
change logfile path 
```
   <file>your_log_file.log</file>
   <fileNamePattern>your_log_file.%d{yyyy-MM-dd}.log</fileNamePattern>
```
build application usind below command
```
mvn clean install
```
after this you will get a folder of name *target* and under this folder you will find 
*Kafka-Consumer-App.jar* this is final build for consumer application.


Now you can run this application by using 
```
java -jar Kafka-Consumer-App.jar 
```


**5. Producer Application**<br>
Go To Spring-Boot-Kafka-Producer directory and build the application using maven and before building you need to change log file path wherever you want.

```
cd Spring-Boot-Kafka-Producer
vim src/main/resources/logback-spring.xml
```
change logfile path 
```
   <file>your_log_file.log</file>
   <fileNamePattern>your_log_file.%d{yyyy-MM-dd}.log</fileNamePattern>
```
build application usind below command
```
mvn clean install
```
after this you will get a folder of name *target* and under this folder you will find 
*Kafka-Producer-App.jar* this is final build for producer application.


Now you can run this application by using 
```
java -jar Kafka-Producer-App.jar 
```

Once producer executes it start a scheculed task which will get data from a currency excahge api and push it to kafka broker 

All the three consumer's listining to this broker will immediately recieve this data and do their specific task's.

for examale the third consumer in this application sending data to elasticsearch db.
to check data successfully inserted to elasticsearch go to consumer three log
and find this log message 


```
Message SEND to Elasticsearch :{ elasticsearch URL :: http://localhost:9200/currency_exchange_rate/USDTOINR/{id}/_create } | { id : 1575181948758 } Data Sent To Elasticsearch.
```
copy above id and replace it with <id> in below url 
```
http://localhost:9200/currency_exchange_rate/USDTOINR/<id>

```

open it with browser you can find data if it successfully inserted.



