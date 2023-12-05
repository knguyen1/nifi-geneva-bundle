package com.github.knguyen.processors.geneva;

import java.time.LocalDateTime;
import java.util.List;
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
