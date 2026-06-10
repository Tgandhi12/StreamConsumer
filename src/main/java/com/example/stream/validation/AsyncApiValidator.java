package com.example.stream.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

public class AsyncApiValidator {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AsyncApiValidator.class);

    private final ObjectMapper mapper =
            new ObjectMapper();

    private final Set<String> requiredFields =
            new HashSet<>();

    private final Set<String> allowedOperations =
            new HashSet<>();

    public AsyncApiValidator() {

        try (InputStream input =
                     getClass()
                             .getClassLoader()
                             .getResourceAsStream("asyncapi.yaml")) {

            if (input == null) {

                throw new RuntimeException(
                        "asyncapi.yaml not found");
            }

            loadSchema(input);

            log.info(
                    "AsyncAPI loaded successfully");

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load AsyncAPI",
                    e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadSchema(InputStream input) {

        Yaml yaml = new Yaml();

        Map<String, Object> root =
                yaml.load(input);

        Map<String, Object> components =
                (Map<String, Object>)
                        root.get("components");

        /*
         * Resolve:
         *
         * components
         *   -> messages
         *      -> CdcEvent
         *          -> payload
         *              -> $ref
         */

        Map<String, Object> messages =
                (Map<String, Object>)
                        components.get("messages");

        Map<String, Object> cdcEvent =
                (Map<String, Object>)
                        messages.get("CdcEvent");

        Map<String, Object> payloadRef =
                (Map<String, Object>)
                        cdcEvent.get("payload");

        String ref =
                payloadRef.get("$ref")
                        .toString();

        String schemaName =
                ref.substring(
                        ref.lastIndexOf("/") + 1);

        log.info(
                "Resolved schema: {}",
                schemaName);

        Map<String, Object> schemas =
                (Map<String, Object>)
                        components.get("schemas");

        Map<String, Object> payload =
                (Map<String, Object>)
                        schemas.get(schemaName);

        if (payload == null) {

            throw new RuntimeException(
                    "Schema not found: "
                            + schemaName);
        }

        List<String> required =
                (List<String>)
                        payload.get("required");

        if (required != null) {

            requiredFields.addAll(required);

            log.info(
                    "Required fields: {}",
                    requiredFields);
        }

        Map<String, Object> properties =
                (Map<String, Object>)
                        payload.get("properties");

        if (properties != null
                && properties.containsKey("op_type")) {

            Map<String, Object> opType =
                    (Map<String, Object>)
                            properties.get("op_type");

            List<String> enums =
                    (List<String>)
                            opType.get("enum");

            if (enums != null) {

                allowedOperations.addAll(enums);

                log.info(
                        "Allowed operations: {}",
                        allowedOperations);
            }
        }
    }

    public boolean validate(String message) {

        try {

            JsonNode node =
                    mapper.readTree(message);

            for (String field : requiredFields) {

                if (!node.has(field)) {

                    log.warn(
                            "Validation failed. Missing required field: {}",
                            field);

                    return false;
                }
            }

            if (node.has("op_type")) {

                String operation =
                        node.get("op_type")
                                .asText();

                if (!allowedOperations.isEmpty()
                        && !allowedOperations.contains(operation)) {

                    log.warn(
                            "Validation failed. Invalid operation: {}",
                            operation);

                    return false;
                }
            }

            return true;

        } catch (Exception e) {

            log.error(
                    "Invalid JSON payload",
                    e);

            return false;
        }
    }
}