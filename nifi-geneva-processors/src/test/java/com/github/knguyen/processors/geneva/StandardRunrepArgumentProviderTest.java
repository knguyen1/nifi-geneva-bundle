package com.github.knguyen.processors.geneva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.ProcessContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class StandardRunrepArgumentProviderTest {

    @Mock
    private ProcessContext context;

    @Mock
    private FlowFile flowfile;

    private StandardRunrepArgumentProvider provider;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        provider = new StandardRunrepArgumentProvider(context, flowfile);
    }

    @Test
    void testControlCaseValidArgumentsDoesNotThrow() {
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.RUNREP_USERNAME)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("user");

        final var propertyValue2 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.RUNREP_PASSWORD)).thenReturn(propertyValue2);
        when(propertyValue2.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue2);
        when(propertyValue2.getValue()).thenReturn("pass");

        final var propertyValue3 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PORTFOLIO_LIST)).thenReturn(propertyValue3);
        when(propertyValue3.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue3);
        when(propertyValue3.getValue()).thenReturn("123,\\\"My Portfolio\\\",456");

        final var propertyValue0 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.ACCOUNTING_RUN_TYPE)).thenReturn(propertyValue0);
        when(propertyValue0.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue0);
        when(propertyValue0.getValue()).thenReturn(BaseExecuteGeneva.CLOSED_PERIOD_ACCOUNTING.getValue());

        final var propertyValue4 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PERIOD_START_DATE)).thenReturn(propertyValue4);
        when(propertyValue4.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue4);
        when(propertyValue4.getValue()).thenReturn("2023-01-01T00:00:00");

        final var propertyValue5 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PERIOD_END_DATE)).thenReturn(propertyValue5);
        when(propertyValue5.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue5);
        when(propertyValue5.getValue()).thenReturn("2023-01-31T23:59:59");

        final var propertyValue6 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.KNOWLEDGE_DATE)).thenReturn(propertyValue6);
        when(propertyValue6.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue6);
        when(propertyValue6.getValue()).thenReturn("2023-01-31T23:59:59");

        final var propertyValue7 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PRIOR_KNOWLEDGE_DATE)).thenReturn(propertyValue7);
        when(propertyValue7.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue7);
        when(propertyValue7.getValue()).thenReturn("2022-12-01T00:00:00");

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
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.RUNREP_USERNAME)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("user");

        final var propertyValue2 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.RUNREP_PASSWORD)).thenReturn(propertyValue2);
        when(propertyValue2.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue2);
        when(propertyValue2.getValue()).thenReturn("");

        // Asserting that IllegalArgumentException is thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.validateUserCredentials());
    }

    @Test
    void testValidUsernamePasswordDoesNotThrow() {
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.RUNREP_USERNAME)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("user");

        final var propertyValue2 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.RUNREP_PASSWORD)).thenReturn(propertyValue2);
        when(propertyValue2.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue2);
        when(propertyValue2.getValue()).thenReturn("password");

        Assertions.assertDoesNotThrow(() -> provider.validateUserCredentials());
    }

    @Test
    void testValidatePassesForValidPortfolioList1() {
        // Mocking a valid portfolio list
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PORTFOLIO_LIST)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("123,456");

        Assertions.assertDoesNotThrow(() -> provider.validatePortfolioList());
    }

    @Test
    void testASinglePortfolioIsValid() {
        // Mocking a valid portfolio list
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PORTFOLIO_LIST)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("123");

        Assertions.assertDoesNotThrow(() -> provider.validatePortfolioList());
    }

    @Test
    void testValidatePassesForValidPortfolioList2() {
        // Mocking a valid portfolio list
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PORTFOLIO_LIST)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("123,\\\"My Portfolio\\\",456");

        Assertions.assertDoesNotThrow(() -> provider.validatePortfolioList());
    }

    @Test
    void testInvalidPortfolioList1() {
        // Mocking a valid portfolio list
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PORTFOLIO_LIST)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("123,My Portfolio,456");

        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.validatePortfolioList());
    }

    @Test
    void testInvalidPortfolioList2() {
        // Mocking a valid portfolio list
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PORTFOLIO_LIST)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("123,\\\"My Portfolio,456");

        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.validatePortfolioList());
    }

    @Test
    void testInvalidPortfolioList3() {
        // Mocking a valid portfolio list
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PORTFOLIO_LIST)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("123,My Portfolio\\\",456");

        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.validatePortfolioList());
    }

    @Test
    void testInvalidPortfolioList4() {
        // Mocking a valid portfolio list
        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PORTFOLIO_LIST)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("\\\"123,My Portfolio,456\\\"");

        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.validatePortfolioList());
    }

    @Test
    void testValidPeriodStartDateValidPeriodEndDate() {
        final var propertyValue0 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.ACCOUNTING_RUN_TYPE)).thenReturn(propertyValue0);
        when(propertyValue0.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue0);
        when(propertyValue0.getValue()).thenReturn(BaseExecuteGeneva.DYNAMIC_ACCOUNTING.getValue());

        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PERIOD_START_DATE)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("2023-01-01T00:00:00");

        final var propertyValue2 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PERIOD_END_DATE)).thenReturn(propertyValue2);
        when(propertyValue2.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue2);
        when(propertyValue2.getValue()).thenReturn("2023-01-31T23:59:59");

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
        final var propertyValue0 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.ACCOUNTING_RUN_TYPE)).thenReturn(propertyValue0);
        when(propertyValue0.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue0);
        when(propertyValue0.getValue()).thenReturn(BaseExecuteGeneva.CLOSED_PERIOD_ACCOUNTING.getValue());

        final var propertyValue1 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PERIOD_START_DATE)).thenReturn(propertyValue1);
        when(propertyValue1.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue1);
        when(propertyValue1.getValue()).thenReturn("2023-01-01T00:00:00");

        final var propertyValue2 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PERIOD_END_DATE)).thenReturn(propertyValue2);
        when(propertyValue2.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue2);
        when(propertyValue2.getValue()).thenReturn("2023-01-31T23:59:59");

        final var propertyValue3 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.KNOWLEDGE_DATE)).thenReturn(propertyValue3);
        when(propertyValue3.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue3);
        when(propertyValue3.getValue()).thenReturn("");

        final var propertyValue4 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.PRIOR_KNOWLEDGE_DATE)).thenReturn(propertyValue4);
        when(propertyValue4.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue4);
        when(propertyValue4.getValue()).thenReturn("2022-12-01T00:00:00");

        Assertions.assertDoesNotThrow(() -> provider.validateDateLogic());
    }

    @Test
    void testClosedPeriodButNoPriorKnowledgeDate() {
        final var propertyValue0 = Mockito.mock(PropertyValue.class);
        when(context.getProperty(BaseExecuteGeneva.ACCOUNTING_RUN_TYPE)).thenReturn(propertyValue0);
        when(propertyValue0.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue0);
        when(propertyValue0.getValue()).thenReturn(BaseExecuteGeneva.CLOSED_PERIOD_ACCOUNTING.getValue());

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

        when(flowfile.getAttribute(CoreAttributes.UUID.key())).thenReturn("c480d5a6-9400-11ee-b9d1-0242ac120002");

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

        when(flowfile.getAttribute(CoreAttributes.UUID.key())).thenReturn("c480d5a6-9400-11ee-b9d1-0242ac120002");

        assertEquals("C:/my user/my filename.csv", provider.getOutputPath());
    }
}
