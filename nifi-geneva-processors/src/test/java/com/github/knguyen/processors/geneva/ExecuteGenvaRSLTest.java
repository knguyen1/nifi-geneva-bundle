package com.github.knguyen.processors.geneva;

import org.apache.nifi.context.PropertyContext;
import org.apache.nifi.processors.standard.ssh.SSHClientProvider;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteFile.ReadAheadRemoteFileInputStream;
import net.schmizz.sshj.sftp.SFTPClient;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

class ExecuteGenvaRSLTest extends BaseExecuteGenevaTest {
    @Mock
    private SSHClient mockSshClient;

    @Mock
    private SFTPClient mockSftpClient;

    @Mock
    private Session mockSession;

    @Mock
    private Session.Command mockCommand;

    @Mock
    private SSHClientProvider mockSshClientProvider;

    @Mock
    private RemoteFile mockRemoteFile;

    @Mock
    private RemoteFile.ReadAheadRemoteFileInputStream mockRfis;

    @BeforeEach
    public void setup() throws Exception {
        System.setProperty("skipSleep", String.valueOf(Boolean.TRUE));

        MockitoAnnotations.openMocks(this);

        // configure mocks
        when(mockSshClient.startSession()).thenReturn(mockSession);
        when(mockSshClient.isConnected()).thenReturn(true);
        when(mockSshClient.newSFTPClient()).thenReturn(mockSftpClient);
        when(mockSshClientProvider.getClient(any(PropertyContext.class), anyMap())).thenReturn(mockSshClient);
        when(mockSession.exec(any(String.class))).thenReturn(mockCommand);
        when(mockSftpClient.open(anyString())).thenReturn(mockRemoteFile);

        // mock the standard error output
        final String noErrors = "okay\n";
        when(mockCommand.getErrorStream()).thenReturn(new ByteArrayInputStream(noErrors.getBytes()));
        when(mockCommand.getExitStatus()).thenReturn(0);

        testRunner = TestRunners.newTestRunner(ExecuteGenevaRSL.class);

        // Here we inject the mock into the processor
        BaseExecuteGeneva processor = (BaseExecuteGeneva) testRunner.getProcessor();
        processor.setSSHClientProvider(mockSshClientProvider);
    }

    @Test
    void testBasicRunEndToEnd() throws Exception {
        final GenevaTestRunner gvaTestRunner = new GenevaTestRunner.Builder().withHostname(HOSTNAME)
                .withUsername(USERNAME).withPassword(PASSWORD).withRunrepUsername(RUNREP_USERNAME)
                .withRunrepPassword(RUNREP_PASSWORD).withGenevaAga(9999).withRSLName("netassets").build();

        gvaTestRunner.execute(this);

        // verify that we called with the correct cmd
        Mockito.verify(mockSession).exec("foo");
    }
}
