package com.github.knguyen.processors.geneva;

import java.time.LocalDate;

import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExecuteGenevaArgsValidationTests extends BaseExecuteGenevaTest {
    @BeforeEach
    public void setup() {
        testRunner = TestRunners.newTestRunner(ExecuteGenevaRSL.class);
    }

    @Test
    void testRequiredArgumentsValidations() {
        final GenevaTestRunner gvaTestRunner = new GenevaTestRunner.Builder().withHostname(HOSTNAME)
                .withUsername(USERNAME).withPassword(PASSWORD).withRunrepUsername(RUNREP_USERNAME)
                .withRunrepPassword(RUNREP_PASSWORD).withGenevaAga(9999).withRSLName("netassets").build();

        gvaTestRunner.assertValid(this);
    }

    @Test
    void testSpaceInPortfolioMustBeEscaped1() {
        final GenevaTestRunner gvaTestRunner = new GenevaTestRunner.Builder().withHostname(HOSTNAME)
                .withUsername(USERNAME).withPassword(PASSWORD).withRunrepUsername(RUNREP_USERNAME)
                .withRunrepPassword(RUNREP_PASSWORD).withGenevaAga(9999).withRSLName("netassets")
                .withPortfolioList("123,\\\"Space In Portfolio\\\",456").build();

        gvaTestRunner.assertValid(this);
    }

    @Test
    void testSpaceInPortfolioMustBeEscaped2() {
        final GenevaTestRunner gvaTestRunner = new GenevaTestRunner.Builder().withHostname(HOSTNAME)
                .withUsername(USERNAME).withPassword(PASSWORD).withRunrepUsername(RUNREP_USERNAME)
                .withRunrepPassword(RUNREP_PASSWORD).withGenevaAga(9999).withRSLName("netassets")
                .withPortfolioList("123,Space In Portfolio,456").build();

        gvaTestRunner.assertNotValid(this);
    }

    @Test
    void testInvalidHostname() {
        final GenevaTestRunner gvaTestRunner = new GenevaTestRunner.Builder().withHostname("foo&bar")
                .withRunrepUsername(RUNREP_USERNAME).withRunrepPassword(RUNREP_PASSWORD).withRSLName("netassets")
                .build();

        gvaTestRunner.assertNotValid(this);
    }

    @Test
    void testBasicRunArgumentsShouldCreateValidProcessor() {
        // also support ip addresses
        final GenevaTestRunner gvaTestRunner = new GenevaTestRunner.Builder().withHostname("195.168.1.123")
                .withUsername(USERNAME).withPassword(PASSWORD).withRunrepUsername(RUNREP_USERNAME)
                .withRunrepPassword(RUNREP_PASSWORD).withGenevaAga(9999).withRSLName("netassets")
                .withPeriodStartDate(LocalDate.of(2023, 1, 1).atStartOfDay())
                .withPeriodEndDate(LocalDate.of(2023, 1, 31).atTime(23, 59, 59))
                .withKnowledgeDate(LocalDate.of(2023, 1, 31).atTime(23, 59, 59)).build();

        gvaTestRunner.assertValid(this);
    }
}
