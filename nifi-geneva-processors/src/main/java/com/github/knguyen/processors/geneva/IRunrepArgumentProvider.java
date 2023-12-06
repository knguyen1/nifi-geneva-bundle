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
 * Methods: - getGenevaUser: Retrieves the Geneva username. - getGenevaPassword: Retrieves the Geneva password. -
 * getGenevaAga: Retrieves the Geneva AGA (Application Gateway Architecture) value. - getOutputFilename: Determines the
 * output file name for the report. - getPortfolioList: Retrieves the portfolio list for the report. -
 * getPeriodStartDate: Retrieves the start date for the report period. - getPeriodEndDate: Retrieves the end date for
 * the report period. - getKnowledgeDate: Retrieves the knowledge date for the report. - getPriorKnowledgeDate:
 * Retrieves the prior knowledge date for the report. - getAccountingRunType: Retrieves the accounting run type for the
 * report. - getReportConsolidation: Retrieves the report consolidation setting. - getExtraFlags: Retrieves any extra
 * flags or parameters for the report command. - getRSLName: Retrieves the RSL name.
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
}
