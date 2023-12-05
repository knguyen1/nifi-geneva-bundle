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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processors.standard.ssh.SSHClientProvider;

import com.github.knguyen.processors.ssh.ICommand;

import net.schmizz.sshj.sftp.RemoteFile;

public interface RemoteCommandExecutor extends Closeable {
    void setSSHClientProvider(SSHClientProvider sshClientProvider);

    String getProtocolName();

    boolean isClosed();

    void close() throws IOException;

    void execute(final ICommand command, final FlowFile originalFlowFile, final ProcessSession processSession)
            throws IOException, GenevaException;

    FlowFile getRemoteFile(final ICommand command, final FlowFile originalFlowFile, final ProcessSession processSession,
            IStreamHandler streamHandler) throws IOException;

    InputStream getStreamFromRemoteFile(final RemoteFile remoteFile) throws IOException;

    void deleteFile(final ICommand command, final FlowFile flowFile) throws IOException;

    default void maybeRaiseException(final String message, final String errorLine, final String loggableCommand)
            throws GenevaException {
        // Array of keywords to check in the errorLine
        String[] keywords = { "error", "failed", "exception", "error running", "failure" };

        // Check if any keyword is present in errorLine
        for (String keyword : keywords) {
            if (errorLine != null && errorLine.toLowerCase().contains(keyword)) {
                // If a keyword is found, throw GenevaRunrepException
                throw new GenevaException(message, errorLine, loggableCommand);
            }
        }
    }

}
