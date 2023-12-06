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

import org.apache.nifi.util.StringUtils;

public class GSQLCommand extends RunrepCommand {
    public GSQLCommand(IRunrepArgumentProvider argumentProvider) {
        super(argumentProvider);
    }

    @Override
    protected String getReportCommand() {
        // get the output format
        final String outputFormat = argumentProvider.getOutputFormat();

        // get the output filename
        final String outputFilename = getOuputFilename();
        final String reportParameters = getReportParameters();

        // get the gsql query
        final String gsqlQuery = argumentProvider.getGSQLQuery();

        if (StringUtils.isNotBlank(reportParameters)) {
            return String.format("rungsql -f %s -o \"%s\" %s%n%s", outputFormat, outputFilename, reportParameters,
                    gsqlQuery);
        } else {
            return String.format("rungsql -f %s -o \"%s\"%n%s", outputFormat, outputFilename, gsqlQuery);
        }
    }
}
