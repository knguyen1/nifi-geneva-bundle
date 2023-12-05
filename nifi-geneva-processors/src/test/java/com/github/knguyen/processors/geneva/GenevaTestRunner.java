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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * The `GenevaTestRunner` class is used for running Geneva tests via SSH. It encapsulates various configuration
 * parameters required for running the tests, such as hostname, port, SSH authentication strategy, username, password,
 * private key details, connection timeouts, report output directory, test account details, and test parameters.
 *
 * It uses a Builder pattern to construct an instance of `GenevaTestRunner`. This way, you can specify only the
 * parameters you need and keep the rest with their default values.
 *
 * After construction, call the `execute` method and pass in an implementation of `IExecuteGenevaTest`. This will run
 * the test with the current configuration. `execute` method will return a boolean indicating whether the test succeeded
 * or not.
 *
 * After the test run, you can call `assertPass` to assert that the test was successful. It will throw an AssertionError
 * if the test did not pass.
 *
 * Typical usage would involve chaining method calls as follows: GenevaTestRunner testRunner = new
 * GenevaTestRunner.Builder().withHostname("hostname").withUsername("username").build();
 * testRunner.execute(myTestImplementation).assertPass();
 */
public class GenevaTestRunner {
    private String hostname;
    private Integer port;
    private String sshAuthenticationStrategy;
    private String username;
    private String password;
    private String privateKeyPath;
    private String privateKeyPassphrase;
    private String dataTimeout;
    private String sftpTransferConnectionTimeout;
    private String reportOutputDirectory;
    private String runrepUsername;
    private String runrepPassword;
    private Integer genevaAga;
    private String accountingRunType;
    private String portfolioList;
    private LocalDateTime periodStartDate;
    private LocalDateTime periodEndDate;
    private LocalDateTime knowledgeDate;
    private LocalDateTime priorKnowledgeDate;
    private String reportConsolidation;
    private String extraFlags;

    // report-specific args
    private String rslName;

    // flowfile content checks
    private Map<String, String> expectedAttributes;
    private String expectedContent;
    private String expectedCommandPattern;

    private Boolean testSucceed = false;

    public static class Builder {
        private String hostname;
        private Integer port = 22;
        private String sshAuthenticationStrategy;
        private String username;
        private String password;
        private String privateKeyPath;
        private String privateKeyPassphrase;
        private String dataTimeout;
        private String sftpTransferConnectionTimeout;
        private String reportOutputDirectory;
        private String runrepUsername;
        private String runrepPassword;
        private Integer genevaAga;
        private String accountingRunType;
        private String portfolioList;
        private LocalDateTime periodStartDate;
        private LocalDateTime periodEndDate;
        private LocalDateTime knowledgeDate;
        private LocalDateTime priorKnowledgeDate;
        private String reportConsolidation;
        private String extraFlags;

        // report-specific args
        private String rslName;

        // flowfile content checks
        private Map<String, String> expectedAttributes;
        private String expectedContent;
        private String expectedCommandPattern;

        public Builder withExpectedAttributes(Map<String, String> expectedAttributes) {
            this.expectedAttributes = expectedAttributes;
            return this;
        }

        public Builder withExpectedCommandPattern(String expectedCommandPattern) {
            this.expectedCommandPattern = expectedCommandPattern;
            return this;
        }

        public Builder withExpectedContent(String expectedContent) {
            this.expectedContent = expectedContent;
            return this;
        }

        public Builder withHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder withPort(Integer port) {
            this.port = port;
            return this;
        }

