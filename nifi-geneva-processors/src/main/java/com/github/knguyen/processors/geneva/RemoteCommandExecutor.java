package com.github.knguyen.processors.geneva;

import java.io.Closeable;
import java.io.IOException;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;

public interface RemoteCommandExecutor extends Closeable {
    boolean isClosed();

    void close() throws IOException;

    void execute(final String command, final FlowFile originalFlowFile, final ProcessSession processSession)
            throws IOException, GenevaException;

    default void maybeRaiseException(final String message, final String command, final String errorLine)
            throws GenevaException {
        // Array of keywords to check in the errorLine
        String[] keywords = { "error", "failed", "exception", "error running", "failure" };

        // Check if any keyword is present in errorLine
        for (String keyword : keywords) {
            if (errorLine != null && errorLine.toLowerCase().contains(keyword)) {
                // If a keyword is found, throw GenevaRunrepException
                throw new GenevaException(message, command, errorLine);
            }
        }
    }

}
