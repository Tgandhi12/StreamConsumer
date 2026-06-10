package com.example.stream.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class AsyncApiValidator {

    private final ObjectMapper mapper =
            new ObjectMapper();

    private final Map<String, Object> asyncApiSpec;

    public AsyncApiValidator() {

        try (InputStream input =
                     getClass()
                             .getClassLoader()
                             .getResourceAsStream("asyncapi.yaml")) {

            if (input == null) {
                throw new RuntimeException(
                        "asyncapi.yaml not found");
            }

            Yaml yaml = new Yaml();

            asyncApiSpec = yaml.load(input);

            System.out.println(
                    "AsyncAPI Loaded Successfully");

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load AsyncAPI",
                    e);
        }
    }

    public boolean validate(String message) {

        try {

            JsonNode node =
                    mapper.readTree(message);

            if (!node.has("table")) {

                System.out.println(
                        "Validation Failed: table missing");

                return false;
            }

            if (!node.has("op_type")) {

                System.out.println(
                        "Validation Failed: op_type missing");

                return false;
            }

            return true;

        } catch (Exception e) {

            System.out.println(
                    "Invalid JSON");

            return false;
        }
    }
}