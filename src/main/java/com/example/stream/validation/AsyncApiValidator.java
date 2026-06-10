package com.example.stream.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

public class AsyncApiValidator {

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

            System.out.println(
                    "AsyncAPI Loaded Successfully");

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
                (Map<String, Object>) root.get("components");

        Map<String, Object> schemas =
                (Map<String, Object>) components.get("schemas");

        Map<String, Object> payload =
                (Map<String, Object>)
                        schemas.get("CdcEventPayload");

        List<String> required =
                (List<String>)
                        payload.get("required");

        if (required != null) {
            requiredFields.addAll(required);
        }

        Map<String, Object> properties =
                (Map<String, Object>)
                        payload.get("properties");

        Map<String, Object> opType =
                (Map<String, Object>)
                        properties.get("op_type");

        List<String> enums =
                (List<String>)
                        opType.get("enum");

        if (enums != null) {
            allowedOperations.addAll(enums);
        }
    }

    public boolean validate(String message) {

        try {

            JsonNode node =
                    mapper.readTree(message);

            for (String field : requiredFields) {

                if (!node.has(field)) {

                    System.out.println(
                            "Validation Failed: Missing field "
                                    + field);

                    return false;
                }
            }

            if (node.has("op_type")) {

                String operation =
                        node.get("op_type")
                                .asText();

                if (!allowedOperations.contains(operation)) {

                    System.out.println(
                            "Validation Failed: Invalid operation "
                                    + operation);

                    return false;
                }
            }

            return true;

        } catch (Exception e) {

            System.out.println(
                    "Invalid JSON");

            return false;
        }
    }
}