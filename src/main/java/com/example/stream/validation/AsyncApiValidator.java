package com.example.stream.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Set;

public class AsyncApiValidator {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AsyncApiValidator.class);

    private final ObjectMapper mapper =
            new ObjectMapper();

    private final JsonSchema schema;

    public AsyncApiValidator() {

        try {

            InputStream schemaInput =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream(
                                    "cdc-event-schema.json");

            if (schemaInput == null) {

                throw new RuntimeException(
                        "cdc-event-schema.json not found");
            }

            JsonSchemaFactory factory =
                    JsonSchemaFactory.getInstance(
                            SpecVersion.VersionFlag.V202012);

            schema =
                    factory.getSchema(
                            schemaInput);

            log.info(
                    "JSON Schema loaded successfully");

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Unable to load schema",
                    exception);
        }
    }

    public boolean validate(
            String message) {

        try {

            JsonNode node =
                    mapper.readTree(
                            message);

            Set<ValidationMessage> errors =
                    schema.validate(
                            node);

            if (!errors.isEmpty()) {

                errors.forEach(
                        error ->
                                log.warn(
                                        error.getMessage()));

                return false;
            }

            return true;

        } catch (Exception exception) {

            log.error(
                    "Validation failed",
                    exception);

            return false;
        }
    }
}