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

import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import com.github.knguyen.processors.geneva.argument.StandardRunrepArgumentProvider;
import com.github.knguyen.processors.geneva.command.GSQLCommand;
import com.github.knguyen.processors.geneva.command.ICommand;

public class ExecuteGenevaGSQL extends BaseExecuteGeneva {
    public static final PropertyDescriptor GENEVA_SQL_QUERY = new PropertyDescriptor.Builder().name("geneva-sql-query")
            .displayName("Geneva SQL Query")
            .description(
                    "The SQL select query to execute. The query can be empty, a constant value, or built from attributes "
                            + "using Expression Language. If this property is specified, it will be used regardless of the content of "
                            + "incoming flowfiles. If this property is empty, the content of the incoming flow file is expected "
                            + "to contain a valid SQL select query, to be issued by the processor to the database. Note that Expression "
                            + "Language is not evaluated for flow file contents. The query, regardless if specified in this property or the FlowFile content, must end in a semi-colon.")
            .required(false).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).build();

    @Override
    protected List<PropertyDescriptor> additionalDescriptors() {
        return Arrays.asList(GENEVA_SQL_QUERY);
    }

    @OnScheduled
    public void setup(ProcessContext context) {
        // If the query is not set, then an incoming flow file is needed. Otherwise fail the initialization
        if (!context.getProperty(GENEVA_SQL_QUERY).isSet() && !context.hasIncomingConnection()) {
            final String errorString = "Either the Select Query must be specified or there must be an incoming connection "
                    + "providing flowfile(s) containing a SQL select query";
            getLogger().error(errorString);
            throw new ProcessException(errorString);
        }
    }

    @Override
    protected ICommand getCommand(final ProcessSession session, final ProcessContext context, final FlowFile flowfile)
            throws IllegalArgumentException {
        final var provider = new StandardRunrepArgumentProvider(session, context, flowfile);
        final var command = new GSQLCommand(provider);
        command.validate();
        return command;
    }
}
