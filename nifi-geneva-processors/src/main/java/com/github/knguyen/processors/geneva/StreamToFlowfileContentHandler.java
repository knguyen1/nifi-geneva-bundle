package com.github.knguyen.processors.geneva;

import java.io.IOException;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.stream.io.StreamUtils;

import net.schmizz.sshj.sftp.RemoteFile;

public class StreamToFlowfileContentHandler implements IStreamHandler {
    @Override
    public FlowFile handleStream(final FlowFile originalFlowFile, final ProcessSession processSession,
            final RemoteFile.ReadAheadRemoteFileInputStream rfis) throws IOException {
        if (rfis.available() > 0)
            return processSession.write(originalFlowFile, out -> StreamUtils.copy(rfis, out));

        return originalFlowFile;
    }
}
