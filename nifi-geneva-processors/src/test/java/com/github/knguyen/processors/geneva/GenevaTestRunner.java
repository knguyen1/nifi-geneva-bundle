package com.github.knguyen.processors.geneva;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

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
    }

    public GenevaTestRunner execute(IExecuteGenevaTest testRunner) {
        this.testSucceed = testRunner.executeTest(hostname, port, sshAuthenticationStrategy, username, password,
                privateKeyPath, privateKeyPassphrase, dataTimeout, sftpTransferConnectionTimeout, reportOutputDirectory,
                runrepUsername, runrepPassword, genevaAga, accountingRunType, portfolioList, periodStartDate,
                periodEndDate, knowledgeDate, priorKnowledgeDate, reportConsolidation, extraFlags);

        return this;
    }

    public void assertPass() {
        assertEquals(Boolean.TRUE, this.testSucceed);
    }
}
