/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.knguyen.processors.geneva;

import org.apache.nifi.context.PropertyContext;
import org.apache.nifi.processors.standard.ssh.SSHClientProvider;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPClient;

import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.knguyen.processors.geneva.runners.GenevaTestRunner;
import com.github.knguyen.processors.geneva.runners.SSHCommandExecutorForTestProvider;
import com.github.knguyen.processors.geneva.runners.SSHCommandExecutorForTesting;

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
        processor.setExecutorProvider(new SSHCommandExecutorForTestProvider());
    }

    @Test
    void testBasicRunEndToEnd() throws Exception {
        final Map<String, String> expectedAttributes = new HashMap<>();
        expectedAttributes.put("ssh.remote.username", "foo");
        expectedAttributes.put("geneva.runrep.user", "runrepusr");
        expectedAttributes.put("ssh.remote.port", "22");
        expectedAttributes.put("ssh.remote.host", "my.geneva.server.com");
        expectedAttributes.put("geneva.runrep.aga", "9999");

        final String commandPattern = "runrep -f empty.lst -b << EOF" + System.lineSeparator() + //
                "connect runrepusr/\\*\\*\\*\\*\\*\\*\\*\\*\\* -k 9999" + System.lineSeparator() + //
                "read \"netassets.rsl\"" + System.lineSeparator() + //
                "runfile \"netassets\" -f csv -o \"/tmp/[a-f0-9\\-]*\\.csv\"" + System.lineSeparator() + //
                "exit" + System.lineSeparator() + //
                "EOF\n";

        final GenevaTestRunner gvaTestRunner = new GenevaTestRunner.Builder().withHostname(HOSTNAME)
                .withUsername(USERNAME).withPassword(PASSWORD).withRunrepUsername(RUNREP_USERNAME)
                .withRunrepPassword(RUNREP_PASSWORD).withGenevaAga(9999).withRSLName("netassets")
                .withExpectedContent(SSHCommandExecutorForTesting.CSV_CONTENT)
                .withExpectedAttributes(expectedAttributes).withExpectedCommandPattern(commandPattern).build();

        gvaTestRunner.execute(this);
        gvaTestRunner.assertPass();

        // verify that we called with the correct cmd
        Mockito.verify(mockSession).exec(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String argument) {
                return argument.matches("runrep -f empty.lst -b << EOF" + System.lineSeparator() + //
                        "connect runrepusr/runreppass -k 9999" + System.lineSeparator() + //
                        "read \"netassets.rsl\"" + System.lineSeparator() + //
                        "runfile \"netassets\" -f csv -o \"/tmp/[a-f0-9\\-]*\\.csv\"" + System.lineSeparator() + //
                        "exit" + System.lineSeparator() + //
                        "EOF\n");
            }
        }));

        Mockito.verify(mockSftpClient).open(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String argument) {
                return argument.matches("/tmp/[a-f0-9\\-]*\\.csv");
            }
        }));
    }

    @Test
    void testRunWithDifferentUsernamePasswords() throws Exception {
        final String commandPattern = "runrep -f empty.lst -b << EOF" + System.lineSeparator() + //
                "connect fez/\\*\\*\\*\\*\\*\\*\\*\\*\\* -k 1234" + System.lineSeparator() + //
                "read \"positions.rsl\"" + System.lineSeparator() + //
                "runfile \"positions\" -f csv -o \"/tmp/my report file.csv\"" + System.lineSeparator() + //
                "exit" + System.lineSeparator() + //
                "EOF\n";

        final GenevaTestRunner gvaTestRunner = new GenevaTestRunner.Builder().withHostname(HOSTNAME).withUsername("foo")
                .withPassword("bar").withRunrepUsername("fez").withRunrepPassword("faz").withGenevaAga(1234)
                .withRSLName("positions").withReportOutputPath("/tmp/my report file.csv")
                .withExpectedCommandPattern(commandPattern).build();

        gvaTestRunner.execute(this);
        gvaTestRunner.assertPass();

        // verify that we called with the correct cmd
        Mockito.verify(mockSession).exec(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String argument) {
                return argument.matches("runrep -f empty.lst -b << EOF" + System.lineSeparator() + //
                        "connect fez/faz -k 1234" + System.lineSeparator() + //
                        "read \"positions.rsl\"" + System.lineSeparator() + //
                        "runfile \"positions\" -f csv -o \"/tmp/my report file.csv\"" + System.lineSeparator() + //
                        "exit" + System.lineSeparator() + //
                        "EOF\n");
            }
        }));
    }
}
