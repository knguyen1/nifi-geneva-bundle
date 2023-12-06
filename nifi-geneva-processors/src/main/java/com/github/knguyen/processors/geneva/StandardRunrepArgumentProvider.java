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

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.StringUtils;

/**
 * The {@code StandardRunrepArgumentProvider} class implements the {@code IRunrepArgumentProvider} interface, providing
 * a standardized way to retrieve arguments required for the Runrep process within a Geneva system context. This class
 * specifically caters to extracting and validating necessary parameters from Apache NiFi's {@code ProcessContext} and a
 * {@code FlowFile}. These parameters include user credentials, file paths, dates, and other specific settings required
 * for the successful execution of the Runrep process.
 *
 * Usage of this class is essential in scenarios where dynamic resolution of arguments based on flowfile attributes and
 * process context properties is required, ensuring that the Runrep process is supplied with accurate and valid data.
 *
 * <p>
 * Key functionalities include:
 * <ul>
 * <li>Retrieving various arguments such as user credentials, file paths, portfolio lists, date ranges, and other
 * Geneva-specific settings.</li>
 * <li>Dynamic evaluation of arguments based on the flowfile attributes.</li>
 * <li>Validation of arguments to ensure their integrity and correctness before they are passed to the Runrep
 * process.</li>
 * </ul>
 *
 * <p>
 * This class plays a crucial role in scenarios where Apache NiFi processes interact with Geneva systems, ensuring the
 * smooth and error-free transfer of necessary parameters.
 *
 * @implNote This class is designed to be used within Apache NiFi processors and assumes that the flowfiles processed
 *           through these processors adhere to the standards and requirements of Geneva systems.
 */
public class StandardRunrepArgumentProvider implements IRunrepArgumentProvider {
    private final ProcessContext context;
    private final FlowFile flowfile;

    /**
     * Constructs a new {@code StandardRunrepArgumentProvider} instance with the specified Apache NiFi
     * {@code ProcessContext} and {@code FlowFile}. This constructor initializes the provider with the context and
     * flowfile required for extracting and evaluating the necessary arguments for the Runrep process in a Geneva system
     * environment.
     *
     * @param context
     *            The {@code ProcessContext} providing access to processor configuration and system information. It is
     *            used to retrieve property values relevant to the Runrep process execution.
     * @param flowfile
     *            The {@code FlowFile} representing the data flow within Apache NiFi. It is utilized for dynamically
     *            evaluating attribute expressions that may be embedded in the processor properties, allowing for a
     *            flexible and dynamic argument resolution based on the flowfile content.
     *
     * @throws NullPointerException
     *             if either {@code context} or {@code flowfile} is null, ensuring that the provider is initialized with
     *             valid references for its operation.
     */
    public StandardRunrepArgumentProvider(final ProcessContext context, final FlowFile flowfile) {
        this.context = context;
        this.flowfile = flowfile;
    }

    /**
     * Retrieves the Geneva system username.
     *
     * @return A {@code String} representing the Geneva system username. It may return {@code null} if the property is
     *         not set or if the dynamic evaluation against the flowfile results in no value.
     */
    @Override
    public String getGenevaUser() {
        return context.getProperty(BaseExecuteGeneva.RUNREP_USERNAME).evaluateAttributeExpressions(flowfile).getValue();
    }

    /**
     * Retrieves the Geneva system password.
     *
     * @return A {@code String} representing the Geneva system password. It may return {@code null} if the property is
     *         not set or if the dynamic evaluation against the flowfile results in no value.
     */
    @Override
    public String getGenevaPassword() {
        return context.getProperty(BaseExecuteGeneva.RUNREP_PASSWORD).evaluateAttributeExpressions(flowfile).getValue();
    }

    /**
     * Retrieves the Advent Global Area (AGA) identifier for the Geneva system from the Apache NiFi
     * {@code ProcessContext}. This method is responsible for extracting the AGA setting, which is a crucial
     * configuration for specifying the operational context within the Geneva system. The AGA identifier helps in
     * determining the specific data partition or regional settings that the Runrep process should operate upon.
     *
     * The AGA value is fetched by evaluating the GENEVA_AGA property in the provided context, with respect to the
     * attributes present in the current {@code FlowFile}. This approach allows for dynamic resolution of the AGA value,
     * ensuring flexibility and adaptability in varied data flow scenarios.
     *
     * @return A {@code String} representing the Advent Global Area (AGA) identifier. The return value could be
     *         {@code null} if the GENEVA_AGA property is not set in the context or if the attribute expression
     *         evaluation against the flowfile does not yield a value.
     */
    @Override
    public String getGenevaAga() {
        return context.getProperty(BaseExecuteGeneva.GENEVA_AGA).evaluateAttributeExpressions(flowfile).getValue();
    }

