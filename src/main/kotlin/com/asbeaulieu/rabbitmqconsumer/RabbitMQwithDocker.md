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

The producer is setup from the com.rabbitmq.client.ConnectionFactory package

```kotlin
@PostMapping("/{messageId}")
    fun publishMessage(@PathVariable("messageId") messageId: Int, @RequestBody messageBody: RabbitMQMessage): RabbitMQMessage? {
        val rabbitMQMessage = RabbitMQMessage(messageId = messageId, message = messageBody.message, date = Instant.now())

        val factory = ConnectionFactory()
        factory.newConnection("amqp://guest:guest@localhost:5672/").use { connection ->
            connection.createChannel().use { channel ->
                channel.queueDeclare("test_queue", false, false, false, null)
                val message = rabbitMQMessage
                channel.basicPublish(
                    "",
                    "", //routing key cannot be null
                    null,
                    message.toString().toByteArray(StandardCharsets.UTF_8)
                )
                println("Published message: ${message}")
            }
        }

        return rabbitMQMessage
    }
```

## Consumer

The consumer operates much in the same way to connect to the message broker

```kotlin
fun main(args: Array<String>) {
    val factory = ConnectionFactory()
    val connection = factory.newConnection("amqp://guest:guest@localhost:5672/")
    val channel = connection.createChannel()
    val consumerTag = "SimpleConsumer"

    channel.queueDeclare("test_queue", false, false, false, null)

    println("[$consumerTag] Waiting for messages...")

    val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
        val message = String(delivery.body, StandardCharsets.UTF_8)
        println("[$consumerTag] Received message: '$message'")
    }

    val cancelCallback = CancelCallback {consumerTag: String? ->
        println("[$consumerTag] was cancelled")
    }

    channel.basicConsume(
        "test_queue",
        true,
        consumerTag,
        deliverCallback,
        cancelCallback
    )
}
```

## Running the program. 

1) Start docker container for rabbitMQ manager

2) open browser to http://localhost:15762 to confirm message
broker is up

3) Run KotlinToolboxApplication

4) Run RabbitConsumerApplication

5) Use Postman, Insomnia, or CUrl operation:

```curl
curl --location --request POST 'localhost:8080/rabbit/46' \
--header 'Content-Type: application/json' \
--data-raw '{
    "message": "Hello there!"
}'
```

6) Check console logs and RabbitMQ manager
   1) KotlinToolboxApplication should log publishing of message
   2) RabbitMQ manager should show traffic of message
   3) RabbitConsumerApplication should log the message as it's consumed

