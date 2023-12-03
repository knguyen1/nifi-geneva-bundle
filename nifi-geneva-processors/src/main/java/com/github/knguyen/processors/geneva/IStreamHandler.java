package com.github.knguyen.processors.geneva;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;

import net.schmizz.sshj.sftp.RemoteFile;

public interface IStreamHandler {
    FlowFile handleStream(final FlowFile originalFlowFile, final ProcessSession processSession,
            final RemoteFile.ReadAheadRemoteFileInputStream rfis);
}
