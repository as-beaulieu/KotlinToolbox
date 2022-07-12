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
* 