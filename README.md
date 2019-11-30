# SDN-Project-Kafka-Deployment

## Developing Kafka deployment and publisher/consumer for ReST based services
1. Deploy kafka

2. Assume as application is exposing data using ReST services - for that any standard API available on open network can be used.

3. There is a publisher that calls this ReST api to read the data (ensure that the ReST APIs are returning data in JSON Format) and put it on kafka topic using Java APIs.

4. There are three consumers who are reading this data from this topic. i) one of them is printing it locally ii) Sending it using ReST apis to a remote applications. Iii) Storing the data in ElasticSerach DB.



## Steps to run




Instruction for all the above steps is given below. 













## Install Apache kafka on ubuntu

***Step 1:Pre-requisite***

Apache Kafka required Java to run. You must have java installed on your system. Execute below command to install default      OpenJDK on your system from the official PPA’s.

```sudo apt update
$ sudo apt install default-jdk
```

***Step 2 – Download Apache Kafka***

Download the Apache Kafka binary files from its official download website. You can also select any nearby mirror to download.
wget http://www-us.apache.org/dist/kafka/2.2.1/kafka_2.12-2.2.1.tgz
Then extract the archive file
tar xzf kafka_2.12-2.2.1.tgz
mv kafka_2.12-2.2.1 /usr/local/kafka

***Step 3 – Start Kafka Server***

Kafka uses ZooKeeper, so first, start a ZooKeeper server on your system. You can use the script available with Kafka to get start single-node ZooKeeper instance.

```bashcd 
$ /usr/local/kafka
$ bin/zookeeper-server-start.sh config/zookeeper.properties
```

Now start the Kafka server:
```bash
$ bin/kafka-server-start.sh config/server.properties
```

***Step 4 – Create a Topic in Kafka***
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic testTopic
The replication-factor describes how many copies of data will be created. As we are running with single instance keep this value 1.

Set the partitions options as the number of brokers you want your data to be split between. As we are running with a single broker keep this value 1.

Now you can see the created topic on Kafka by runnin gthe list topic command
```bash
$ bin/kafka-topics.sh --list --zookeeper localhost:2181
```

***Step 5 – Send Messages to Kafka***
The “producer” is the process responsible for put data into our Kafka. The Kafka comes with a command line client that will take input from a file or from standard input and send it out as messages to the Kafka cluster. The default Kafka send each line as a separate message.

Let’s run the producer and then type a few messages into the console to send to the server.
```bash
$ bin/kafka-console-producer.sh --broker-list localhost:9092 --topic testTopic
```
You can exit this command or keep this terminal running for further testing. Now open a new terminal to the Kafka consumer process on next step.

***Step 6 – Using Kafka Consumer***
Kafka also has a command line consumer to read data from Kafka cluster and display messages to standard output.
```bash
$ bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic testTopic --from-beginning
```




## Install Elastic search on ubuntu
### 1.Dependencies
First, update the list of available packages by running apt-get update.
Install the OpenJDK runtime supplied by Ubuntu.

### OpenJDK
To accomplish the first option, we can simply run apt-get install openjdk-6-jre.

### Test your Java installation
You can then check that Java is installed by running java -version.

That’s all the dependencies we need for now, so let’s get started with obtaining and installing Elasticsearch.

### 2.Download and Install
Elasticsearch can be downloaded directly from their site in zip, tar.gz, deb, or rpm packages. You don’t need to do this ahead of time, as we will download the files that we need as we need them in the text below.


### Download the archive
```bash
$ wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-0.90.7.zip
```
```bash
$ unzip elasticsearch-0.90.7.zip
```

### Configuration files
If installed from the zip, configuration files are found in the config folder of the resulting directory.

In either case, there will be two main configuration files: elasticsearch.yml and logging.yml. The first configures the Elasticsearch server settings, and the latter, unsurprisingly, the logger settings used by Elasticsearch.

“elasticsearch.yml” will, by default, contain nothing but comments.

“logging.yml” provides configuration for basic logging. You can find the resulting logs in /var/log/elasticsearch.

