/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.knguyen.processors.utils;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.Validator;

public class CustomValidators {
    private static boolean isValidHostname(String hostname) {
        if (hostname.length() > 255)
            return false;

        if (hostname.endsWith("."))
            hostname = hostname.substring(0, hostname.length() - 1); // strip exactly one dot from the right, if present

        Pattern allowed = Pattern.compile("(?!-)[A-Z\\d-]{1,63}(?<!-)$", Pattern.CASE_INSENSITIVE);
        return Arrays.stream(hostname.split("\\.")).allMatch(s -> allowed.matcher(s).matches());
    }

    /**
     * This class represents a hostname validator implementing the Validator interface. The validator uses a simplified
     * way to validate hostname strings by checking the length, ensuring no trailing periods, and matching to a
     * specified regular expression. see: https://stackoverflow.com/questions/2532053/validate-a-hostname-string
     *
     * @see Validator
     */
    public static final Validator HOSTNAME_VALIDATOR = (subject, input, context) -> {
        // Evaluate Expression Language if present
        final String evaluatedInput;
        if (context.isExpressionLanguageSupported(subject) && context.isExpressionLanguagePresent(input)) {
            evaluatedInput = context.newPropertyValue(input).evaluateAttributeExpressions().getValue();
        } else {
            evaluatedInput = input;
        }

        // Validate the (possibly evaluated) input
        final boolean isValid = isValidHostname(evaluatedInput);
        return new ValidationResult.Builder().subject(subject).input(evaluatedInput).valid(isValid)
                .explanation(isValid ? "Valid hostname" : "Invalid hostname").build();
    };

    /**
     * A pre-configured instance of {@code DateTimeValidator} with Expression Language support enabled.
     *
     * This static final field provides a readily available instance of {@code DateTimeValidator} that is configured to
     * evaluate and validate date-time strings with Expression Language. It simplifies the process of utilizing a
     * {@code DateTimeValidator} in scenarios where Expression Language is expected and should be processed.
     *
     * By having Expression Language support enabled, this validator can evaluate strings that contain dynamic
     * expressions in addition to validating their format as date-time strings. This is particularly useful in contexts
     * where the flexibility to handle both static date-time strings and those constructed through Expression Language
     * is needed.
     *
     * Example usage:
     *
     * <pre>{@code
     * ValidationResult result = DateTimeValidator.DATETIME_VALIDATOR.validate("dateTimeField", "2023-12-01T15:00:00",
     *         validationContext);
     * }</pre>
     *
     * This instance simplifies usage by negating the need to explicitly create a new instance of
     * {@code DateTimeValidator} with Expression Language enabled each time date-time validation is required.
     */
    public static final Validator DATETIME_VALIDATOR = new DateTimeValidator(true);

    public static final Validator PORTFOLIO_LIST_VALIDATOR = (subject, input, context) -> {
        boolean isValid = true;
        String explanation = null;

        if (input != null && !input.isEmpty()) {
            String[] portfolios = input.split(",");
            for (String portfolio : portfolios) {
                portfolio = portfolio.trim();
                if (portfolio.contains(" ") && !(portfolio.startsWith("\\\"") && portfolio.endsWith("\\\""))) {
                    isValid = false;
                    explanation = "Portfolio names containing spaces must be enclosed within escaped quotes.";
                    break;
                }
            }
        }

        return new ValidationResult.Builder().subject(subject).input(input).valid(isValid).explanation(explanation)
                .build();
    };

    public static final Validator DIRECTORY_EXISTS_FROM_PATH_VALIDATOR = new DirectoryExistsValidator(true, true);

    public static class DirectoryExistsValidator implements Validator {

        private final boolean allowEL;
        private final boolean create;

        public DirectoryExistsValidator(final boolean allowExpressionLanguage, final boolean create) {
            this.allowEL = allowExpressionLanguage;
            this.create = create;
        }

        @Override
        public ValidationResult validate(final String subject, final String value, final ValidationContext context) {
            if (context.isExpressionLanguageSupported(subject) && context.isExpressionLanguagePresent(value)) {
                return new ValidationResult.Builder().subject(subject).input(value)
                        .explanation("Expression Language Present").valid(true).build();
            }

            final String substituted;
            if (allowEL) {
                try {
                    substituted = context.newPropertyValue(value).evaluateAttributeExpressions().getValue();
                } catch (final Exception e) {
                    return new ValidationResult.Builder().subject(subject).input(value).valid(false)
                            .explanation("Not a valid Expression Language value: " + e.getMessage()).build();
                }

                if (substituted.trim().isEmpty() && !value.trim().isEmpty()) {
                    // User specified an Expression and nothing more... assume valid.
                    return new ValidationResult.Builder().subject(subject).input(value).valid(true).build();
                }
            } else {
                substituted = value;
            }

            String path = substituted;
            // If path has an extension, extract the directory component
            if (path.contains(".")) {
                path = new File(path).getParent();
            }

            String reason = null;
            try {
                final File file = new File(path);
                if (!file.exists()) {
                    if (!create) {
                        reason = "Directory does not exist";
                    } else if (!file.mkdirs()) {
                        reason = "Directory does not exist and could not be created";
                    }
                } else if (!file.isDirectory()) {
                    reason = "Path does not point to a directory";
                }
            } catch (final Exception e) {
                reason = "Value is not a valid directory name";
            }

            return new ValidationResult.Builder().subject(subject).input(value).explanation(reason)
                    .valid(reason == null).build();
        }
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * This utility class is not meant to be instantiated. It provides utility methods for String.
     */
    private CustomValidators() {
    }
}
