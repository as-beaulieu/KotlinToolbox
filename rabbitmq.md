# RabbitMQ

## Benefits over RPC

RPC: (Remote Procedure Call)
* One service calls another specific service directly
* Fast, usually in binary
* But does couple those services together
  * Usually a protobuff or contract on specific shape of message
  * Has to send to a specific address (Url or IP)
  * In microservices, need to have awareness of and name of any and all
instances of the arrival microservice to send to the right one

Message Based System:

Advantages:
* Put a message broker in the middle
  * Sender sends the message to the broker
    * Broker configured to send message to receiver
    * Sender no longer need updating about any/all receivers
  * Multiple receivers are possible
    * Messages can even be duplicated if needed
    * Easily add new receivers
  * Queuing messages are possible
    * If processing is slow, messages will wait to get picked up
    * If any/all receivers are down, messages won't be lost
  * Decouples services further
    * Services only need to know the minimum amount about other services

Disadvantages: 
* Communication is now asynchronous
* The message broker is a crucial integration point of application
  * Can have multiple instances of RabbitMQ running for redundancy (Clustering)
* Increased network traffic

## Protocols in RabbitMQ

* AMQP 0-9-1 (Advanced Message Queuing Protocol v0.9.1)
  * Most popular and widly supported
* STOMP (Simple Text Oriented Message Protocol)
* MQTT (Message Queuing Telemetry Transport)
* AMQP 1.0 (Advanced Message Queuing Protocol v1)

### AMQP

* Binary Protocol

A contrast example: HTTP

```
GET /files/image.jpg HTTP/1.0
Connection: Keep-Alive
User-Agent: Mozilla/4.01 [en] (Win95: I)
Host: google.com
Accept: image/gif, image/x-xbitmap, image/jpeg
Accept-Language: en
Accept-Charset: iso-8859-1, *, utf-8

Response is much the same
<binary data here>
```

In case of a binary protocol, field names are not transmitted

Receiving side can find data from specific locations that have
been agreed upon

A single frame of AMQP transfer broken into bytes
* type 
  * Method
    * Basic.Publish
  * HEADER
    * body size
  * BODY
    * binary data
  * HEARTBEAT
    * Just to signal that sender is still alive
* channel
  * a channel is a virtual connection
    * in AMQP, a client can have a single physical connection
to message broker
      * But can use that connection for multiple threads
      * Each thread gets its own channel
* size
* payload
  * multiple bytes to contain the payload
* frame-end
  * single byte signaling end of the frame

A single message can contain multiple frames

Example message:

```
METHOD  (channel) (size) "Basic.Publish"  (frame-end)
HEADER  (channel) (size) <body size>      (frame-end)
BODY    (channel) (size) <binary data>    (frame-end)
BODY    (channel) (size) <binary data>    (frame-end)
BODY    (channel) (size) <binary data>    (frame-end)
```

## AMQ Model

Model of how RabbitMQ is constructed

### Publisher/Producer

Publisher connects with a message broker

Publisher sends the message to an exchange (can send a routing key with the message)

The exchange sends the message onto the queues
* The exchange will use certain rules (bindings) to determine which queues to route to

The message is sent from the queues to a subscribed Consumer
* It's also possible to have the consumers pull/fetch the messages on demand

Multiple applications can subscribe to a queue, but only one will receive the message

Consumer will send acknowledgment to broker, for receipt of message and to notify
broker to remove the message

## Queue and Exchange Configuration

Queues and Exchanges are Durable or Transient
* A Durable Queue will survive a RabbitMQ restart
* A Transient Queue will not
* This does not mean that any underlying messages are persisted
  * Unless persistence is specified, messages are kept in memory
* Can also tell a Queue to auto-delete if no consumers are connected

## Exchange Types

All Exchange types will handle routing keys differently

* Direct
* Fanout
* Topic
* Headers

### Direct

* Will route messages to all queues connected to the direct exchange
with the same routing key that the publisher used
  * Example: Publisher used routing key `customer.purchase`
    * Two Queues in the Direct Exchange, `customer.registration` and `customer.purchase`
    * Exchange will route the message to the customer.purchase queue

### Fanout

* Messages are routed to all bound queues
  * Every queue will receive a copy of the message
* Routing key is ignored

### Topic

* Topic Exchanges route based on patterns in the routing key
  * Example: add to message a routing key: `customer.purchase.cancelled`
    * If a Queue had a binding with a hash `#` (`customer.#`), will match anything > 0
    * If a Queue had an asterisk `*` (`*.purchase.cancelled`), will match a single word
    * Will not go to `premiumcustomer.#`

### Headers

* Ignore routing key, instead looking at headers sent with message
  * Example: Publisher sent a message with headers: `entity: order, action: cancelled`
    * Need to decide if queues want to match any or all headers
    * A Queue with binding arguments will route
      * entity = order
      * action = cancelled
      * x-match = all (This means all headers must match)
    * This queue with these bindings will not route
      * entity = order
      * action = confirmed
      * x-match = all
    * This Queue will route
      * entity = order
      * action = something
      * x-match = any (This means at least one must match)

### Exchange type use cases

It's the consumers that will tell you best which to use

#### Fanout

* A service annoucement that needs to be sent to all connected mobile devices
* A game that needs to update leaderboard updates
* Distributed Systems that notify components of configuration chagnes

#### Direct
