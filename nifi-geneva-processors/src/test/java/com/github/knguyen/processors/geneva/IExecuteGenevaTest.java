package com.github.knguyen.processors.geneva;

import java.time.LocalDateTime;

public interface IExecuteGenevaTest {
    boolean executeTest(String hostname, Integer port, String sshAuthenticationStrategy, String username,
            String password, String privateKeyPath, String privateKeyPassphrase, String dataTimeout,
            String sftpTransferConnectionTimeout, String reportOutputDirectory, String runrepUsername,
            String runrepPassword, Integer genevaAga, String accountingRunType, String portfolioList,
            LocalDateTime periodStartDate, LocalDateTime periodEndDate, LocalDateTime knowledgeDate,
            LocalDateTime priorKnowledgeDate, String reportConsolidation, String extraFlags);

    void assertValid(String hostname, Integer port, String sshAuthenticationStrategy, String username, String password,
            String privateKeyPath, String privateKeyPassphrase, String dataTimeout,
            String sftpTransferConnectionTimeout, String reportOutputDirectory, String runrepUsername,
            String runrepPassword, Integer genevaAga, String accountingRunType, String portfolioList,
            LocalDateTime periodStartDate, LocalDateTime periodEndDate, LocalDateTime knowledgeDate,
            LocalDateTime priorKnowledgeDate, String reportConsolidation, String extraFlags);

    void assertNotValid(String hostname, Integer port, String sshAuthenticationStrategy, String username,
            String password, String privateKeyPath, String privateKeyPassphrase, String dataTimeout,
            String sftpTransferConnectionTimeout, String reportOutputDirectory, String runrepUsername,
            String runrepPassword, Integer genevaAga, String accountingRunType, String portfolioList,
            LocalDateTime periodStartDate, LocalDateTime periodEndDate, LocalDateTime knowledgeDate,
            LocalDateTime priorKnowledgeDate, String reportConsolidation, String extraFlags);
}
