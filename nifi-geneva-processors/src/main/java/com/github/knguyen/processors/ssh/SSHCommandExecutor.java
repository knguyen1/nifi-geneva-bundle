package com.github.knguyen.processors.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.nifi.context.PropertyContext;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processors.standard.ssh.SSHClientProvider;
import org.apache.nifi.processors.standard.ssh.StandardSSHClientProvider;
import org.apache.nifi.processors.standard.util.FileTransfer;
import org.apache.nifi.processors.standard.util.SFTPTransfer;
import org.apache.nifi.util.StringUtils;
import org.apache.nifi.util.file.FileUtils;

import com.github.knguyen.processors.geneva.GenevaException;
import com.github.knguyen.processors.geneva.RemoteCommandExecutor;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPClient;

import java.util.Objects;

public class SSHCommandExecutor implements RemoteCommandExecutor {
    private static final SSHClientProvider sshClientProvider = new StandardSSHClientProvider();

    private final PropertyContext context;
    private final ComponentLog logger;

    private SSHClient sshClient;

    private volatile boolean closed = false;
    private String activeHostname;
    private String activePort;
    private String activeUsername;
    private String activePassword;
    private String activePrivateKeyPath;
    private String activePrivateKeyPassphrase;

    public SSHCommandExecutor(final PropertyContext context, final ComponentLog logger) {
        this.context = context;
        this.logger = logger;
    }

    protected SSHClient getSSHClient(final FlowFile flowFile) throws IOException {
        final String evaledHostname = context.getProperty(FileTransfer.HOSTNAME).evaluateAttributeExpressions(flowFile)
                .getValue();
        final String evaledPort = context.getProperty(SFTPTransfer.PORT).evaluateAttributeExpressions(flowFile)
                .getValue();
        final String evaledUsername = context.getProperty(SFTPTransfer.USERNAME).evaluateAttributeExpressions(flowFile)
                .getValue();
        final String evaledPassword = context.getProperty(FileTransfer.PASSWORD).evaluateAttributeExpressions(flowFile)
                .getValue();
        final String evaledPrivateKeyPath = context.getProperty(SFTPTransfer.PRIVATE_KEY_PATH)
                .evaluateAttributeExpressions(flowFile).getValue();
        final String evaledPrivateKeyPassphrase = context.getProperty(SFTPTransfer.PRIVATE_KEY_PASSPHRASE)
                .evaluateAttributeExpressions(flowFile).getValue();

        // If the client is already initialized then compare the host that the client is connected to with the current
        // host from the properties/flow-file, and if different then we need to close and reinitialize, if same we can
        // reuse
        if (sshClient != null) {
            if (Objects.equals(evaledHostname, activeHostname) && Objects.equals(evaledPort, activePort)
                    && Objects.equals(evaledUsername, activeUsername) && Objects.equals(evaledPassword, activePassword)
                    && Objects.equals(evaledPrivateKeyPath, activePrivateKeyPath)
                    && Objects.equals(evaledPrivateKeyPassphrase, activePrivateKeyPassphrase)) {
                // destination matches so we can keep our current session
                return sshClient;
            } else {
                // this flowFile is going to a different destination, reset session
                close();
            }
        }

        final Map<String, String> attributes = flowFile == null ? Collections.emptyMap() : flowFile.getAttributes();
        this.sshClient = sshClientProvider.getClient(context, attributes);

        activeHostname = evaledHostname;
        activePort = evaledPort;
        activePassword = evaledPassword;
        activeUsername = evaledUsername;
        activePrivateKeyPath = evaledPrivateKeyPath;
        activePrivateKeyPassphrase = evaledPrivateKeyPassphrase;
        this.closed = false;

        // Configure timeout for ssh operations
        final int dataTimeout = context.getProperty(FileTransfer.DATA_TIMEOUT).asTimePeriod(TimeUnit.MILLISECONDS)
                .intValue();
        final int connectTimeout = context.getProperty(SFTPTransfer.CONNECTION_TIMEOUT)
                .asTimePeriod(TimeUnit.MILLISECONDS).intValue();
        this.sshClient.setTimeout(dataTimeout);
        this.sshClient.setConnectTimeout(connectTimeout);

        return sshClient;
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }

        try {
            if (null != sshClient) {
                sshClient.disconnect();
            }
        } catch (final Exception ex) {
            logger.warn("Failed to close SSHClient due to {}", ex.toString(), ex);
        }
        sshClient = null;
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void execute(final ICommand command, final FlowFile originalFlowFile, final ProcessSession processSession)
            throws IOException, GenevaException {
        final SSHClient client = this.getSSHClient(originalFlowFile);

        // ensure that the SSHClient is initialized and connected
        // the class `SSHClientProvider` should have already instantiated a connection for us
        if (client == null || !client.isConnected()) {
            logger.error("SSH Client is not connected. Cannot execute command.");
            throw new IllegalStateException("SSH Client is not connected. Cannot execute command.");
        }

        try (final Session session = client.startSession()) {
            final Command cmd = session.exec(command.getCommand());

            // Nested try-with-resources for BufferedReader and InputStreamReader
            try (BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(cmd.getErrorStream()))) {
                String line;
                while (StringUtils.isNotBlank(line = stdErrReader.readLine())) {
                    this.maybeRaiseException("Failed to run command in runrep", command.getObfuscatedCommand(), line);

                    // Break the loop if the exit status is available
                    if (cmd.getExitStatus() != null) {
                        break;
                    }
                }

                // wait x seconds for the file to materialise
                FileUtils.sleepQuietly(3000);
            }
        }
    }

    @Override
    public FlowFile getRemoteFile(final ICommand command, final FlowFile originalFlowFile,
            final ProcessSession processSession) throws IOException {
        final String resource = command.getOutputResource();

        final SSHClient client = this.getSSHClient(originalFlowFile);

        // ensure that the SSHClient is initialized and connected
        // the class `SSHClientProvider` should have already instantiated a connection for us
        if (client == null || !client.isConnected()) {
            logger.error("SSH Client is not connected. Cannot execute command.");
            throw new IllegalStateException("SSH Client is not connected. Cannot execute command.");
        }

        try (final SFTPClient sftpClient = client.newSFTPClient()) {
            try (final RemoteFile remoteFile = sftpClient.open(resource)) {
                try (final RemoteFile.ReadAheadRemoteFileInputStream rfis = remoteFile.new ReadAheadRemoteFileInputStream(
                        16)) {

                }
            }
        }
    }
}
