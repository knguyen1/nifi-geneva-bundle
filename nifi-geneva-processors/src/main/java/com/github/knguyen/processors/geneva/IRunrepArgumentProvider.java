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

/**
 * Interface IRunrepArgumentProvider defines a contract for providing arguments needed by the RunrepCommand.
 * Implementations of this interface are responsible for supplying values such as Geneva user credentials, Geneva AGA,
 * output filenames, and various other parameters necessary for constructing and executing Runrep commands.
 *
 * This abstraction allows for the decoupling of argument retrieval logic from the RunrepCommand class, enabling more
 * flexible and testable code. Implementations can vary from direct retrievals from a ProcessContext and FlowFile to
 * more complex scenarios involving additional data sources or configurations.
 *
 * <p>
 * Methods:
 * <ul>
 * <li>getGenevaUser: Retrieves the Geneva username.</li>
 * <li>getGenevaPassword: Retrieves the Geneva password.</li>
 * <li>getGenevaAga: Retrieves the Geneva AGA (Application Gateway Architecture) value.</li>
 * <li>getOutputFilename: Determines the output file name for the report.</li>
 * <li>getPortfolioList: Retrieves the portfolio list for the report.</li>
 * <li>getPeriodStartDate: Retrieves the start date for the report period.</li>
 * <li>getPeriodEndDate: Retrieves the end date for the report period.</li>
 * <li>getKnowledgeDate: Retrieves the knowledge date for the report.</li>
 * <li>getPriorKnowledgeDate: Retrieves the prior knowledge date for the report.</li>
 * <li>getAccountingRunType: Retrieves the accounting run type for the report.</li>
 * <li>getReportConsolidation: Retrieves the report consolidation setting.</li>
 * <li>getExtraFlags: Retrieves any extra flags or parameters for the report command.</li>
 * <li>getRSLName: Retrieves the RSL name.</li>
 * <li>validate: Validates logical consistencies of provided arguments.</li>
 * </ul>
 */

public interface IRunrepArgumentProvider {
    String getGenevaUser();

    String getGenevaPassword();

    String getGenevaAga();

    String getOutputFilename();

    String getOutputDirectory();

    String getOutputPath();

    String getPortfolioList();

    String getPeriodStartDate();

    String getPeriodEndDate();

    String getKnowledgeDate();

    String getPriorKnowledgeDate();

    String getAccountingRunType();

    String getReportConsolidation();

    String getExtraFlags();

    String getRSLName();

    /**
     * Validates the provided arguments for the Runrep command.
     *
     * This method is responsible for ensuring that all the necessary arguments required to construct and execute a
     * Runrep command are valid and meet the expected criteria. This could include checks like non-nullity, adherence to
     * specific formats, or meeting certain value constraints.
     *
     * Implementations of this method should throw a specific exception or provide a detailed error message if any of
     * the arguments do not meet the validation criteria. This ensures that any issues with the arguments can be
     * identified and addressed early in the process, prior to attempting to execute the Runrep command.
     *
     * @throws IllegalArgumentException
     *             If any of the arguments do not meet the validation criteria.
     */
    void validate() throws IllegalArgumentException;
}
