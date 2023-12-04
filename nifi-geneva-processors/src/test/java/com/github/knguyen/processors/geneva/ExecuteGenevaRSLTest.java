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

import java.time.LocalDateTime;

import org.apache.nifi.util.StringUtils;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

abstract class BaseExecuteGenevaTest implements IExecuteGenevaTest {

    protected TestRunner testRunner;

    protected boolean argumentsHaveBeenSet = false;

    protected void setArguments(String hostname, Integer port, String sshAuthenticationStrategy, String username,
            String password, String privateKeyPath, String privateKeyPassphrase, String dataTimeout,
            String sftpTransferConnectionTimeout, String reportOutputDirectory, String runrepUsername,
            String runrepPassword, Integer genevaAga, String accountingRunType, String portfolioList,
            LocalDateTime periodStartDate, LocalDateTime periodEndDate, LocalDateTime knowledgeDate,
            LocalDateTime priorKnowledgeDate, String reportConsolidation, String extraFlags) {
        if (StringUtils.isNotBlank(hostname))
            testRunner.setProperty(BaseExecuteGeneva.HOSTNAME, hostname);

        if (port != null) {
            testRunner.setProperty(BaseExecuteGeneva.PORT, String.valueOf(port));
        }

        if (StringUtils.isNotBlank(sshAuthenticationStrategy)) {
            testRunner.setProperty(BaseExecuteGeneva.SSH_AUTHENTICATION_STRATEGY, sshAuthenticationStrategy);
        }

        if (StringUtils.isNotBlank(username)) {
            testRunner.setProperty(BaseExecuteGeneva.USERNAME, username);
        }

        if (StringUtils.isNotBlank(password)) {
            testRunner.setProperty(BaseExecuteGeneva.PASSWORD, password);
        }

        if (StringUtils.isNotBlank(privateKeyPath)) {
            testRunner.setProperty(BaseExecuteGeneva.PRIVATE_KEY_PATH, privateKeyPath);
        }

        if (StringUtils.isNotBlank(privateKeyPassphrase)) {
            testRunner.setProperty(BaseExecuteGeneva.PRIVATE_KEY_PASSPHRASE, privateKeyPassphrase);
        }

        if (StringUtils.isNotBlank(dataTimeout)) {
            testRunner.setProperty(BaseExecuteGeneva.DATA_TIMEOUT, dataTimeout);
        }

        if (StringUtils.isNotBlank(sftpTransferConnectionTimeout)) {
            testRunner.setProperty("Connection Timeout", sftpTransferConnectionTimeout);
        }

        if (StringUtils.isNotBlank(reportOutputDirectory)) {
            testRunner.setProperty(BaseExecuteGeneva.REPORT_OUTPUT_DIRECTORY, reportOutputDirectory);
        }

        if (StringUtils.isNotBlank(runrepUsername)) {
            testRunner.setProperty(BaseExecuteGeneva.RUNREP_USERNAME, runrepUsername);
        }

        if (StringUtils.isNotBlank(runrepPassword)) {
            testRunner.setProperty(BaseExecuteGeneva.RUNREP_PASSWORD, runrepPassword);
        }

        if (genevaAga != null) {
            testRunner.setProperty(BaseExecuteGeneva.GENEVA_AGA, String.valueOf(genevaAga));
        }

        if (StringUtils.isNotBlank(accountingRunType)) {
            testRunner.setProperty(BaseExecuteGeneva.ACCOUNTING_RUN_TYPE, accountingRunType);
        }

        if (StringUtils.isNotBlank(portfolioList)) {
            testRunner.setProperty(BaseExecuteGeneva.PORTFOLIO_LIST, portfolioList);
        }

        if (periodStartDate != null) {
            final String periodStartDateTimeISO = periodStartDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            testRunner.setProperty(BaseExecuteGeneva.PERIOD_START_DATE, periodStartDateTimeISO);
        }

        if (periodEndDate != null) {
            final String periodEndDateTimeISO = periodEndDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            testRunner.setProperty(BaseExecuteGeneva.PERIOD_END_DATE, periodEndDateTimeISO);
        }

        if (knowledgeDate != null) {
            final String knowledgeDateTimeISO = knowledgeDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            testRunner.setProperty(BaseExecuteGeneva.KNOWLEDGE_DATE, knowledgeDateTimeISO);
        }

        if (priorKnowledgeDate != null) {
            final String priorKnowledgeDateTimeISO = priorKnowledgeDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            testRunner.setProperty(BaseExecuteGeneva.PRIOR_KNOWLEDGE_DATE, priorKnowledgeDateTimeISO);
        }

        if (StringUtils.isNotBlank(reportConsolidation)) {
            testRunner.setProperty(BaseExecuteGeneva.REPORT_CONSOLIDATION, reportConsolidation);
        }

        if (StringUtils.isNotBlank(extraFlags)) {
            testRunner.setProperty(BaseExecuteGeneva.EXTRA_FLAGS, extraFlags);
        }

        argumentsHaveBeenSet = true;
    }

