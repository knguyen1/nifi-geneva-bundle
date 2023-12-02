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
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.processors.standard.ssh.SSHClientProvider;
import org.apache.nifi.processors.standard.ssh.StandardSSHClientProvider;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processors.standard.util.FileTransfer;
import org.apache.nifi.processors.standard.util.SFTPTransfer;
import org.apache.commons.lang3.tuple.Pair;

import com.github.knguyen.processors.ssh.SSHCommandExecutor;
import com.github.knguyen.processors.utils.CustomValidators;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseExecuteGeneva extends AbstractProcessor {

    protected static final SSHClientProvider SSH_CLIENT_PROVIDER = new StandardSSHClientProvider();

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
            .fromPropertyDescriptor(SFTPTransfer.DATA_TIMEOUT)
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

    static final PropertyDescriptor PORTFOLIO_LIST = new PropertyDescriptor.Builder().name("portfolio")
            .displayName("Portfolio List")
            .description(
                    "Specifies portfolios as a comma-separated list. Enclose names containing spaces in escaped quotes, e.g., `MyPortfolio1,\\\"9000 International Fixed Income\\\",MyOtherPortfolio`.")
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false)
            .addValidator(Validator.VALID).build();

    static final PropertyDescriptor PERIOD_START_DATE = new PropertyDescriptor.Builder().name("periodstartdate")
            .displayName("Period Start Date")
            .description("Specifies the period start date using ISO Date Time Format, i.e. yyyy-MM-dd'T'HH:mm:ss")
            .addValidator(CustomValidators.DATETIME_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false).build();

    static final PropertyDescriptor PERIOD_END_DATE = new PropertyDescriptor.Builder().name("periodenddate")
            .displayName("Period End Date")
            .description("Specifies the period end date using ISO Date Time Format, i.e. yyyy-MM-dd'T'HH:mm:ss")
            .addValidator(CustomValidators.DATETIME_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false).build();

    static final PropertyDescriptor KNOWLEDGE_DATE = new PropertyDescriptor.Builder().name("knowledgedate")
            .displayName("Knowledge Date")
            .description("Specifies the knowledge date using ISO Date Time Format, i.e. yyyy-MM-dd'T'HH:mm:ss")
            .addValidator(CustomValidators.DATETIME_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false).build();

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
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(false).build();

    static final PropertyDescriptor EXTRA_FLAGS = new PropertyDescriptor.Builder().displayName("Extra Flags")
            .name("extra-flags")
            .description(
                    "Use this field to specify any other flags.  You are responsible for correctly escaping any fields with special characters and spaces.  The arguments specified here will be presented as-is during a report run.")
            .addValidator(Validator.VALID).required(false)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).build();

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
            .defaultValue(NONE_CONSOLIDATED.getValue()).required(false).build();

    static final Relationship REL_SUCCESS = new Relationship.Builder().name("success")
            .description("Any Geneva query that executed without errors will be routed to success").build();
    static final Relationship REL_FAILURE = new Relationship.Builder().name("failure")
            .description("Any Geneva query that executed with errors will be routed to failure").build();

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
        baseDescriptors.add(DATA_TIMEOUT);
        baseDescriptors.add(SFTPTransfer.CONNECTION_TIMEOUT);
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

        return baseDescriptors;
    }

    protected abstract List<PropertyDescriptor> additionalDescriptors();

    @Override
    protected void init(final ProcessorInitializationContext context) {
        descriptors = commonDescriptors();
        descriptors.addAll(additionalDescriptors());
        descriptors = Collections.unmodifiableList(descriptors);

        relationships = new HashSet<>();
        relationships.add(REL_SUCCESS);
        relationships.add(REL_FAILURE);
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

    public RemoteCommandExecutor createExecutor(final ProcessContext context) {
        return new SSHCommandExecutor(context, getLogger());
    }

    /**
     * Generates and returns the initialization string for the Runrep command. This method constructs the string used to
     * initialize the Runrep utility, specifying an empty list file and beginning a command block. The returned string
     * is typically used as the first part of a script or command sequence for interacting with the Runrep utility.
     *
     * @return A {@link String} representing the initialization command for Runrep, indicating the start of a command
     *         sequence with an empty list file.
     */
    protected String getRunrepInitStr() {
        return "runrep -f empty.lst -b << EOF";
    }

    /**
     * Constructs and returns a command string for executing the Geneva utility along with its obfuscated version. This
     * method extracts necessary credentials and other parameters from the given {@link ProcessContext} and
     * {@link FlowFile}, formats them into a Geneva utility command, and also creates an obfuscated version of the
     * command where sensitive information such as the password is masked.
     *
     * @param context
     *            The processing context to obtain property values related to the Geneva utility.
     * @param flowfile
     *            The flow file providing additional attribute expressions for command construction.
     *
     * @return A {@link Pair} of {@link String}s, where the first element is the actual command and the second element
     *         is the obfuscated version of the command with sensitive information masked.
     */
    protected Pair<String, String> getRunrepConnectStr(final ProcessContext context, final FlowFile flowfile) {
        final String genevaUser = context.getProperty(RUNREP_USERNAME).evaluateAttributeExpressions(flowfile)
                .getValue();
        final String genevaPassword = context.getProperty(RUNREP_PASSWORD).evaluateAttributeExpressions(flowfile)
                .getValue();
        final String genevaAga = context.getProperty(GENEVA_AGA).evaluateAttributeExpressions(flowfile).getValue();

        final String command = String.format("connect %s/%s -k %s", genevaUser, genevaPassword, genevaAga);
        final String obfuscatedCommand = String.format("connect %s/%s -k %s", genevaUser, "*********", genevaAga);

        return Pair.of(command, obfuscatedCommand);
    }

    protected abstract String constructReportParameters(final ProcessContext context, final FlowFile flowfile);
}
