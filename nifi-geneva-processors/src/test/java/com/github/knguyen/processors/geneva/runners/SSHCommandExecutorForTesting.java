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
package com.github.knguyen.processors.geneva.runners;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.nifi.context.PropertyContext;
import org.apache.nifi.logging.ComponentLog;

import com.github.knguyen.processors.ssh.SSHCommandExecutor;

import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPException;
import net.schmizz.sshj.sftp.Response.StatusCode;

public class SSHCommandExecutorForTesting extends SSHCommandExecutor {
    public static final String CSV_CONTENT = "column1,column2,column3\n" + //
            "value1,value2,value3\n" + //
            "value4,value5,value6\n";

    // toggles for testing
    private boolean noSuchFile;
    private boolean permissionDenied;

    public SSHCommandExecutorForTesting(final PropertyContext context, final ComponentLog logger) {
        super(context, logger);
    }

    public void setNoSuchFile(boolean noSuchFile) {
        this.noSuchFile = noSuchFile;
    }

    public void setPermissionDenied(boolean permissionDenied) {
        this.permissionDenied = permissionDenied;
    }

    @Override
    public InputStream getStreamFromRemoteFile(final RemoteFile remoteFile) throws IOException {
        if (noSuchFile)
            throw new SFTPException(StatusCode.NO_SUCH_FILE, "File not found for testing");

        if (permissionDenied)
            throw new SFTPException(StatusCode.PERMISSION_DENIED, "Permission denied for testing");

        // Convert the CSV content to InputStream
        final byte[] bytesContent = CSV_CONTENT.getBytes();
        final InputStream inputStream = new ByteArrayInputStream(bytesContent);

        return inputStream;
    }
}
