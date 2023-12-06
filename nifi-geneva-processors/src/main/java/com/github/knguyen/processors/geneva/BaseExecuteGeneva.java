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

import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.processors.standard.ssh.SSHClientProvider;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processors.standard.util.FTPTransfer;
import org.apache.nifi.processors.standard.util.FileTransfer;
import org.apache.nifi.processors.standard.util.SFTPTransfer;

import com.github.knguyen.processors.ssh.SSHCommandExecutorProvider;
import com.github.knguyen.processors.utils.CustomValidators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.nifi.util.StopWatch;

import java.util.concurrent.TimeUnit;

public abstract class BaseExecuteGeneva extends AbstractProcessor {

    protected SSHClientProvider sshClientProvider;
    protected RemoteCommandExecutorProvider executorProvider = new SSHCommandExecutorProvider();
    protected RemoteCommandExecutor remoteCommandExecutor;

    protected void setSSHClientProvider(final SSHClientProvider sshClientProvider) {
        this.sshClientProvider = sshClientProvider;
    }

    protected void setExecutorProvider(final RemoteCommandExecutorProvider commandExecutorProvider) {
        this.executorProvider = commandExecutorProvider;
    }

    static final AllowableValue USERNAME_PASSWORD_STRATEGY = new AllowableValue("password-authentication",
            "Password Authentication",
            "Use username and password for SSH authentication. Be aware that these credentials are generally different from those used for the `runrep` utility.");
    static final AllowableValue IDENTITY_FILE_STRATEGY = new AllowableValue("identity-file", "Identity File",
            "Use an identity file to log in to SSH.  The file should be accessible by your NiFi installation and have appropriate permissions.  The identify file is assumed to be password-less and in RSA format, where applicable.");

    static final PropertyDescriptor HOSTNAME = new PropertyDescriptor.Builder()
            .fromPropertyDescriptor(FileTransfer.HOSTNAME)
            .description(
                    "SSH Host for Runrep Utility: This refers to the SSH host where the Geneva runrep utility is located. In most configurations, this is the same server that hosts your Geneva AGA. You should specify this as a hostname or IP address.")
            .addValidator(CustomValidators.HOSTNAME_VALIDATOR).build();

    static final PropertyDescriptor PORT = new PropertyDescriptor.Builder().fromPropertyDescriptor(SFTPTransfer.PORT)
            .description(
                    "The port on the server to connect to; default is 22. This value is not the same as your Geneva AGA.")
            .build();

    static final PropertyDescriptor SSH_AUTHENTICATION_STRATEGY = new PropertyDescriptor.Builder()
            .name("ssh-authentication-strategy").displayName("SSH Authentication Strategy")
            .description("Specifies the method of authentication for the SSH connection.")
            .allowableValues(USERNAME_PASSWORD_STRATEGY, IDENTITY_FILE_STRATEGY)
            .defaultValue(USERNAME_PASSWORD_STRATEGY.getValue()).addValidator(Validator.VALID).required(true).build();

    static final PropertyDescriptor USERNAME = new PropertyDescriptor.Builder()
            .fromPropertyDescriptor(FileTransfer.USERNAME).description("The username on the host to connect as.")
            .build();

    static final PropertyDescriptor PASSWORD = new PropertyDescriptor.Builder()
            .fromPropertyDescriptor(FileTransfer.PASSWORD)
            .description(
                    "The password to connect to the host.  This property is ignored if `Identify File` is chosen as the authentication strategy.")
            .dependsOn(SSH_AUTHENTICATION_STRATEGY, USERNAME_PASSWORD_STRATEGY).build();

    static final PropertyDescriptor PRIVATE_KEY_PATH = new PropertyDescriptor.Builder()
            .fromPropertyDescriptor(SFTPTransfer.PRIVATE_KEY_PATH)
            .description(
                    "The path to the SSH identity file.  Must be accessible by NiFi and have appropriate permissions.  This property is ignored if `Password Authentication` is chosen as the authentication strategy.")
            .dependsOn(SSH_AUTHENTICATION_STRATEGY, IDENTITY_FILE_STRATEGY)
            .addValidator(StandardValidators.FILE_EXISTS_VALIDATOR).build();

