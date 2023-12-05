package com.github.knguyen.processors.geneva;

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
