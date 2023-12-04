package com.github.knguyen.processors.geneva;

import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExecuteGenevaRSLTest extends BaseExecuteGenevaTest {
    @BeforeEach
    public void setup() {
        testRunner = TestRunners.newTestRunner(ExecuteGenevaRSL.class);
    }

    @Test void testRequiredArgumentsValidations() {
        final GenevaTestRunner gvaTestRunner = new GenevaTestRunner.Builder()
            .withHostname("my.host.name")
            .withUsername("user")
            .withPassword("password")
            .withRunrepUsername("runrepusr")
            .withRunrepPassword("runreppass")
            .withGenevaAga(9999)
            .withRSLName("netassets")
            .build();

        gvaTestRunner.assertValid(this);
    }
}
