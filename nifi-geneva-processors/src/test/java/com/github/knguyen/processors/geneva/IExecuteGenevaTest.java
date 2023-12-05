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
import java.util.Map;

public interface IExecuteGenevaTest {
    boolean executeTest(String hostname, Integer port, String sshAuthenticationStrategy, String username,
            String password, String privateKeyPath, String privateKeyPassphrase, String dataTimeout,
            String sftpTransferConnectionTimeout, String reportOutputDirectory, String runrepUsername,
            String runrepPassword, Integer genevaAga, String accountingRunType, String portfolioList,
            LocalDateTime periodStartDate, LocalDateTime periodEndDate, LocalDateTime knowledgeDate,
            LocalDateTime priorKnowledgeDate, String reportConsolidation, String extraFlags, String rslName,
            Map<String, String> expectedAttributes, String expectedContent, String expectedCommandPattern);

    void assertValid(String hostname, Integer port, String sshAuthenticationStrategy, String username, String password,
            String privateKeyPath, String privateKeyPassphrase, String dataTimeout,
            String sftpTransferConnectionTimeout, String reportOutputDirectory, String runrepUsername,
            String runrepPassword, Integer genevaAga, String accountingRunType, String portfolioList,
            LocalDateTime periodStartDate, LocalDateTime periodEndDate, LocalDateTime knowledgeDate,
            LocalDateTime priorKnowledgeDate, String reportConsolidation, String extraFlags, String rslName,
            Map<String, String> expectedAttributes, String expectedContent, String expectedCommandPattern);

    void assertNotValid(String hostname, Integer port, String sshAuthenticationStrategy, String username,
            String password, String privateKeyPath, String privateKeyPassphrase, String dataTimeout,
            String sftpTransferConnectionTimeout, String reportOutputDirectory, String runrepUsername,
            String runrepPassword, Integer genevaAga, String accountingRunType, String portfolioList,
            LocalDateTime periodStartDate, LocalDateTime periodEndDate, LocalDateTime knowledgeDate,
            LocalDateTime priorKnowledgeDate, String reportConsolidation, String extraFlags, String rslName,
            Map<String, String> expectedAttributes, String expectedContent, String expectedCommandPattern);
}
