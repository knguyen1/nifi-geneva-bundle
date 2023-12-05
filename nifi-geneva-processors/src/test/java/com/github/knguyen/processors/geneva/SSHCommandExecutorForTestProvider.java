package com.github.knguyen.processors.geneva;

import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

public class SSHCommandExecutorForTestProvider implements RemoteCommandExecutorProvider {
    @Override
    public RemoteCommandExecutor createExecutor(final ProcessContext context, final ComponentLog logger) {
        return new SSHCommandExecutorForTesting(context, logger);
    }
}
