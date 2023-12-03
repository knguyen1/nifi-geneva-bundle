package com.github.knguyen.processors.geneva;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.stream.io.StreamUtils;

import net.schmizz.sshj.sftp.RemoteFile;

import java.io.InputStream;

public class StreamToFlowfileContentHandler implements IStreamHandler {
    @Override
    public FlowFile handleStream(final FlowFile originalFlowFile, final ProcessSession processSession,
            final RemoteFile.ReadAheadRemoteFileInputStream rfis) {
        return processSession.write(originalFlowFile, out -> StreamUtils.copy((InputStream) rfis, out));
    }
}
