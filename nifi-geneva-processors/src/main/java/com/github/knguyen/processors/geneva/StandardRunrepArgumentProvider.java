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

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.StringUtils;

public class StandardRunrepArgumentProvider implements IRunrepArgumentProvider {
    private final ProcessContext context;
    private final FlowFile flowfile;

    public StandardRunrepArgumentProvider(final ProcessContext context, final FlowFile flowfile) {
        this.context = context;
        this.flowfile = flowfile;
    }

    @Override
    public String getGenevaUser() {
        return context.getProperty(BaseExecuteGeneva.RUNREP_USERNAME).evaluateAttributeExpressions(flowfile).getValue();
    }

    @Override
    public String getGenevaPassword() {
        return context.getProperty(BaseExecuteGeneva.RUNREP_PASSWORD).evaluateAttributeExpressions(flowfile).getValue();
    }

    @Override
    public String getGenevaAga() {
        return context.getProperty(BaseExecuteGeneva.GENEVA_AGA).evaluateAttributeExpressions(flowfile).getValue();
    }

    @Override
    public String getOutputFilename() {
        return context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_PATH).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    @Override
    public String getOutputDirectory() {
        return context.getProperty(BaseExecuteGeneva.REPORT_OUTPUT_DIRECTORY).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    @Override
    public String getOutputPath() {
        final String outputFilename = getOutputFilename();
        if (StringUtils.isNotBlank(outputFilename))
            return outputFilename;

        final String outputDirectory = getOutputDirectory();
        return com.github.knguyen.processors.utils.StringUtils.getGuidFilename(outputDirectory, flowfile);
    }

    @Override
    public String getPortfolioList() {
        return context.getProperty(BaseExecuteGeneva.PORTFOLIO_LIST).evaluateAttributeExpressions(flowfile).getValue();
    }

    @Override
    public String getPeriodStartDate() {
        return context.getProperty(BaseExecuteGeneva.PERIOD_START_DATE).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    @Override
    public String getPeriodEndDate() {
        return context.getProperty(BaseExecuteGeneva.PERIOD_END_DATE).evaluateAttributeExpressions(flowfile).getValue();
    }

    @Override
    public String getKnowledgeDate() {
        return context.getProperty(BaseExecuteGeneva.KNOWLEDGE_DATE).evaluateAttributeExpressions(flowfile).getValue();
    }

    @Override
    public String getPriorKnowledgeDate() {
        return context.getProperty(BaseExecuteGeneva.PRIOR_KNOWLEDGE_DATE).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    @Override
    public String getAccountingRunType() {
        return context.getProperty(BaseExecuteGeneva.ACCOUNTING_RUN_TYPE).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    @Override
    public String getReportConsolidation() {
        return context.getProperty(BaseExecuteGeneva.REPORT_CONSOLIDATION).evaluateAttributeExpressions(flowfile)
                .getValue();
    }

    @Override
    public String getExtraFlags() {
        return context.getProperty(BaseExecuteGeneva.EXTRA_FLAGS).evaluateAttributeExpressions(flowfile).getValue();
    }

    @Override
    public String getRSLName() {
        return context.getProperty(ExecuteGenevaRSL.RSL_NAME).evaluateAttributeExpressions(flowfile).getValue();
    }
}
