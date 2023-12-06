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

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;

public abstract class RunrepCommand implements ICommand {

    protected final IRunrepArgumentProvider argumentProvider;

    protected String commandStr;
    protected String outputResource;
    protected String obfuscatedCommand;

    protected RunrepCommand(final IRunrepArgumentProvider argumentProvider) {
        this.argumentProvider = argumentProvider;
        init();
    }

    /**
     * Constructs a Command object representing both obfuscated and unobfuscated forms of a runrep command. This method
     * combines various parts of the runrep command, including initialization, connection, report parameters, and exit
     * sequences. It ensures that sensitive information in the command is obfuscated, while also maintaining the
     * original unobfuscated command for execution purposes. The method leverages other protected methods to construct
     * different parts of the command and then assembles them into a single command string.
     *
     * @return An ICommand object containing both the complete unobfuscated command and its obfuscated counterpart,
     *         suitable for execution and logging purposes respectively.
     */
    protected void init() {
        final String runrepInitStr = getRunrepInitStr();
        final Pair<String, String> runrepConnectStr = getRunrepConnectStr();
        final String reportCommandStr = getReportCommand();
        final String runrepExitStr = getRunrepExitStr();

        final String runrepCommand = String.format("%s%n%s%n%s%n%s", runrepInitStr, runrepConnectStr.getLeft(),
                reportCommandStr, runrepExitStr);
        final String obfuscatedRunrepCommand = String.format("%s%n%s%n%s%n%s", runrepInitStr,
                runrepConnectStr.getRight(), reportCommandStr, runrepExitStr);

        this.commandStr = runrepCommand;
        this.obfuscatedCommand = obfuscatedRunrepCommand;
        this.outputResource = getOuputFilename();
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
     * Retrieves the string command used to exit the 'runrep' process.
     *
     * This method returns a string that combines an 'exit' command followed by an 'EOF' (End Of File) marker. This
     * combination signals the end of an input stream.
     *
     * @return A string "exit\nEOF" indicating the commands to terminate the 'runrep' process.
     */
    protected String getRunrepExitStr() {
        return "exit\nEOF\n";
    }

    /**
     * Constructs and returns a command string for executing the Geneva utility along with its obfuscated version. This
     * method extracts necessary credentials and other parameters from the given {@link ProcessContext} and
     * {@link FlowFile}, formats them into a Geneva utility command, and also creates an obfuscated version of the
     * command where sensitive information such as the password is masked.
     *
     * @return A {@link Pair} of {@link String}s, where the first element is the actual command and the second element
     *         is the obfuscated version of the command with sensitive information masked.
     */
    protected Pair<String, String> getRunrepConnectStr() {
        final String genevaUser = argumentProvider.getGenevaUser();
        final String genevaPassword = argumentProvider.getGenevaPassword();
        final String genevaAga = argumentProvider.getGenevaAga();

        final String command = String.format("connect %s/%s -k %s", genevaUser, genevaPassword, genevaAga);
        final String obsCommand = String.format("connect %s/%s -k %s", genevaUser, "*********", genevaAga);

        return Pair.of(command, obsCommand);
    }

    protected abstract String getReportCommand();

    /**
     * Determines the output file name for the report based on the processor context and a flowfile.
     *
     * Initially, it tries to get the output filename from the 'REPORT_OUTPUT_PATH' property. If this property is set
     * and not blank, it returns this value as the output file name.
     *
     * If 'REPORT_OUTPUT_PATH' is not set or is blank, it uses the 'REPORT_OUTPUT_DIRECTORY' property and appends a
     * filename generated using the flowfile's UUID to it. This new path is returned as the output file name.
     *
     * @return A string representing the output file name.
     */
    protected final String getOuputFilename() {
        return argumentProvider.getOutputPath();
    }

    /**
     * Constructs a string of report parameters based on the processor context and a flowfile.
     *
     * This method formats each property's value into a string parameter if the property value is set and not blank. The
     * parameters are then joined together into a single string with a space between each one.
     *
     * The method handles the formatting of the following properties: 'PORTFOLIO_LIST', 'PERIOD_START_DATE',
     * 'PERIOD_END_DATE', 'KNOWLEDGE_DATE', 'PRIOR_KNOWLEDGE_DATE', 'ACCOUNTING_RUN_TYPE', 'REPORT_CONSOLIDATION', and
     * 'EXTRA_FLAGS'. See individual formatting methods for more details on how each property is handled.
     *
     * @return A string of report parameters.
     */
    protected String getReportParameters() {
        return Stream
                .of(formatParameter("-p", argumentProvider.getPortfolioList()),
                        formatParameter("-ps", argumentProvider.getPeriodStartDate()),
                        formatParameter("-pe", argumentProvider.getPeriodEndDate()),
                        formatParameter("-k", argumentProvider.getKnowledgeDate()),
                        formatParameter("-pk", argumentProvider.getPriorKnowledgeDate()), formatAccountingRunType(),
                        formatReportConsolidation(), formatExtraFlags())
                .filter(Objects::nonNull).collect(Collectors.joining(" "));
    }

    /**
     * Formats a parameter name and its value into a string if the value is set and not blank.
     *
     * This method takes a parameter name and its value as inputs. If the parameter value is not blank, it returns a
     * string in the form of "{paramName} {paramValue}". If the parameter value is blank, the function returns null.
     *
     * @return A formatted string of the parameter name and its value, or null if the value is blank.
     */
    protected String formatParameter(final String paramName, final String paramValue) {
        return org.apache.nifi.util.StringUtils.isNotBlank(paramValue) ? String.format("%s %s", paramName, paramValue)
                : null;
    }

    /**
     * Retrieves and formats extra flags, if any, from the ProcessContext.
     *
     * This method retrieves the extra flags property from the ProcessContext. If the extra flags property value is not
     * blank, it returns the trimmed property value. If the value is blank, the function returns null.
     *
     * @return The trimmed string of extra flags, or null if the extra flags property value is blank.
     */
    protected String formatExtraFlags() {
        final String extraFlags = argumentProvider.getExtraFlags();
        return org.apache.nifi.util.StringUtils.isNotBlank(extraFlags) ? extraFlags.trim() : null;
    }

    /**
     * Retrieves and formats the report consolidation property from the ProcessContext, if available.
     *
     * This method retrieves the report consolidation property from the ProcessContext. If the property value is not
     * equal to 'NONE_CONSOLIDATED' and is not blank, it returns the property value. If the property value is
     * 'NONE_CONSOLIDATED', null, or blank, the function returns null.
     *
     * @return The string of the report consolidation property value, or null if the value is 'NONE_CONSOLIDATED', null,
     *         or blank.
     */
    protected String formatReportConsolidation() {
        String consolidationValue = argumentProvider.getReportConsolidation();

        // Check if the value is not NONE_CONSOLIDATED and not null/blank
        if (!BaseExecuteGeneva.NONE_CONSOLIDATED.getValue().equals(consolidationValue)
                && org.apache.nifi.util.StringUtils.isNotBlank(consolidationValue)) {
            return consolidationValue;
        }

        // Return null if it's NONE_CONSOLIDATED, null, or blank
        return null;
    }

    /**
     * Generates a parameter string for the accounting run type if it's not dynamic accounting or blank. The parameter
     * string will be in the format `-at {value}`.
     *
     * @return The parameter string or null if the condition is not met.
     */
    protected String formatAccountingRunType() {
        final String accountingRunType = argumentProvider.getAccountingRunType();

        // Check if the accounting run type is not dynamic and not blank
        if (!BaseExecuteGeneva.DYNAMIC_ACCOUNTING.getValue().equals(accountingRunType)
                && org.apache.nifi.util.StringUtils.isNotBlank(accountingRunType)) {
            return String.format("%s %s", BaseExecuteGeneva.ACCOUNTING_RUN_TYPE.getName(), accountingRunType);
        }

        // Return null if dynamic accounting or blank
        return null;
    }

    @Override
    public String getCommand() {
        return this.commandStr;
    }

    @Override
    public String getObfuscatedCommand() {
        return this.obfuscatedCommand;
    }

    @Override
    public String getLoggablePart() {
        return getObfuscatedCommand();
    }

    @Override
    public String getOutputResource() {
        return this.outputResource;
    }
}
