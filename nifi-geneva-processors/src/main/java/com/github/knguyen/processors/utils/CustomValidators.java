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

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.Validator;
import org.apache.nifi.processor.util.StandardValidators;

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
    public static final Validator HOSTNAME_VALIDATOR = new Validator() {
        /**
         * Validates a provided hostname string in a particular validation context, with support for Expression
         * Language. If the hostname string is valid, it returns a ValidationResult with valid set to true. If the
         * hostname string is invalid, it returns a ValidationResult with valid set to false. If the input contains
         * Expression Language, it is evaluated before validation.
         *
         * @param subject
         *            The subject of validation, usually the name or type of the data being validated.
         * @param input
         *            The hostname string to validate. This can be a plain string or an Expression Language statement.
         * @param context
         *            The context of validation, providing additional information needed for validation.
         *
         * @return A ValidationResult that includes the subject, input, validation result (valid/invalid), and an
         *         explanation.
         */
        @Override
        public ValidationResult validate(String subject, String input, ValidationContext context) {
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
        }
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
}
