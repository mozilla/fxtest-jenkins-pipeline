/** Publish message to a Pulse exchange
 *
 * @param exchange exchange to send message to
 * @param routingKey routing key to use
 * @param message message to send
 * @param schema optional schema to validate against
*/
def call(String exchange,
         String routingKey,
         String message,
         String schema = null) {
  if ( schema != null ) {
    def jsonSchemaValidator = new org.mozilla.fxtest.JsonSchemaValidator()
    jsonSchemaValidator.validate(message, schema)
  }
  def pulse = new org.mozilla.fxtest.Pulse()
  pulse.publish(exchange, routingKey, message)
}
