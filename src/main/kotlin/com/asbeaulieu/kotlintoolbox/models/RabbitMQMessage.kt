package com.asbeaulieu.kotlintoolbox.models

import java.time.Instant

data class RabbitMQMessage(
    val messageId: Int? = null,
    val message: String,
    val date: Instant? = Instant.now()
)