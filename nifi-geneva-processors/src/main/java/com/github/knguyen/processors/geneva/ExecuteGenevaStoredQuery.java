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

import java.util.Arrays;
import java.util.List;

import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;

public class ExecuteGenevaStoredQuery extends BaseExecuteGeneva {

    static final AllowableValue RUN_TYPE_RUN = new AllowableValue("run", "run",
            "Run a report by specifying its title (as opposed to its *.rsl file name) in quotation marks. E.g. `run \"Tax Lot Appraisal with Accruals\" -p Green`");
    static final AllowableValue RUN_TYPE_RUNFILE = new AllowableValue("runfile", "runfile",
            "Run a report by specifying its file name without the \".rsl\" extension. If the report has not yet been read into Runrep, Runrep tries to read in the report, and displays an error message if it cannot find the report. E.g. `runfile taxlotappacc -p Green`");
    static final AllowableValue RUN_TYPE_RUNNUMBER = new AllowableValue("runnumber", "runnumber",
            "Run a report by specifying its report number. You can use the `List` command to identify a report's number. A report's number depends on the report list file (such as rep.lst) read into Runrep. Changing a list file can change a report's number, which can impact scripts that use the Runnumber command. E.g. `runnumber 117 -p Green`");
    static final AllowableValue RUN_TYPE_RUNQUERY = new AllowableValue("runquery", "runquery",
            "Run a report's query by specifying the query's name. You can use the `pquery` command to see all of the available queries. E.g. `runquery TaxLotAppraisalAccruals -p Green`");

    static final PropertyDescriptor GENEVA_RUN_TYPE = new PropertyDescriptor.Builder().name("geneva-run-type")
            .displayName("Run Type")
            .description(
                    "Defines the run command to execute.  The `Portfolio List` option (to specify one or more portfolios) is required for all of these commands.")
            .required(true).defaultValue(RUN_TYPE_RUNFILE.getValue())
            .allowableValues(RUN_TYPE_RUN, RUN_TYPE_RUNFILE, RUN_TYPE_RUNNUMBER, RUN_TYPE_RUNQUERY).build();

    static final PropertyDescriptor GENEVA_QUERY_NAME = new PropertyDescriptor.Builder().name("geneva-query-name")
            .displayName("Query Name")
            .description(
                    "Defines the target for the run type, e.g. the `taxlotappacc` value in the command `runfile taxlotappacc -p Green`.")
            .required(true).defaultValue("${geneva.queryname}").build();

    @Override
    protected List<PropertyDescriptor> additionalDescriptors() {
        return Arrays.asList(GENEVA_QUERY_NAME, GENEVA_RUN_TYPE);
    }
}