    /**
     * Retrieves the filename for the output report generated by the Runrep process in the Geneva system. This method
     * fetches the filename by evaluating the REPORT_OUTPUT_PATH property from the Apache NiFi {@code ProcessContext},
     * dynamically considering the attributes of the current {@code FlowFile}. The output filename is essential to
     * identify and store the report generated by the Runrep process.
     *
     * @return A {@code String} representing the output report filename. It could return {@code null} if the
     *         REPORT_OUTPUT_PATH property is not set or if the dynamic attribute expression evaluation against the
     *         flowfile does not resolve to a value.
     */
    @Override
    public String getOutputFilename() {
        return context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_PATH).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    /**
     * Retrieves the directory path where the output report generated by the Runrep process is to be stored. This method
     * obtains the directory path by evaluating the REPORT_OUTPUT_DIRECTORY property in the Apache NiFi
     * {@code ProcessContext}, with respect to the attributes in the current {@code FlowFile}. This directory path is
     * crucial for determining where the generated report should be saved.
     *
     * @return A {@code String} representing the directory path for storing the output report. If the
     *         REPORT_OUTPUT_DIRECTORY property is not defined or if the attribute expression against the flowfile does
     *         not yield a value, this method may return {@code null}.
     */
    @Override
    public String getOutputDirectory() {
        return context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_DIRECTORY).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    /**
     * Constructs and returns the complete output path for the Runrep process report in the Geneva system. This method
     * combines the output filename and directory, obtained from getOutputFilename() and getOutputDirectory() methods
     * respectively, to form the full path for storing the report. If only the directory is provided, it generates a
     * unique filename within that directory.
     *
     * @return A {@code String} representing the full output path for the report. It includes both the directory and the
     *         filename, ensuring a complete path is available for report storage. The method may return {@code null} if
     *         neither an output filename nor directory is available.
     */
    @Override
    public String getOutputPath() {
        final String outputFilename = getOutputFilename();
        if (StringUtils.isNotBlank(outputFilename))
            return outputFilename;

        final String outputDirectory = getOutputDirectory();
        return com.github.knguyen.processors.utils.StringUtils.getGuidFilename(outputDirectory, flowfile);
    }

    /**
     * Retrieves the list of portfolios to be processed or reported on by the Runrep process in the Geneva system. This
     * method extracts the portfolio list from the Apache NiFi {@code ProcessContext}, by evaluating the PORTFOLIO_LIST
     * property. The evaluation takes into account any dynamic expressions based on the current {@code FlowFile}'s
     * attributes, allowing for flexibility in specifying the portfolios based on the flow of data.
     *
     * The portfolio list is a critical parameter for the Runrep process, determining the scope of the financial data or
     * reports to be generated. This list can contain one or more portfolio identifiers, typically separated by commas.
     *
     * @return A {@code String} representing the list of portfolios. It could return {@code null} if the PORTFOLIO_LIST
     *         property is not defined in the context, or if the dynamic evaluation against the flowfile does not result
     *         in any value. In scenarios where a specific set of portfolios needs to be processed dynamically, this
     *         method ensures the necessary configurability and adaptability.
     */
    @Override
    public String getPortfolioList() {
        return context.getProperty(BaseExecuteGeneva.PORTFOLIO_LIST).evaluateAttributeExpressions(flowfile).getValue();
    }

