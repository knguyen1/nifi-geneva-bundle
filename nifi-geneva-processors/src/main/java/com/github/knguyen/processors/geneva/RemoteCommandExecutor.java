package com.github.knguyen.processors.geneva;

import java.io.Closeable;
import java.io.IOException;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;

import com.github.knguyen.processors.ssh.ICommand;

public interface RemoteCommandExecutor extends Closeable {
    String getProtocolName();

    boolean isClosed();

    void close() throws IOException;

    void execute(final ICommand command, final FlowFile originalFlowFile, final ProcessSession processSession)
            throws IOException, GenevaException;

    FlowFile getRemoteFile(final ICommand command, final FlowFile originalFlowFile, final ProcessSession processSession,
            IStreamHandler streamHandler) throws IOException;

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