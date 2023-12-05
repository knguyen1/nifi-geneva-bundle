package com.github.knguyen.processors.geneva;

import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

/**
 * This interface defines a provider for RemoteCommandExecutor objects. The purpose of this provider is to enable the
 * creation of different types of RemoteCommandExecutor (for example, SSHCommandExecutor or its test variant) in a
 * flexible way, allowing for easier testing and potential future extensions.
 *
 * Classes implementing this interface should override the createExecutor method to return an instance of the
 * appropriate type of RemoteCommandExecutor.
 */
public interface RemoteCommandExecutorProvider {
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
    RemoteCommandExecutor createExecutor(final ProcessContext context, final ComponentLog logger);
}
