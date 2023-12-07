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

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.Validator;

/**
 * Provides validation for date-time strings against the ISO_LOCAL_DATE_TIME format. This class implements the
 * {@code Validator} interface and offers the capability to validate strings representing date and time. The validation
 * can optionally consider Expression Language, depending on the configuration provided at instantiation.
 *
 * The {@code DateTimeValidator} class is capable of handling both simple date-time strings and those that incorporate
 * Expression Language. This dual functionality allows it to be used in a variety of contexts where either strict
 * date-time format validation is required, or where the flexibility to evaluate and validate date-time strings with
 * embedded Expression Language is necessary.
 *
 * If Expression Language is enabled during instantiation, the validator will evaluate such expressions before
 * validating the resultant date-time string. If the Expression Language is disabled or not present in the string, the
 * validator treats the input as a standard date-time string in ISO_LOCAL_DATE_TIME format.
 *
 * Example usage:
 *
 * <pre>{@code
 * DateTimeValidator validator = new DateTimeValidator(true);
 * ValidationResult result = validator.validate("dateTimeField", "2023-12-01T15:00:00", validationContext);
 * }</pre>
 *
 * This class is particularly useful in scenarios where date-time data needs to be validated for correctness and format,
 * such as in configuration settings, data import/export processes, or user input validation in applications where date
 * and time information is critical.
 */
public class DateTimeValidator implements Validator {

    private final boolean allowExpressionLanguage;

    /**
     * Constructs a new {@code DateTimeValidator} with the option to allow or disallow the use of Expression Language.
     *
     * This constructor initializes the {@code DateTimeValidator} with a specified setting for handling Expression
     * Language. If {@code allowExpressionLanguage} is set to true, the validator will evaluate and validate date-time
     * strings considering Expression Language. If set to false, the validator will treat the input as a plain date-time
     * string and will not process Expression Language.
     *
     * @param allowExpressionLanguage
     *            A boolean flag indicating whether Expression Language should be considered in the validation process.
     *            If true, Expression Language is allowed and will be evaluated; if false, Expression Language is not
     *            considered.
     */
    public DateTimeValidator(final boolean allowExpressionLanguage) {
        this.allowExpressionLanguage = allowExpressionLanguage;
    }

    /**
     * Validates if a string is a valid date-time in ISO_LOCAL_DATE_TIME format.
     *
     * @param dateTimeStr
     *            The string to validate.
     *
     * @return true if the string is a valid date-time, false otherwise.
     */
    private static boolean isValidDateTime(String dateTimeStr) {
        try {
            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates the given date-time string according to the ISO_LOCAL_DATE_TIME format, with optional support for
     * Expression Language.
     *
     * This method evaluates whether the provided string represents a valid date and time. If Expression Language is
     * enabled and present in the string, it is first evaluated before performing the validation. The method returns a
     * {@code ValidationResult} indicating whether the validation was successful and, if not, provides an explanation.
     *
     * If the string is a valid date-time or a valid Expression Language expression that results in a valid date-time,
     * the method returns a {@code ValidationResult} with the valid flag set to true. If the string is invalid, either
     * as a date-time or due to an Expression Language evaluation error, the valid flag is set to false, and an
     * explanation is provided.
     *
     * @param subject
     *            The subject of validation, typically the name or identifier of the field being validated.
     * @param value
     *            The date-time string to validate. This can be a plain string in ISO_LOCAL_DATE_TIME format or a string
     *            with embedded Expression Language, depending on the configuration of the validator.
     * @param context
     *            The {@code ValidationContext} providing additional information and functionality needed for evaluating
     *            Expression Language and performing validation.
     *
     * @return A {@code ValidationResult} object containing the result of the validation, including a valid flag and an
     *         explanation in case of an invalid result.
     */
    @Override
    public ValidationResult validate(final String subject, final String value, final ValidationContext context) {
        if (context.isExpressionLanguageSupported(subject) && context.isExpressionLanguagePresent(value)) {
            return new ValidationResult.Builder().subject(subject).input(value)
                    .explanation("Expression Language Present").valid(true).build();
        }

        final String substituted;
        if (allowExpressionLanguage) {
            try {
                substituted = context.newPropertyValue(value).evaluateAttributeExpressions().getValue();
            } catch (final Exception e) {
                return new ValidationResult.Builder().subject(subject).input(value).valid(false)
                        .explanation("Not a valid Expression Language value: " + e.getMessage()).build();
            }
        } else {
            substituted = value;
        }

        if (!isValidDateTime(substituted)) {
            return new ValidationResult.Builder().subject(subject).input(value).valid(false)
                    .explanation("Invalid date-time format").build();
        }

        return new ValidationResult.Builder().subject(subject).input(value).valid(true).build();
    }
}
