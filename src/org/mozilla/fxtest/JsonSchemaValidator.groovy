package org.mozilla.fxtest

@Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.8.8')
import com.fasterxml.jackson.databind.ObjectMapper

@Grab(group='com.fasterxml.jackson.dataformat', module='jackson-dataformat-yaml', version='2.8.3')
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

@Grab(group='com.github.fge', module='json-schema-validator', version='2.2.5')
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.github.fge.jsonschema.main.JsonSchemaFactory

@NonCPS
def validate(payload, schema) {
  def mapper = new ObjectMapper()
  def yamlFactory = new YAMLFactory()
  def jsonSchemaFactory = JsonSchemaFactory.byDefault()
  def schemaJsonNode = mapper.readTree(yamlFactory.createParser(schema))
  def jsonSchema = jsonSchemaFactory.getJsonSchema(schemaJsonNode)
  def payloadJsonNode = mapper.readTree(payload)
  def report = jsonSchema.validate(payloadJsonNode)
  if ( !report.isSuccess() ) {
    for ( message in report ) {
      echo "$message"
    }
    throw new ProcessingException('Failure validating Pulse payload against schema.')
  } else {
    echo 'Sucessfully validated Pulse payload against schema.'
  }
}