    @Override
    public boolean executeTest(String hostname, Integer port, String sshAuthenticationStrategy, String username,
            String password, String privateKeyPath, String privateKeyPassphrase, String dataTimeout,
            String sftpTransferConnectionTimeout, String reportOutputDirectory, String runrepUsername,
            String runrepPassword, Integer genevaAga, String accountingRunType, String portfolioList,
            LocalDateTime periodStartDate, LocalDateTime periodEndDate, LocalDateTime knowledgeDate,
            LocalDateTime priorKnowledgeDate, String reportConsolidation, String extraFlags) {
        if (!argumentsHaveBeenSet)
            setArguments(hostname, port, sshAuthenticationStrategy, username, password, privateKeyPath,
                    privateKeyPassphrase, dataTimeout, sftpTransferConnectionTimeout, reportOutputDirectory,
                    runrepUsername, runrepPassword, genevaAga, accountingRunType, portfolioList, periodStartDate,
                    periodEndDate, knowledgeDate, priorKnowledgeDate, reportConsolidation, extraFlags);

        return true;
    }

    @Override
    public void assertValid(String hostname, Integer port, String sshAuthenticationStrategy, String username,
            String password, String privateKeyPath, String privateKeyPassphrase, String dataTimeout,
            String sftpTransferConnectionTimeout, String reportOutputDirectory, String runrepUsername,
            String runrepPassword, Integer genevaAga, String accountingRunType, String portfolioList,
            LocalDateTime periodStartDate, LocalDateTime periodEndDate, LocalDateTime knowledgeDate,
            LocalDateTime priorKnowledgeDate, String reportConsolidation, String extraFlags) {
        if (!argumentsHaveBeenSet)
            setArguments(hostname, port, sshAuthenticationStrategy, username, password, privateKeyPath,
                    privateKeyPassphrase, dataTimeout, sftpTransferConnectionTimeout, reportOutputDirectory,
                    runrepUsername, runrepPassword, genevaAga, accountingRunType, portfolioList, periodStartDate,
                    periodEndDate, knowledgeDate, priorKnowledgeDate, reportConsolidation, extraFlags);

        testRunner.assertValid();
    }

    @Override
    public void assertNotValid(String hostname, Integer port, String sshAuthenticationStrategy, String username,
            String password, String privateKeyPath, String privateKeyPassphrase, String dataTimeout,
            String sftpTransferConnectionTimeout, String reportOutputDirectory, String runrepUsername,
            String runrepPassword, Integer genevaAga, String accountingRunType, String portfolioList,
            LocalDateTime periodStartDate, LocalDateTime periodEndDate, LocalDateTime knowledgeDate,
            LocalDateTime priorKnowledgeDate, String reportConsolidation, String extraFlags) {
        if (!argumentsHaveBeenSet)
            setArguments(hostname, port, sshAuthenticationStrategy, username, password, privateKeyPath,
                    privateKeyPassphrase, dataTimeout, sftpTransferConnectionTimeout, reportOutputDirectory,
                    runrepUsername, runrepPassword, genevaAga, accountingRunType, portfolioList, periodStartDate,
                    periodEndDate, knowledgeDate, priorKnowledgeDate, reportConsolidation, extraFlags);

        testRunner.assertNotValid();
    }

    // @BeforeEach
    // public void init() {
    // testRunner = TestRunners.newTestRunner(BaseExecuteGeneva.class);
    // }

    // @Test
    // void testProcessor() {

    // }

}
