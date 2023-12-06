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
package com.github.knguyen.processors.geneva;

import java.time.LocalDate;

import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.knguyen.processors.geneva.runners.GenevaTestRunner;

class ExecuteGenevaArgsValidationTest extends BaseExecuteGenevaTest {
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
