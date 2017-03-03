package org.mozilla.fxtest

@Grab(group='com.rabbitmq', module='amqp-client', version='4.1.0')
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties

@NonCPS
def publish(String exchange, String routingKey, String payload) {
  def factory = new ConnectionFactory()
  factory.setUri("amqp://${PULSE}@pulse.mozilla.org:5671")
  factory.useSslProtocol()
  def connection = factory.newConnection()
  def channel = connection.createChannel()
  channel.exchangeDeclare exchange, 'topic', true

  def properties = new AMQP.BasicProperties.Builder()
    .contentType('application/json')
    .deliveryMode(2)
    .build()

  channel.basicPublish exchange, routingKey, properties, payload.bytes
  echo "Published payload to Pulse on $exchange with routing key $routingKey."
  echo payload
  channel.close()
  connection.close()
}
