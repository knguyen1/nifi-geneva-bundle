package com.github.knguyen.processors.geneva;

import static org.mockito.Mockito.when;

import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StandardRunrepArgumentProviderTest {
    
    @Mock
    private ProcessContext context;
    
    @Mock
    private FlowFile flowfile;

    @Mock
    private PropertyValue propertyValue;

    private StandardRunrepArgumentProvider provider;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        provider = new StandardRunrepArgumentProvider(context, flowfile);
    }

    @Test
    void testValidateThrowsExceptionForBlankUsername() {
        // Mocking to return a blank username
        when(context.getProperty(BaseExecuteGeneva.RUNREP_USERNAME)).thenReturn(propertyValue);
        when(propertyValue.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue);
        when(propertyValue.getValue()).thenReturn("");

        // Asserting that IllegalArgumentException is thrown
        Assertions.assertThrows(NullPointerException.class, () -> provider.validate());
    }

    @Test
    void testValidateThrowsExceptionForBlankPasword() {
        // Mocking to return a blank username
        when(context.getProperty(BaseExecuteGeneva.RUNREP_PASSWORD)).thenReturn(propertyValue);
        when(propertyValue.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue);
        when(propertyValue.getValue()).thenReturn("");

        // Asserting that IllegalArgumentException is thrown
        Assertions.assertThrows(NullPointerException.class, () -> provider.validate());
    }

    @Test
    void testValidateThrowsExceptionForBlankPaswordButNotUsername() {
        // Mocking to return a non-blank username
        when(context.getProperty(BaseExecuteGeneva.RUNREP_USERNAME)).thenReturn(propertyValue);
        when(propertyValue.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue);
        when(propertyValue.getValue()).thenReturn("validUser");

        // Mocking to return a blank username
        when(context.getProperty(BaseExecuteGeneva.RUNREP_PASSWORD)).thenReturn(propertyValue);
        when(propertyValue.evaluateAttributeExpressions(flowfile)).thenReturn(propertyValue);
        when(propertyValue.getValue()).thenReturn("");

        // Asserting that IllegalArgumentException is thrown
        Assertions.assertThrows(NullPointerException.class, () -> provider.validate());
    }
}
