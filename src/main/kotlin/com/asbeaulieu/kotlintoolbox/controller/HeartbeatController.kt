package com.asbeaulieu.kotlintoolbox.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/heartbeat")
class HeartbeatController {

    @GetMapping
    fun getHeartbeat() = "Hello!"
}