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
package com.github.knguyen.processors.geneva.argument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.github.knguyen.processors.geneva.BaseExecuteGeneva;

class StandardRunrepArgumentProviderTest {

    private final String MOCK_FLOW_UUID = "c480d5a6-9400-11ee-b9d1-0242ac120002";

    @Mock
    private ProcessSession session;

    @Mock
    private ProcessContext context;

    @Mock
    private FlowFile flowfile;

    private StandardRunrepArgumentProvider provider;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        provider = new StandardRunrepArgumentProvider(session, context, flowfile);

        mockPropertyWithGivenValue(BaseExecuteGeneva.RUNREP_USERNAME, "user");
        mockPropertyWithGivenValue(BaseExecuteGeneva.RUNREP_PASSWORD, "password");
        mockPropertyWithGivenValue(BaseExecuteGeneva.PORTFOLIO_LIST, "123,\\\"My Portfolio\\\",456");
        mockPropertyWithGivenValue(BaseExecuteGeneva.ACCOUNTING_RUN_TYPE,
                BaseExecuteGeneva.CLOSED_PERIOD_ACCOUNTING.getValue());
        mockPropertyWithGivenValue(BaseExecuteGeneva.PERIOD_START_DATE, "2023-01-01T00:00:00");
        mockPropertyWithGivenValue(BaseExecuteGeneva.PERIOD_END_DATE, "2023-01-31T23:59:59");
        mockPropertyWithGivenValue(BaseExecuteGeneva.KNOWLEDGE_DATE, "2023-02-28T23:59:59");
        mockPropertyWithGivenValue(BaseExecuteGeneva.PRIOR_KNOWLEDGE_DATE, "2022-12-01T00:00:00");
        mockPropertyWithGivenValue(BaseExecuteGeneva.REPORT_OUTPUT_FORMAT, "csv");
    }

    private void mockPropertyWithGivenValue(PropertyDescriptor propertyDescriptor, String value) {
        final var propertyValue = Mockito.mock(PropertyValue.class);
        when(context.getProperty(propertyDescriptor)).thenReturn(propertyValue);
        when(propertyValue.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue);
        when(propertyValue.getValue()).thenReturn(value);
    }

    @Test
    void testControlCaseValidArgumentsDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> provider.validate());
    }

    @Test
    void testValidateThrowsExceptionForBlankUsername() {
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        // Mocking to return a blank username
        when(context.getProperty(BaseExecuteGeneva.RUNREP_USERNAME)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("");

        // Asserting that IllegalArgumentException is thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.validateUserCredentials());
    }

    @Test
    void testValidateThrowsExceptionForBlankPasword() {
        final var propertyValue2 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.RUNREP_PASSWORD)).thenReturn(propertyValue2);
        when(propertyValue2.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue2);
        when(propertyValue2.getValue()).thenReturn("");

        // Asserting that IllegalArgumentException is thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.validateUserCredentials());
    }

    @Test
    void testValidUsernamePasswordDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> provider.validateUserCredentials());
    }

    @Test
    void testValidatePassesDynamicAccounting() {
        final var propertyValue0 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.ACCOUNTING_RUN_TYPE)).thenReturn(propertyValue0);
        when(propertyValue0.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue0);
        when(propertyValue0.getValue()).thenReturn(BaseExecuteGeneva.DYNAMIC_ACCOUNTING.getValue());

        final var propertyValue3 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.KNOWLEDGE_DATE)).thenReturn(propertyValue3);
        when(propertyValue3.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue3);
        when(propertyValue3.getValue()).thenReturn("");

        final var propertyValue4 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PRIOR_KNOWLEDGE_DATE)).thenReturn(propertyValue4);
        when(propertyValue4.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue4);
        when(propertyValue4.getValue()).thenReturn("");

        Assertions.assertDoesNotThrow(() -> provider.validateDateLogic());
    }

    @Test
    void testInvalidStartAndEndDateBecauseStartDateAfterEndDate() {
        final var propertyValue0 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.ACCOUNTING_RUN_TYPE)).thenReturn(propertyValue0);
        when(propertyValue0.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue0);
        when(propertyValue0.getValue()).thenReturn(BaseExecuteGeneva.DYNAMIC_ACCOUNTING.getValue());

        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PERIOD_START_DATE)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("2023-01-31T23:59:59");

        final var propertyValue2 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PERIOD_END_DATE)).thenReturn(propertyValue2);
        when(propertyValue2.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue2);
        when(propertyValue2.getValue()).thenReturn("2023-01-01T00:00:00");

        final var propertyValue3 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.KNOWLEDGE_DATE)).thenReturn(propertyValue3);
        when(propertyValue3.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue3);
        when(propertyValue3.getValue()).thenReturn("");

        final var propertyValue4 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PRIOR_KNOWLEDGE_DATE)).thenReturn(propertyValue4);
        when(propertyValue4.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue4);
        when(propertyValue4.getValue()).thenReturn("");

        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.validateDateLogic());
    }

    @Test
    void testClosedPeriodValidDates() {
        final var propertyValue3 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.KNOWLEDGE_DATE)).thenReturn(propertyValue3);
        when(propertyValue3.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue3);
        when(propertyValue3.getValue()).thenReturn("2023-05-01T23:59:59");

        Assertions.assertDoesNotThrow(() -> provider.validateDateLogic());
    }

    @Test
    void testClosedPeriodButNoPriorKnowledgeDate() {
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PERIOD_START_DATE)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("2023-01-31T23:59:59");

        final var propertyValue2 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PERIOD_END_DATE)).thenReturn(propertyValue2);
        when(propertyValue2.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue2);
        when(propertyValue2.getValue()).thenReturn("2023-01-01T00:00:00");

        final var propertyValue3 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.KNOWLEDGE_DATE)).thenReturn(propertyValue3);
        when(propertyValue3.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue3);
        when(propertyValue3.getValue()).thenReturn("");

        final var propertyValue4 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PRIOR_KNOWLEDGE_DATE)).thenReturn(propertyValue4);
        when(propertyValue4.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue4);
        when(propertyValue4.getValue()).thenReturn("");

        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.validateDateLogic());
    }

    @Test
    void testFileNameFromDirectoryPath() {
        final var propertyValue0 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_DIRECTORY)).thenReturn(propertyValue0);
        when(propertyValue0.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue0);
        when(propertyValue0.getValue()).thenReturn(System.getProperty("java.io.tmpdir"));

        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_PATH)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("");

        when(flowfile.getAttribute(CoreAttributes.UUID.key())).thenReturn(MOCK_FLOW_UUID);

        assertEquals(System.getProperty("java.io.tmpdir") + "/c480d5a6-9400-11ee-b9d1-0242ac120002.csv",
                provider.getOutputPath());
    }

    @Test
    void testFilenameFromFilePath() {
        final var propertyValue0 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_DIRECTORY)).thenReturn(propertyValue0);
        when(propertyValue0.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue0);
        when(propertyValue0.getValue()).thenReturn("");

        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_PATH)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("C:/my user/my filename.csv");

        when(flowfile.getAttribute(CoreAttributes.UUID.key())).thenReturn(MOCK_FLOW_UUID);

        assertEquals("C:/my user/my filename.csv", provider.getOutputPath());
    }

    @Test
    void testProvideNonDefaultFormatAndADirectory() {
        // path must be blank in order to make use of directory
        final var propertyValue2 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_PATH)).thenReturn(propertyValue2);
        when(propertyValue2.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue2);
        when(propertyValue2.getValue()).thenReturn("");

        final var propertyValue0 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_DIRECTORY)).thenReturn(propertyValue0);
        when(propertyValue0.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue0);
        when(propertyValue0.getValue()).thenReturn("/tmp/me");

        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_FORMAT)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("json");

        when(flowfile.getAttribute(CoreAttributes.UUID.key())).thenReturn(MOCK_FLOW_UUID);

        assertEquals("/tmp/me/c480d5a6-9400-11ee-b9d1-0242ac120002.json", provider.getOutputPath());
    }
}
