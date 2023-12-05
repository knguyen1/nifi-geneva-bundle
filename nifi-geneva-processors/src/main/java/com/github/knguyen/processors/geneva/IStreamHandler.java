package com.github.knguyen.processors.geneva;

import java.io.IOException;
import java.io.InputStream;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;

public interface IStreamHandler {
    FlowFile handleStream(final FlowFile originalFlowFile, final ProcessSession processSession,
            final InputStream inputStream) throws IOException;
}