    static final PropertyDescriptor PRIVATE_KEY_PASSPHRASE = new PropertyDescriptor.Builder()
            .fromPropertyDescriptor(SFTPTransfer.PRIVATE_KEY_PASSPHRASE)
            .dependsOn(SSH_AUTHENTICATION_STRATEGY, IDENTITY_FILE_STRATEGY)
            .description(
                    "Password for the private key.  This property is ignored if `Password Authentication` is chosen as the authentication strategy.")
            .build();

    static final PropertyDescriptor DATA_TIMEOUT = new PropertyDescriptor.Builder()
            .fromPropertyDescriptor(FileTransfer.DATA_TIMEOUT)
            .description(
                    "Specifies the timeout duration for data transmission during command execution, like `runrep` or `rungsql`.  If you have large RSL reports or accounting runs, you should set this value to a long duration.")
            .defaultValue("5 mins").build();

    static final PropertyDescriptor RUNREP_USERNAME = new PropertyDescriptor.Builder().name("runrep-username")
            .displayName("Runrep Username").description("The username used to authenticate with runrep.").required(true)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .addValidator(StandardValidators.NON_BLANK_VALIDATOR).build();

    static final PropertyDescriptor RUNREP_PASSWORD = new PropertyDescriptor.Builder().name("runrep-password")
            .displayName("Runrep Password").description("The password used to authenticate with runrep.").required(true)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).sensitive(true)
            .addValidator(StandardValidators.NON_BLANK_VALIDATOR).build();

    static final PropertyDescriptor GENEVA_AGA = new PropertyDescriptor.Builder().name("geneva-aga")
            .displayName("Geneva AGA").description("Specifies the Geneva AGA (the port number) you want to target.")
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).sensitive(false).required(false)
            .addValidator(StandardValidators.PORT_VALIDATOR).build();

    static final AllowableValue OUTPUT_ASCII = new AllowableValue("ascii", "ASCII",
            "ASCII text with report run identifier. Default option if no other format is specified.");
    static final AllowableValue OUTPUT_ASCII_NOID = new AllowableValue("asciinoid", "ASCII No ID",
            "ASCII text without report run identifier.");
    static final AllowableValue OUTPUT_ASCII_NOHEADER = new AllowableValue("asciinoheader", "ASCII No Header",
            "ASCII text without report header or report run identifier.");
    static final AllowableValue OUTPUT_BCP = new AllowableValue("bcp", "BCP",
            "Pipe (|) delimited flat file records ready for loading into another database.");
    static final AllowableValue OUTPUT_BCP_ID = new AllowableValue("bcpid", "BCP ID",
            "Pipe (|) delimited flat file records with report run identifiers appended to each record.");
    static final AllowableValue OUTPUT_BCP_NOSPACE = new AllowableValue("bcpnospace", "BCP No Space",
            "Pipe (|) delimited flat file records that replace spaces in the report output with underscores (_).");
    static final AllowableValue OUTPUT_COLUMNAR = new AllowableValue("col", "Columnar",
            "(For runquery only) Columnar flat file records.");
    static final AllowableValue OUTPUT_CSV = new AllowableValue("csv", "CSV",
            "Comma-delimited flat file records without run identifier.");
    static final AllowableValue OUTPUT_CSV_NOSPACE = new AllowableValue("csvnospace", "CSV No Space",
            "Comma-delimited flat file records without run identifiers, that replace spaces in the report output with underscores (_).");
    static final AllowableValue OUTPUT_JSON = new AllowableValue("json", "JSON", "JavaScript Object Notation format");
    static final AllowableValue OUTPUT_PDF = new AllowableValue("pdf", "PDF",
            "Adobe Portable Document Format. For information about including an image from a graphics file in the report, see “Including Images in PDF Reports” on page 179.");
    static final AllowableValue OUTPUT_PDF_NOID = new AllowableValue("pdfnoid", "PDF No ID",
            "Adobe Portable Document Format files without report run identifiers.");
    static final AllowableValue OUTPUT_RMF = new AllowableValue("rmf", "RMF", "Report metafile (GenPAV).");
    static final AllowableValue OUTPUT_TSV = new AllowableValue("tsv", "TSV", "Tab-separated values format.");
    static final AllowableValue OUTPUT_XML = new AllowableValue("xml", "XML",
            "A single XML file, <report>.xml, that contains all report output, as well as report arguments and addendum errors.");
    static final AllowableValue OUTPUT_XML_ERROR = new AllowableValue("xmlerr", "XML Error",
            "A <report>.xml file, that contains all report output; <report>.err, that contains the report’s addendum errors; and <report>.arg, that contains the report’s arguments.");

    static final PropertyDescriptor REPORT_OUTPUT_FORMAT = new PropertyDescriptor.Builder().name("report-output-format")
            .displayName("Report Output Format").description("Defines the output format of the report.").required(true)
            .defaultValue(OUTPUT_CSV.getValue())
            .allowableValues(OUTPUT_ASCII, OUTPUT_ASCII_NOID, OUTPUT_ASCII_NOHEADER, OUTPUT_BCP, OUTPUT_BCP_ID,
                    OUTPUT_BCP_NOSPACE, OUTPUT_COLUMNAR, OUTPUT_CSV, OUTPUT_CSV_NOSPACE, OUTPUT_JSON, OUTPUT_PDF,
                    OUTPUT_PDF_NOID, OUTPUT_RMF, OUTPUT_TSV, OUTPUT_XML, OUTPUT_XML_ERROR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).addValidator(Validator.VALID)
            .build();

    static final PropertyDescriptor REPORT_OUTPUT_PATH = new PropertyDescriptor.Builder().name("report-output-path")
            .displayName("Report Output Path")
            .description(
                    "Specifies the absolute, fully-qualified path for the report output, which corresponds to the `-o` option for `runrep`. The path should be accessible and writable by `runrep`. When this property is set, the `Report Output Directory` property is ignored. If this property is not set, NiFi will use the `Report Output Directory` value and generate a filename matching the FlowFile's `UUID`.  Keep in mind that by using this property, you effectively set a constant filepath for writing reports.  This means that successive executions of flowfiles in the same given pipeline will overwrite any existing reports at this locatio.  This could be desirable for certain scenarios, such as when using processing pipelines external to NiFi that require data at a fixed location.")
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).sensitive(false).required(false)
            .addValidator(Validator.VALID).build();

    static final PropertyDescriptor REPORT_OUTPUT_DIRECTORY = new PropertyDescriptor.Builder()
            .name("report-output-directory").displayName("Report Output Directory")
            .description(
                    "Defines the directory for storing report files. This directory stores reports for NiFi processing. System administrators should regularly manage and clear this space for smooth operation, although processors in this bundle will make attempts to dispose any un-managed resources.  This value defaults to the system-dependent 'temporary path' value.")
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).sensitive(false).required(false)
            .addValidator(StandardValidators.createDirectoryExistsValidator(true, true))
            .defaultValue(System.getProperty("java.io.tmpdir")).build();

    static final PropertyDescriptor PORTFOLIO_LIST = new PropertyDescriptor.Builder().name("portfolio")
            .displayName("Portfolio List")
            .description(
                    "Specifies portfolios as a comma-separated list. Enclose names containing spaces in escaped quotes, e.g., `MyPortfolio1,\\\"9000 International Fixed Income\\\",MyOtherPortfolio`.")
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false)
            .defaultValue("${geneva.portfolio}").addValidator(CustomValidators.PORTFOLIO_LIST_VALIDATOR).build();

    static final PropertyDescriptor PERIOD_START_DATE = new PropertyDescriptor.Builder().name("periodstartdate")
            .displayName("Period Start Date")
            .description("Specifies the period start date using ISO Date Time Format, i.e. yyyy-MM-dd'T'HH:mm:ss")
            .addValidator(CustomValidators.DATETIME_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false)
            .defaultValue("${geneva.periodstartdate}").build();

    static final PropertyDescriptor PERIOD_END_DATE = new PropertyDescriptor.Builder().name("periodenddate")
            .displayName("Period End Date")
            .description("Specifies the period end date using ISO Date Time Format, i.e. yyyy-MM-dd'T'HH:mm:ss")
            .addValidator(CustomValidators.DATETIME_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false)
            .defaultValue("${geneva.periodenddate}").build();

    static final PropertyDescriptor KNOWLEDGE_DATE = new PropertyDescriptor.Builder().name("knowledgedate")
            .displayName("Knowledge Date")
            .description("Specifies the knowledge date using ISO Date Time Format, i.e. yyyy-MM-dd'T'HH:mm:ss")
            .addValidator(CustomValidators.DATETIME_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false)
            .defaultValue("${geneva.knowledgedate}").build();

    static final AllowableValue DYNAMIC_ACCOUNTING = new AllowableValue("Dynamic", "Dynamic",
            "Dynamic accounting run.  The default mode for most reports.");
    static final AllowableValue CLOSED_PERIOD_ACCOUNTING = new AllowableValue("ClosedPeriod", "ClosedPeriod",
            "Signifies that we should run this report under `ClosedPeriod` accounting.  PNL's and accruals are treated differently under this mode.");

    static final AllowableValue UNAMENDED_CLOSED_PERIOD_ACCOUNTING = new AllowableValue("UnAmendedClosedPeriod",
            "UnAmendedClosedPeriod", "UnAmendedClosedPeriod accounting run.");
    static final AllowableValue INCREMENTAL_ACCOUNTING = new AllowableValue("Incremental", "Incremental",
            "Incremental accounting run.");
    static final AllowableValue NAV_ACCOUNTING = new AllowableValue("NAV", "NAV", "NAV accounting run.");
    static final AllowableValue WOULD_BE_ADJUSTMENTS_ACCOUNTING = new AllowableValue("WouldBeAdjustments",
            "WouldBeAdjustments", "WouldBeAdjustments accounting run.");
    static final AllowableValue TWR_ACCOUNTING = new AllowableValue("TWR", "TWR", "TWR accounting run.");
    static final AllowableValue SNAPSHOT_ACCOUNTING = new AllowableValue("Snapshot", "Snapshot",
            "Snapshot accounting run.");

    static final PropertyDescriptor ACCOUNTING_RUN_TYPE = new PropertyDescriptor.Builder().name("-at")
            .displayName("Accounting Run Type")
            .description(
                    "The type of accounting run that the report performs.  If you do not use this option, it defaults to dynamic accounting.  For reports that cannot use dynamic accounting, however, such as the Fund category reports, and the Transactions Excluded Due to Freezepoint and Transactions Modified After Freezepoint reports, you must use this option to specify a valid accouting type.")
            .addValidator(Validator.VALID)
            .allowableValues(DYNAMIC_ACCOUNTING, CLOSED_PERIOD_ACCOUNTING, UNAMENDED_CLOSED_PERIOD_ACCOUNTING,
                    INCREMENTAL_ACCOUNTING, NAV_ACCOUNTING, WOULD_BE_ADJUSTMENTS_ACCOUNTING, TWR_ACCOUNTING,
                    SNAPSHOT_ACCOUNTING)
            .defaultValue(DYNAMIC_ACCOUNTING.getValue())
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).build();

    static final PropertyDescriptor PRIOR_KNOWLEDGE_DATE = new PropertyDescriptor.Builder().name("PriorKnowledgeDate")
            .displayName("Prior Knowledge Date")
            .description("Specifies the prior knowledge date using ISO Date Time Format, i.e. yyyy-MM-dd'T'HH:mm:ss")
            .addValidator(CustomValidators.DATETIME_VALIDATOR)
            .dependsOn(ACCOUNTING_RUN_TYPE, CLOSED_PERIOD_ACCOUNTING, INCREMENTAL_ACCOUNTING)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false)
            .defaultValue("${geneva.priorknowledgedate}").build();

    static final PropertyDescriptor EXTRA_FLAGS = new PropertyDescriptor.Builder().displayName("Extra Flags")
            .name("extra-flags")
            .description(
                    "Use this field to specify any other flags.  You are responsible for correctly escaping any fields with special characters and spaces.  The arguments specified here will be presented as-is during a report run.")
            .addValidator(Validator.VALID).required(false)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .defaultValue("${geneva.extraflags}").build();

    static final AllowableValue CONSOLIDATE_ALL = new AllowableValue("-c1", "All", "Produces one consolidated report");
    static final AllowableValue GROUP_CONSOLIDATE = new AllowableValue("-c2", "GroupsOnly",
            "Produces reports consolidated on portfolio and accounting parameters groups.");
    static final AllowableValue NONE_CONSOLIDATED = new AllowableValue("-c3", "None",
            "Does not produce consolidated reports (the default).");

    static final PropertyDescriptor REPORT_CONSOLIDATION = new PropertyDescriptor.Builder()
            .name("consolidation-preference").displayName("Consolidate")
            .description(
                    "How to consolidate the report: All for consolidated reports, GroupsOnly for group consolidated reports, and None for iterated reports with no consolidation (the default).")
            .allowableValues(CONSOLIDATE_ALL, GROUP_CONSOLIDATE, NONE_CONSOLIDATED)
            .defaultValue(NONE_CONSOLIDATED.getValue()).addValidator(Validator.VALID)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false).build();

    static final Relationship REL_SUCCESS = new Relationship.Builder().name("success")
            .description("Any Geneva query that executed without errors will be routed to `success`.").build();
    static final Relationship REL_FAILURE = new Relationship.Builder().name("failure")
            .description("General exceptions (e.g. IOException, Timeout, etc.) will be routed to `failure`..").build();
    static final Relationship REL_GENEVA_FAILURE = new Relationship.Builder().name("geneva-failure")
            .description("Any Geneva query that executed with errors will be routed to `geneva-failure`.").build();

    protected List<PropertyDescriptor> descriptors;

    protected Set<Relationship> relationships;

    protected static List<PropertyDescriptor> commonDescriptors() {
        final List<PropertyDescriptor> baseDescriptors = new ArrayList<>();
        baseDescriptors.add(HOSTNAME);
        baseDescriptors.add(PORT);
        baseDescriptors.add(SSH_AUTHENTICATION_STRATEGY);
        baseDescriptors.add(USERNAME);
        baseDescriptors.add(PASSWORD);
        baseDescriptors.add(PRIVATE_KEY_PATH);
        baseDescriptors.add(PRIVATE_KEY_PASSPHRASE);
        baseDescriptors.add(REPORT_OUTPUT_FORMAT);
        baseDescriptors.add(REPORT_OUTPUT_PATH);
        baseDescriptors.add(REPORT_OUTPUT_DIRECTORY);
        baseDescriptors.add(RUNREP_USERNAME);
        baseDescriptors.add(RUNREP_PASSWORD);
        baseDescriptors.add(GENEVA_AGA);
        baseDescriptors.add(ACCOUNTING_RUN_TYPE);
        baseDescriptors.add(PORTFOLIO_LIST);
        baseDescriptors.add(PERIOD_START_DATE);
        baseDescriptors.add(PERIOD_END_DATE);
        baseDescriptors.add(KNOWLEDGE_DATE);
        baseDescriptors.add(PRIOR_KNOWLEDGE_DATE);
        baseDescriptors.add(REPORT_CONSOLIDATION);
        baseDescriptors.add(EXTRA_FLAGS);

        // these are SSH connection-specific details, move it to the bottom
        baseDescriptors.add(DATA_TIMEOUT);
        baseDescriptors.add(FileTransfer.CONNECTION_TIMEOUT);
        baseDescriptors.add(SFTPTransfer.USE_KEEPALIVE_ON_TIMEOUT);
        baseDescriptors.add(SFTPTransfer.KEY_ALGORITHMS_ALLOWED);
        baseDescriptors.add(SFTPTransfer.STRICT_HOST_KEY_CHECKING);
        baseDescriptors.add(SFTPTransfer.HOST_KEY_FILE);
        baseDescriptors.add(FileTransfer.USE_COMPRESSION);
        baseDescriptors.add(FileTransfer.USE_COMPRESSION);
        baseDescriptors.add(FTPTransfer.PROXY_TYPE);
        baseDescriptors.add(FTPTransfer.PROXY_HOST);
        baseDescriptors.add(FTPTransfer.PROXY_PORT);
        baseDescriptors.add(FTPTransfer.HTTP_PROXY_USERNAME);
        baseDescriptors.add(FTPTransfer.HTTP_PROXY_PASSWORD);

        return baseDescriptors;
    }

    protected abstract List<PropertyDescriptor> additionalDescriptors();

    protected abstract IStreamHandler getStreamHandler();

    protected abstract ICommand getCommand(final ProcessSession session, final ProcessContext context,
            final FlowFile flowfile) throws IllegalArgumentException;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        descriptors = new ArrayList<>(additionalDescriptors()); // First add the additional descriptors
        descriptors.addAll(commonDescriptors()); // Then add the common descriptors
        descriptors = Collections.unmodifiableList(descriptors); // Make the list unmodifiable

        relationships = new HashSet<>();
        relationships.add(REL_SUCCESS);
        relationships.add(REL_FAILURE);
        relationships.add(REL_GENEVA_FAILURE);
        relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    public RemoteCommandExecutor createOrGetExecutor(final ProcessContext context) {
        if (remoteCommandExecutor == null) {
            remoteCommandExecutor = executorProvider.createExecutor(context, getLogger());
        }

        if (this.sshClientProvider != null) // need this for unit tests
            remoteCommandExecutor.setSSHClientProvider(sshClientProvider);

        return remoteCommandExecutor;
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();
        if (flowFile == null)
            return;

        final StopWatch stopWatch = new StopWatch(true);
        final String host = context.getProperty(HOSTNAME).evaluateAttributeExpressions(flowFile).getValue();
        final int port = context.getProperty(PORT).evaluateAttributeExpressions(flowFile).asInteger();
        final String sshUsername = context.getProperty(USERNAME).evaluateAttributeExpressions(flowFile).getValue();

        // Geneva runrep-specific information
        final String genevaAga = context.getProperty(GENEVA_AGA).evaluateAttributeExpressions(flowFile).getValue();
        final String genevaUser = context.getProperty(RUNREP_USERNAME).evaluateAttributeExpressions(flowFile)
                .getValue();

        try (final RemoteCommandExecutor commandExecutor = createOrGetExecutor(context)) {

            // execute the cmd on the server
            final ICommand command = getCommand(session, context, flowFile);
            final String resultCsvFile = command.getOutputResource();

            final Map<String, String> attributes = new HashMap<>();
            final String protocolName = commandExecutor.getProtocolName();

            attributes.put(protocolName + ".remote.host", host);
            attributes.put(protocolName + ".remote.username", sshUsername);
            attributes.put(protocolName + ".remote.port", String.valueOf(port));
            attributes.put(protocolName + ".remote.filename", resultCsvFile);
            attributes.put("geneva.runrep.aga", genevaAga);
            attributes.put("geneva.runrep.user", genevaUser);
            attributes.put("geneva.runrep.command", command.getLoggablePart());

            flowFile = session.putAllAttributes(flowFile, attributes);

            // It's possible that report runs will fail through no fault of our own
            // This could happen due to no fault of our own (memory, report, invalid params, etc.)
            commandExecutor.execute(command, flowFile, session);

            // The result csv file on the server
            flowFile = commandExecutor.getRemoteFile(context, command, flowFile, session, getStreamHandler());

            final long elapsedMs = stopWatch.getElapsed(TimeUnit.MILLISECONDS);
            flowFile = session.putAttribute(flowFile, "geneva.runrep.elapsedms", String.valueOf(elapsedMs));

            // emit provenance event and transfer FlowFile
            session.getProvenanceReporter().fetch(flowFile,
                    commandExecutor.getProtocolName() + "://" + host + ":" + port + "/" + resultCsvFile, elapsedMs);
            session.transfer(flowFile, REL_SUCCESS);

            final FlowFile finalFlowFile = flowFile;
            session.commitAsync(() -> performCompletion(commandExecutor, command, finalFlowFile));
        } catch (final GenevaException exc) {
            final String failureReason = exc.getGenevaErrorMessage();
            flowFile = session.putAttribute(flowFile, "geneva.runrep.error", failureReason);
            reportFailure(session, flowFile, String.format("Got the error %s while executing command %s.",
                    exc.getGenevaErrorMessage(), exc.getCommand()), exc, REL_GENEVA_FAILURE);
        } catch (final IOException exc) {
            reportFailure(session, flowFile, genevaUser, exc, REL_FAILURE);
            throw new ProcessException("Unexpected error occured.", exc);
        } catch (final IllegalArgumentException exc) {
            throw new ProcessException(exc);
        }
    }

    private void performCompletion(final RemoteCommandExecutor commandExecutor, final ICommand command,
            final FlowFile flowfile) {
        try {
            commandExecutor.deleteFile(command, flowfile);
        } catch (final FileNotFoundException fnfe) {
            // Do nothing, the file is not found
        } catch (final IOException exc) {
            getLogger().warn(String.format(
                    "Successfully ran runrep and got the content from `%s` but something went wrong while clean it up.",
                    command.getOutputResource()), exc);
        }
    }

    private void reportFailure(final ProcessSession session, final FlowFile flowFile, final String error,
            final Exception exception, final Relationship relationship) {
        final ComponentLog logger = getLogger();
        logger.error(error, exception);
        session.transfer(session.penalize(flowFile), relationship);
        session.getProvenanceReporter().route(flowFile, relationship);
    }
}
