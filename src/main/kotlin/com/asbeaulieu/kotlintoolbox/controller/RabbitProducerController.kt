package com.asbeaulieu.kotlintoolbox.controller

import com.asbeaulieu.kotlintoolbox.models.RabbitMQMessage
import com.rabbitmq.client.ConnectionFactory
import org.springframework.web.bind.annotation.*
import java.nio.charset.StandardCharsets
import java.time.Instant

@RestController
@RequestMapping("/rabbit")
class RabbitProducerController {

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
                    "test_queue", //routing key cannot be null
                    null,
                    message.toString().toByteArray(StandardCharsets.UTF_8)
                )
                println("Published message: ${message}")
            }
        }

        return rabbitMQMessage
    }
}