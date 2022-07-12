package com.asbeaulieu.rabbitmqconsumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RabbitConsumerApplication

fun main(args: Array<String>) {
    runApplication<RabbitConsumerApplication>(*args)
}