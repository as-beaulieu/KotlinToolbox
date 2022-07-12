# Running RabbitMQ with Docker

Running an instance of RabbitMQ is possible with docker.

Here is an example using the rabbitmq:3-management image:

```
docker run -d --hostname rabbit-mq-node --name rabbit-mq-instance -p 15672:15672 -p 5672:5672 rabbitmq:3-management
```

Arguments review:

* `-d` runs the container "detached" (Background Mode)
* `--hostname rabbit-mq-node` Names the RabbitMQ node "rabbit-mq-node"
* `--name rabbit-mq-instace` Names the docker image "rabbit-mq-instance"
* `-p 15672:15672` sets the port for the RabbitMQ management console
  * When running, can access console from `http://localhost:15762`
* `-p 5762:5762` sets the port for connecting to the RabbitMQ service
* `rabbitmq:3-management` Name of the docker image to pull

## Setup in project

Add the gradle dependency:

```
implementation 'com.rabbitmq:amqp-client:5.9.0'
```

## Producer