        public Builder withSshAuthenticationStrategy(String sshAuthenticationStrategy) {
            this.sshAuthenticationStrategy = sshAuthenticationStrategy;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withPrivateKeyPath(String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
            return this;
        }

        public Builder withPrivateKeyPassphrase(String privateKeyPassphrase) {
            this.privateKeyPassphrase = privateKeyPassphrase;
            return this;
        }

        public Builder withDataTimeout(String dataTimeout) {
            this.dataTimeout = dataTimeout;
            return this;
        }

        public Builder withSftpTransferConnectionTimeout(String sftpTransferConnectionTimeout) {
            this.sftpTransferConnectionTimeout = sftpTransferConnectionTimeout;
            return this;
        }

        public Builder withReportOutputDirectory(String reportOutputDirectory) {
            this.reportOutputDirectory = reportOutputDirectory;
            return this;
        }

        public Builder withRunrepUsername(String runrepUsername) {
            this.runrepUsername = runrepUsername;
            return this;
        }

        public Builder withRunrepPassword(String runrepPassword) {
            this.runrepPassword = runrepPassword;
            return this;
        }

        public Builder withGenevaAga(Integer genevaAga) {
            this.genevaAga = genevaAga;
            return this;
        }

        public Builder withAccountingRunType(String accountingRunType) {
            this.accountingRunType = accountingRunType;
            return this;
        }

        public Builder withPortfolioList(String portfolioList) {
            this.portfolioList = portfolioList;
            return this;
        }

        public Builder withPeriodStartDate(LocalDateTime periodStartDate) {
            this.periodStartDate = periodStartDate;
            return this;
        }

        public Builder withPeriodEndDate(LocalDateTime periodEndDate) {
            this.periodEndDate = periodEndDate;
            return this;
        }

        public Builder withKnowledgeDate(LocalDateTime knowledgeDate) {
            this.knowledgeDate = knowledgeDate;
            return this;
        }

        public Builder withPriorKnowledgeDate(LocalDateTime priorKnowledgeDate) {
            this.priorKnowledgeDate = priorKnowledgeDate;
            return this;
        }

        public Builder withReportConsolidation(String reportConsolidation) {
            this.reportConsolidation = reportConsolidation;
            return this;
        }

        public Builder withExtraFlags(String extraFlags) {
            this.extraFlags = extraFlags;
            return this;
        }

        public Builder withRSLName(String rslName) {
            this.rslName = rslName;
            return this;
        }

        public GenevaTestRunner build() {
            return new GenevaTestRunner(this);
        }
    }

    private GenevaTestRunner(Builder builder) {
        this.hostname = builder.hostname;
        this.port = builder.port;
        this.sshAuthenticationStrategy = builder.sshAuthenticationStrategy;
        this.username = builder.username;
        this.password = builder.password;
        this.privateKeyPath = builder.privateKeyPath;
        this.privateKeyPassphrase = builder.privateKeyPassphrase;
        this.dataTimeout = builder.dataTimeout;
        this.sftpTransferConnectionTimeout = builder.sftpTransferConnectionTimeout;
        this.reportOutputDirectory = builder.reportOutputDirectory;
        this.runrepUsername = builder.runrepUsername;
        this.runrepPassword = builder.runrepPassword;
        this.genevaAga = builder.genevaAga;
        this.accountingRunType = builder.accountingRunType;
        this.portfolioList = builder.portfolioList;
        this.periodStartDate = builder.periodStartDate;
        this.periodEndDate = builder.periodEndDate;
        this.knowledgeDate = builder.knowledgeDate;
        this.priorKnowledgeDate = builder.priorKnowledgeDate;
        this.reportConsolidation = builder.reportConsolidation;
        this.extraFlags = builder.extraFlags;
        this.rslName = builder.rslName;
        this.expectedAttributes = builder.expectedAttributes;
        this.expectedContent = builder.expectedContent;
        this.expectedCommandPattern = builder.expectedCommandPattern;
    }

    public GenevaTestRunner execute(IExecuteGenevaTest testRunner) {
        this.testSucceed = testRunner.executeTest(hostname, port, sshAuthenticationStrategy, username, password,
                privateKeyPath, privateKeyPassphrase, dataTimeout, sftpTransferConnectionTimeout, reportOutputDirectory,
                runrepUsername, runrepPassword, genevaAga, accountingRunType, portfolioList, periodStartDate,
                periodEndDate, knowledgeDate, priorKnowledgeDate, reportConsolidation, extraFlags, rslName,
                expectedAttributes, expectedContent, expectedCommandPattern);

        return this;
    }

    public GenevaTestRunner assertValid(IExecuteGenevaTest testRunner) {
        testRunner.assertValid(hostname, port, sshAuthenticationStrategy, username, password, privateKeyPath,
                privateKeyPassphrase, dataTimeout, sftpTransferConnectionTimeout, reportOutputDirectory, runrepUsername,
                runrepPassword, genevaAga, accountingRunType, portfolioList, periodStartDate, periodEndDate,
                knowledgeDate, priorKnowledgeDate, reportConsolidation, extraFlags, rslName, expectedAttributes,
                expectedContent, expectedCommandPattern);
        return this;
    }

    public GenevaTestRunner assertNotValid(IExecuteGenevaTest testRunner) {
        testRunner.assertNotValid(hostname, port, sshAuthenticationStrategy, username, password, privateKeyPath,
                privateKeyPassphrase, dataTimeout, sftpTransferConnectionTimeout, reportOutputDirectory, runrepUsername,
                runrepPassword, genevaAga, accountingRunType, portfolioList, periodStartDate, periodEndDate,
                knowledgeDate, priorKnowledgeDate, reportConsolidation, extraFlags, rslName, expectedAttributes,
                expectedContent, expectedCommandPattern);
        return this;
    }

    public void assertPass() {
        assertEquals(Boolean.TRUE, this.testSucceed);
    }
}
