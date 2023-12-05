package com.github.knguyen.processors.geneva;

import java.io.IOException;
import java.io.InputStream;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.stream.io.StreamUtils;

public class StreamToFlowfileContentHandler implements IStreamHandler {
    @Override
    public FlowFile handleStream(final FlowFile originalFlowFile, final ProcessSession processSession,
            final InputStream inputStream) throws IOException {
        if (inputStream.available() > 0)
            return processSession.write(originalFlowFile, out -> StreamUtils.copy(inputStream, out));

        return originalFlowFile;
    }
}