### Remove Elasticsearch Public Access
Before continuing, you will want to configure Elasticsearch so it is not accessible to the public Internet–Elasticsearch has no built-in security and can be controlled by anyone who can access the HTTP API. This can be done by editing elasticsearch.yml. Assuming you installed with the package, open the configuration with this command:

```bash
$ sudo vi /etc/elasticsearch/elasticsearch.yml
```

Then find the line that specifies network.bind_host, then uncomment it and change the value to localhost so it looks like the following:
```bash
network.bind_host: localhost
```
Then insert the following line somewhere in the file, to disable dynamic scripts:
```bash
script.disable_dynamic: true
```
Save and exit. Now restart Elasticsearch to put the changes into effect:
```bash
$ sudo service elasticsearch restart
```


### 3.Test your Elasticsearch install
You have now extracted the zip to a directory and have the Elasticsearch binaries available, and can start the server.Make sure you’re in the resulting directory. 

Let’s ensure that everything is working. Run
```bash
$ ./bin/elasticsearch
 ```
Elasticsearch should now be running on port 9200. Do note that Elasticsearch takes some time to fully start, so running the curl command below immediately might fail. It shouldn’t take longer than ten seconds to start responding, so if the below command fails, something else is likely wrong.

Ensure the server is started by running
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
If you see a response similar to the one above, Elasticsearch is working properly. Alternatively, you can query your install of Elasticsearch from a browser by visiting :9200. You should see the same JSON as you saw when using curl above.

The server can be stopped using the RESTful API
```bash
$curl -X POST 'http://localhost:9200/_cluster/nodes/_local/_shutdown'
```
You can restart the server with the corresponding service elasticsearch start.

### 4.Using Elasticsearch
Elasticsearch is up and running. Now, we’ll go over some basic configuration and usage.

### Basic configuration
Configuration files are found in the config folder inside the resulting directory.The two configuration files you will find are elasticsearch.yml and logging.yml. The first is a general Elasticsearch configuration. The provided file contains nothing but comments, so default settings are used. None of the settings are necessary. You can work with Elasticsearch without doing any of the following, but it’ll be a raw development environment.

The setting “cluster.name” is the method by which Elasticsearch provides auto-discovery. What this means is that if a group of Elasticsearch servers on the same network share the same cluster name, they will automatically discover each other. This is how simple it is to scale Elasticsearch, but be aware that if you keep the default cluster name and there are other Elasticsearch servers on your network that are not under your control, you are likely to wind up in a bad state.

### Basic usage
Let’s add some data to our Elasticsearch install. Elasticsearch uses a RESTful API, which responds to the usual CRUD commands: Create, Read, Update, and Destroy.

To add an entry
```bash
$ curl -X POST 'http://localhost:9200/tutorial/helloworld/1' -d '{ "message": "Hello World!" }'
```
You should see the following response
```
{“ok”:true,“index”:“tutorial”,“type”:“helloworld”,“id”:“1”,“version”:1}
```
What we have done is send a HTTP POST request to the Elasticserach server. The URI of the request was /tutorial/helloworld/1. It’s important to understand the parameters here:

“tutorial” is index of the data in Elasticsearch.
“helloworld” is the type.
“1” is the id of our entry under the above index and type.
If you saw the response above to the curl command, we can now query for the data with

```bash
$ curl -X GET 'http://localhost:9200/tutorial/helloworld/1'
```
which should respond with
```
{"_index":"tutorial","_type":"helloworld","_id":"1","_version":1,"exists":true, "_source" : { "message": "Hello World!" }}
Success! We’ve added to and queried data in Elasticsearch.
```
One thing to note is that we can get nicer output by appending ?pretty=true to the query. Let’s give this a try
```bash
curl -X GET 'http://localhost:9200/tutorial/helloworld/1?pretty=true'
```
Which should respond with
```
{
  "_index" : "tutorial",
  "_type" : "helloworld",
  "_id" : "1",
  "_version" : 1,
  "exists" : true, "_source" : { "message": "Hello World!" }
}
```
which is much more readable. The output will also be pretty printed without needing to append the query string if you have set format=yaml in the Elasticsearch configuration file.

### Conclusion
We have now installed, configured and begun using Elasticsearch. Since it responds to a basic RESTful API. It is now easy to begin adding to and querying data using Elasticsearch from your application.
