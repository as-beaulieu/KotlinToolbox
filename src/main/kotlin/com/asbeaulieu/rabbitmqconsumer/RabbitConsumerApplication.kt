package com.asbeaulieu.rabbitmqconsumer

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.nio.charset.StandardCharsets

@SpringBootApplication
class RabbitConsumerApplication

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