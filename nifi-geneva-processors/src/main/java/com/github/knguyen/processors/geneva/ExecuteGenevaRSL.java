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

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

public class ExecuteGenevaRSL extends BaseExecuteGeneva {

    static final PropertyDescriptor RSL_NAME = new PropertyDescriptor.Builder().name("rsl-name").displayName("RSL Name")
            .description(
                    "Specifies the RSL name.  The '.rsl' (dot rsl) extension is not necessary, e.g. `glmap_netassets` for Statement of Net Assets.")
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(true)
            .addValidator(StandardValidators.NON_BLANK_VALIDATOR).build();

    @Override
    protected List<PropertyDescriptor> additionalDescriptors() {
        return Arrays.asList(RSL_NAME);
    }

    @Override
    public IStreamHandler getStreamHandler() {
        return new StreamToFlowfileContentHandler();
    }

    @Override
    protected ICommand getCommand(final ProcessContext context, final FlowFile flowfile) {
        return new RSLCommand(new StandardRunrepArgumentProvider(context, flowfile));
    }
}
