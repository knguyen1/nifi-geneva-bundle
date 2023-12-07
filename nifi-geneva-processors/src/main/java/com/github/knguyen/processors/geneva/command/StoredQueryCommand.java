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
package com.github.knguyen.processors.geneva.command;

import java.util.Arrays;

import org.apache.nifi.util.StringUtils;

import com.github.knguyen.processors.geneva.argument.IRunrepArgumentProvider;

public class StoredQueryCommand extends RunrepCommand {
    public StoredQueryCommand(IRunrepArgumentProvider argumentProvider) {
        super(argumentProvider);
    }

    @Override
    protected String getReportCommand() {
        // get the output format
        final String outputFormat = argumentProvider.getOutputFormat();

        // get the output filename
        final String outputFilename = getOuputFilename();
        final String reportParameters = getReportParameters();

        final String runCommandName = argumentProvider.getRunCommandName();
        final String runCommandTarget = argumentProvider.getRunCommandTarget();

        // Check if runCommandTarget contains spaces and enclose in quotes if it does
        final String formattedRunCommandTarget = runCommandTarget.contains(" ") ? "\"" + runCommandTarget + "\""
                : runCommandTarget;

        return String.format("%s %s -f %s -o %s %s", runCommandName, formattedRunCommandTarget, outputFormat,
                outputFilename, reportParameters).trim();
    }

    @Override
    public void validate() {
        argumentProvider.validate();

        final String runCommandName = argumentProvider.getRunCommandName();
        final String runCommandTarget = argumentProvider.getRunCommandTarget();

        if (StringUtils.isBlank(runCommandName))
            throw new IllegalArgumentException("`runCommandName` cannot be null");

        if (StringUtils.isBlank(runCommandTarget))
            throw new IllegalArgumentException("`runCommandTarget` cannot be null");

        // Check if runCommandName is one of the specified values
        if (!Arrays.asList("run", "runfile", "runf", "runnumber", "runquery").contains(runCommandName))
            throw new IllegalArgumentException(
                    "`runCommandName` must be one of 'run', 'runfile', 'runf', 'runnumber', 'runquery'.");

        if (runCommandTarget.trim().startsWith("-"))
            throw new IllegalArgumentException(
                    "`runCommandTarget` starts with a '-' character; runrep will misinterpret this as flags.  You can fix this issue by temporarily renaming the object.");
    }
}