    /**
     * Retrieves the start date for the reporting period from the Apache NiFi {@code ProcessContext}. This method is
     * crucial for defining the temporal scope of the report generated by the Runrep process. It extracts the period
     * start date by evaluating the PERIOD_START_DATE property in the context, considering any attribute expressions
     * based on the current {@code FlowFile}.
     *
     * The ability to dynamically resolve the start date based on flowfile attributes allows for flexibility in handling
     * different reporting requirements and scenarios. This method ensures that the report encompasses the correct time
     * frame as specified in the process configuration.
     *
     * @return A {@code String} representing the start date of the reporting period. The format of the date is typically
     *         expected to conform to standard date formats. The method may return {@code null} if the PERIOD_START_DATE
     *         property is not set or if the attribute expression evaluation does not yield a value.
     */
    @Override
    public String getPeriodStartDate() {
        return context.getProperty(BaseExecuteGeneva.PERIOD_START_DATE).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    /**
     * Retrieves the end date for the reporting period from the Apache NiFi {@code ProcessContext}. This method plays a
     * key role in defining the end boundary of the time frame for which the report is generated in the Runrep process.
     * It fetches the period end date by evaluating the PERIOD_END_DATE property in the context, dynamically considering
     * the attributes of the current {@code FlowFile}.
     *
     * The dynamic resolution of the end date ensures that the reporting period can be adjusted based on specific data
     * flows, allowing for tailored reporting periods in various operational contexts.
     *
     * @return A {@code String} representing the end date of the reporting period. The returned date is expected to
     *         follow standard date formats. The method might return {@code null} if the PERIOD_END_DATE property is not
     *         set or if the dynamic evaluation against the flowfile does not result in a value.
     */
    @Override
    public String getPeriodEndDate() {
        return context.getProperty(BaseExecuteGeneva.PERIOD_END_DATE).evaluateAttributeExpressions(flowfile).getValue();
    }

    /**
     * Retrieves the 'knowledge date' for the report from the Apache NiFi {@code ProcessContext}. The knowledge date is
     * typically used to indicate the reference date for which the report's data is relevant or accurate. This method
     * obtains the knowledge date by evaluating the KNOWLEDGE_DATE property, considering the attributes present in the
     * current {@code FlowFile}.
     *
     * This functionality is crucial for ensuring that reports generated by the Runrep process are based on the most
     * relevant and timely data for a specific date.
     *
     * @return A {@code String} representing the knowledge date for the report. The format of this date is expected to
     *         conform to standard date formats. The method may return {@code null} if the KNOWLEDGE_DATE property is
     *         not defined or if the attribute expression evaluation does not yield a value.
     */
    @Override
    public String getKnowledgeDate() {
        return context.getProperty(BaseExecuteGeneva.KNOWLEDGE_DATE).evaluateAttributeExpressions(flowfile).getValue();
    }

    /**
     * Retrieves the 'prior knowledge date' for the report from the Apache NiFi {@code ProcessContext}. This date is
     * used to indicate a historical reference point, typically preceding the primary knowledge date, and is important
     * for certain types of financial or operational reporting. The method fetches this date by evaluating the
     * PRIOR_KNOWLEDGE_DATE property, taking into account the attributes in the current {@code FlowFile}.
     *
     * This ability to specify a prior knowledge date is essential for reports that require comparison or analysis of
     * data over different time frames.
     *
     * @return A {@code String} representing the prior knowledge date. This date is generally in standard date format.
     *         If the PRIOR_KNOWLEDGE_DATE property is not set or if the dynamic evaluation against the flowfile does
     *         not result in a value, the method might return {@code null}.
     */
    @Override
    public String getPriorKnowledgeDate() {
        return context.getProperty(BaseExecuteGeneva.PRIOR_KNOWLEDGE_DATE).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    /**
     * Retrieves the accounting run type for the report from the Apache NiFi {@code ProcessContext}. This method is
     * critical for specifying the nature of the accounting process to be applied in the Runrep report generation. It
     * fetches the accounting run type by evaluating the ACCOUNTING_RUN_TYPE property in the context, considering the
     * attributes in the current {@code FlowFile}.
     *
     * The accounting run type can be one of the following predefined values:
     * <ul>
     * <li>Dynamic - The default mode for most reports, indicating a dynamic accounting run.</li>
     * <li>ClosedPeriod - For reports under 'ClosedPeriod' accounting, where PNLs and accruals are treated
     * differently.</li>
     * <li>UnAmendedClosedPeriod - Represents an UnAmendedClosedPeriod accounting run.</li>
     * <li>Incremental - An incremental accounting run.</li>
     * <li>NAV - NAV accounting run.</li>
     * <li>WouldBeAdjustments - A WouldBeAdjustments accounting run.</li>
     * <li>TWR - TWR accounting run.</li>
     * <li>Snapshot - Snapshot accounting run.</li>
     * </ul>
     *
     * This functionality enables the selection of an appropriate accounting methodology for the report, ensuring
     * accuracy and compliance with specific financial reporting requirements.
     *
     * @return A {@code String} representing the chosen accounting run type. The method returns the default value
     *         'Dynamic' if the ACCOUNTING_RUN_TYPE property is not set or the attribute expression evaluation against
     *         the flowfile does not result in a value.
     */
    @Override
    public String getAccountingRunType() {
        return context.getProperty(BaseExecuteGeneva.ACCOUNTING_RUN_TYPE).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    /**
     * Retrieves the report consolidation preference from the Apache NiFi {@code ProcessContext}. This method is
     * essential for determining how the report generated by the Runrep process should be consolidated. It extracts the
     * consolidation preference by evaluating the REPORT_CONSOLIDATION property in the context, dynamically considering
     * the attributes of the current {@code FlowFile}.
     *
     * The consolidation preference can be one of the following:
     * <ul>
     * <li>All - For generating consolidated reports.</li>
     * <li>GroupsOnly - For creating group consolidated reports.</li>
     * <li>None - For generating iterated reports with no consolidation (default behavior).</li>
     * </ul>
     *
     * The ability to specify different consolidation preferences allows for customization of the report format based on
     * specific requirements or operational contexts.
     *
     * @return A {@code String} representing the selected consolidation preference for the report. The value corresponds
     *         to one of the predefined allowable values (All, GroupsOnly, None). If the REPORT_CONSOLIDATION property
     *         is not set or the evaluation against the flowfile does not yield a value, the method defaults to 'None'
     *         for no consolidation.
     */
    @Override
    public String getReportConsolidation() {
        return context.getProperty(BaseExecuteGeneva.REPORT_CONSOLIDATION).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    /**
     * Retrieves additional flags or arguments for the 'runrep' command from the Apache NiFi {@code ProcessContext}.
     * Given the extensive range of flags and toggles supported by 'runrep', it is impractical to accommodate all of
     * them individually in a NiFi processor. Hence, the 'extra flags' functionality serves as a catch-all mechanism to
     * include any extra parameters or expressions that are necessary for a specific report configuration.
     *
     * Users are responsible for correctly formatting these extra flags, ensuring proper escaping of spaces and special
     * characters, as 'runrep' is a command-line interface (CLI) tool. This method extracts these additional flags by
     * evaluating the EXTRA_FLAGS property in the context, taking into account the current {@code FlowFile}'s
     * attributes.
     *
     * This flexibility allows users to tailor the 'runrep' command to their specific reporting requirements, providing
     * a means to leverage the full power of the 'runrep' CLI beyond the standard set of parameters.
     *
     * @return A {@code String} representing the extra flags or arguments to be passed to the 'runrep' command. The
     *         method might return {@code null} if the EXTRA_FLAGS property is not set or if the attribute expression
     *         evaluation against the flowfile does not result in a value.
     */
    @Override
    public String getExtraFlags() {
        return context.getProperty(BaseExecuteGeneva.EXTRA_FLAGS).evaluateAttributeExpressions(flowfile).getValue();
    }

    /**
     * Retrieves the name of the Report Specification Language (RSL) script from the Apache NiFi {@code ProcessContext}.
     * This method is integral for specifying the RSL script, akin to an SQL stored procedure, which defines the
     * structure and content of the financial report generated by the Runrep process. The RSL script is executed against
     * an Advent Global Area (AGA) to retrieve the desired financial data.
     *
     * The RSL script name is obtained by evaluating the RSL_NAME property in the context, dynamically considering the
     * attributes of the current {@code FlowFile}. This approach allows for the selection of different RSL scripts based
     * on the specific requirements of the data flow or reporting needs.
     *
     * RSL, being Advent's SQL-like language, enables complex and customized report definitions, making this method
     * crucial for tailoring the report output to specific financial reporting standards and organizational needs.
     *
     * @return A {@code String} representing the name of the RSL script. The method may return {@code null} if the
     *         RSL_NAME property is not set or if the dynamic evaluation against the flowfile does not yield a value.
     */
    @Override
    public String getRSLName() {
        return context.getProperty(ExecuteGenevaRSL.RSL_NAME).evaluateAttributeExpressions(flowfile).getValue();
    }

    @Override
    public void validate() throws IllegalArgumentException, NullPointerException {
        validateUserCredentials();
        // validateOutputPath(); // does not work during unit tests
        validatePortfolioList();
        validateDateLogic();
    }

    private void validateUserCredentials() {
        Objects.requireNonNull(getGenevaUser(), "`runrep` user cannot be null");
        Objects.requireNonNull(getGenevaPassword(), "`runrep` password cannot be null");
    }

    private void validateOutputPath() {
        // Validate output path
        final String outputPath = getOutputPath();
        final File outputFile = new File(outputPath);

        // If the path is a file, check if the parent directory exists
        if (outputFile.isFile()) {
            File parentDir = outputFile.getParentFile();
            if (parentDir == null || !parentDir.isDirectory()) {
                throw new IllegalArgumentException("Parent directory of the output file does not exist: " + outputPath);
            }
        } else if (!outputFile.isDirectory()) {
            // If it's not a file, then it should be a directory; otherwise, throw an exception
            throw new IllegalArgumentException("Output path is neither a file nor a directory: " + outputPath);
        }
    }

    private void validatePortfolioList() {
        // Validate portfolio list
        if (StringUtils.isNotBlank(getPortfolioList())) {
            String[] portfolios = getPortfolioList().split(",");
            for (String portfolio : portfolios) {
                portfolio = portfolio.trim();
                if (portfolio.contains(" ") && !(portfolio.startsWith("\\\"") && portfolio.endsWith("\\\""))) {
                    throw new IllegalArgumentException(String.format(
                            "Portfolio argument `%s` contains space that was not propertly escaped, e.g. `123,\\\"My Portfolio\\\",456`.",
                            getPortfolioList()));
                }
            }
        }
    }

    private void validateDateLogic() {
        // Date validations
        final LocalDateTime periodStartDate = validateDateArgument(getPeriodStartDate(),
                BaseExecuteGeneva.PERIOD_START_DATE.getDisplayName());
        final LocalDateTime periodEndDate = validateDateArgument(getPeriodEndDate(),
                BaseExecuteGeneva.PERIOD_END_DATE.getDisplayName());
        final LocalDateTime knowledgeDate = validateDateArgument(getKnowledgeDate(),
                BaseExecuteGeneva.KNOWLEDGE_DATE.getDisplayName());
        final LocalDateTime priorKnowledgeDate = validateDateArgument(getPriorKnowledgeDate(),
                BaseExecuteGeneva.PRIOR_KNOWLEDGE_DATE.getDisplayName());

        // Check for logical cohesion in dates provided
        if (periodStartDate != null && periodEndDate != null) {
            if (periodStartDate.isAfter(periodEndDate)) {
                throw new IllegalArgumentException(
                        String.format("`periodStartDate` (%s) must be greater than or equal to `periodEndDate` (%s).",
                                periodStartDate, periodEndDate));
            }
        }

        // Check that if `ClosedPeriod` accounting is selected, `priorKnowledgeDate` must be provided
        final String accountingRunType = getAccountingRunType();
        if (!StringUtils.isNotBlank(accountingRunType)) {
            if (BaseExecuteGeneva.CLOSED_PERIOD_ACCOUNTING.getValue().equals(accountingRunType)) {
                if (priorKnowledgeDate == null) {
                    throw new IllegalArgumentException(
                            String.format("`%s` accounting was selected, `priorKnowledgeDate` cannot be null.",
                                    BaseExecuteGeneva.CLOSED_PERIOD_ACCOUNTING.getDisplayName()));
                }
            }
        }
    }

    /**
     * Validates and parses a given date string into a LocalDateTime object.
     *
     * This method takes a date string and attempts to parse it as a LocalDateTime. If the string is non-blank and
     * valid, it returns the parsed LocalDateTime object. If the string is either blank or invalid, it handles the
     * scenarios accordingly. For a blank string, the method returns null, indicating no date value provided. For an
     * invalid string, it throws an IllegalArgumentException with a detailed message about the parsing failure.
     *
     * This method simplifies date parsing and validation across different date fields, providing a centralized way to
     * handle common date-related errors and exceptions.
     *
     * @param dateStr
     *            The date string to be parsed.
     * @param propertyName
     *            The name of the property that this date string represents, used for error messaging.
     *
     * @return LocalDateTime parsed from the provided date string, or null if the string is blank.
     *
     * @throws IllegalArgumentException
     *             If the date string is non-blank and cannot be parsed into a LocalDateTime.
     */
    private LocalDateTime validateDateArgument(String dateStr, String propertyName) throws IllegalArgumentException {
        if (StringUtils.isNotBlank(dateStr)) {
            try {
                return LocalDateTime.parse(dateStr);
            } catch (final DateTimeParseException exc) {
                throw new IllegalArgumentException(
                        String.format("Cannot parse value `%s` from `%s`.", dateStr, propertyName), exc);
            }
        }
        return null;
    }
}
