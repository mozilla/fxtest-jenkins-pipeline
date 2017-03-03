package org.mozilla.fxtest

import org.jenkinsci.plugins.pipeline.modeldefinition.shaded.com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.exceptions.ProcessingException
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.util.JsonLoader

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
