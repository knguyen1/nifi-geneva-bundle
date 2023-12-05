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
package com.github.knguyen.processors.ssh;

import org.apache.nifi.processor.ProcessContext;

import com.github.knguyen.processors.geneva.RemoteCommandExecutor;
import com.github.knguyen.processors.geneva.RemoteCommandExecutorProvider;

import org.apache.nifi.logging.ComponentLog;

/**
 * This interface defines a provider for RemoteCommandExecutor objects. The purpose of this provider is to enable the
 * creation of different types of RemoteCommandExecutor (for example, SSHCommandExecutor or its test variant) in a
 * flexible way, allowing for easier testing and potential future extensions.
 *
 * Classes implementing this interface should override the createExecutor method to return an instance of the
 * appropriate type of RemoteCommandExecutor.
 */
public class SSHCommandExecutorProvider implements RemoteCommandExecutorProvider {
    /**
     * Creates and returns a RemoteCommandExecutor object.
     *
     * @param context
     *            the ProcessContext to be used when creating the RemoteCommandExecutor
     * @param logger
     *            the ComponentLog to be used when creating the RemoteCommandExecutor
     *
     * @return a new RemoteCommandExecutor object
     */
    @Override
    public RemoteCommandExecutor createExecutor(final ProcessContext context, final ComponentLog logger) {
        return new SSHCommandExecutor(context, logger);
    }
}
