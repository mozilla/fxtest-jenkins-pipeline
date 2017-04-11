package org.mozilla.fxtest

@Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.8.8')
import com.fasterxml.jackson.databind.ObjectMapper

@Grab(group='com.github.fge', module='json-schema-validator', version='2.2.5')
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.github.fge.jsonschema.main.JsonSchemaFactory

@NonCPS
def validate(payload, schema) {
  def factory = JsonSchemaFactory.byDefault()
  def jsonSchema = factory.getJsonSchema(JsonLoader.fromString(schema));
  def mapper = new ObjectMapper()
  def instance = mapper.readTree(payload)
  def report = jsonSchema.validate(instance)
  if ( !report.isSuccess() ) {
    for ( message in report ) {
      echo "$message"
    }
    throw new ProcessingException('Failure validating Pulse payload against schema.')
  } else {
    echo 'Sucessfully validated Pulse payload against schema.'
  }
}
