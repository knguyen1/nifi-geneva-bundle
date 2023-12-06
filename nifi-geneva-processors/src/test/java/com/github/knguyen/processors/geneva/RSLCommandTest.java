package com.github.knguyen.processors.geneva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.nifi.util.StringUtils;

class RSLCommandTest {
    @Mock
    private IRunrepArgumentProvider runrepArgumentProviderMock;

    private RSLCommand rslCommand;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(runrepArgumentProviderMock.getGenevaUser()).thenReturn("usr");
        when(runrepArgumentProviderMock.getGenevaPassword()).thenReturn("pw");
        when(runrepArgumentProviderMock.getGenevaAga()).thenReturn("9999");
        when(runrepArgumentProviderMock.getOutputFilename())
                .thenReturn("/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv");
        when(runrepArgumentProviderMock.getOutputDirectory()).thenReturn("/usr/advent/geneva-20.0.0/share/rslspecs");
        when(runrepArgumentProviderMock.getOutputPath())
                .thenReturn("/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv");
        when(runrepArgumentProviderMock.getPortfolioList()).thenReturn("123,456,789");
        when(runrepArgumentProviderMock.getPeriodStartDate()).thenReturn("2023-01-01T00:00:00");
        when(runrepArgumentProviderMock.getPeriodEndDate()).thenReturn("2023-01-31T00:00:00");
        when(runrepArgumentProviderMock.getKnowledgeDate()).thenReturn("2023-02-01T23:59:59");
        when(runrepArgumentProviderMock.getPriorKnowledgeDate()).thenReturn("2022-12-01T12:34:56");
        when(runrepArgumentProviderMock.getAccountingRunType())
                .thenReturn(BaseExecuteGeneva.DYNAMIC_ACCOUNTING.getValue());
        when(runrepArgumentProviderMock.getReportConsolidation())
                .thenReturn(BaseExecuteGeneva.NONE_CONSOLIDATED.getValue());
        when(runrepArgumentProviderMock.getExtraFlags()).thenReturn(org.apache.nifi.util.StringUtils.EMPTY);
        when(runrepArgumentProviderMock.getRSLName()).thenReturn("my_positions.rsl");
        when(runrepArgumentProviderMock.getFileExtension()).thenReturn(".csv");
        when(runrepArgumentProviderMock.getOutputFormat()).thenReturn("csv");
    }

    @Test
    void testGetCommandStringFromControlCase() {
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);
        final var commandStr = rslCommand.getCommand();

        assertEquals("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "read \"my_positions.rsl\"\n" + //
                "runfile \"my_positions\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56\n"
                + //
                "exit\n" + //
                "EOF\n", commandStr);
    }

    @ParameterizedTest
    @CsvSource({
        "foo,bar",
        "baz,bez",
        "froppling,jigglywuff"
    })
    void testOverridingUserAnndPassword(final String username, final String password) {
        when(runrepArgumentProviderMock.getGenevaUser()).thenReturn(username);
        when(runrepArgumentProviderMock.getGenevaPassword()).thenReturn(password);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);
        final var commandStr = rslCommand.getCommand();

        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect %s/%s -k 9999\n" + //
                "read \"my_positions.rsl\"\n" + //
                "runfile \"my_positions\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56\n"
                + //
                "exit\n" + //
                "EOF\n", username, password), commandStr);
    }

    @ParameterizedTest
    @CsvSource({
        "foo,bar",
        "baz,bez",
        "froppling,jigglywuff"
    })
    void testThatPasswordIsCorrectlyObfuscated(final String username, final String password) {
        when(runrepArgumentProviderMock.getGenevaUser()).thenReturn(username);
        when(runrepArgumentProviderMock.getGenevaPassword()).thenReturn(password);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);
        final var commandStr = rslCommand.getObfuscatedCommand();
        final var loggablePart = rslCommand.getLoggablePart();

        assertEquals(commandStr, loggablePart);
        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect %s/********* -k 9999\n" + //
                "read \"my_positions.rsl\"\n" + //
                "runfile \"my_positions\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56\n"
                + //
                "exit\n" + //
                "EOF\n", username), commandStr);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Dynamic",
        "ClosedPeriod",
        "Incremental",
        "NAV",
        "Snapshot",
        "WouldBeAdjustments"
    })
    void testAccountingTypeOverride(String accountingRunType) {
        when(runrepArgumentProviderMock.getAccountingRunType()).thenReturn(accountingRunType);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);
        final var commandStr = rslCommand.getCommand();

        String accountingTypeInCommand = "Dynamic".equals(accountingRunType) ? "" : " -at " + accountingRunType;

        String expectedCommand = "runrep -f empty.lst -b << EOF\n" + //
            "connect usr/pw -k 9999\n" + //
            "read \"my_positions.rsl\"\n" + //
            "runfile \"my_positions\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56" + accountingTypeInCommand + "\n" + //
            "exit\n" + //
            "EOF\n";

        assertEquals(expectedCommand, commandStr);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123",
        "123,456",
        "123,\\\"My Portfolio\\\",456"
    })
    void testPortfolioOverrides(final String portfolio) {
        when(runrepArgumentProviderMock.getPortfolioList()).thenReturn(portfolio);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);

        final var commandStr = rslCommand.getCommand();
        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "read \"my_positions.rsl\"\n" + //
                "runfile \"my_positions\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p %s -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56\n" + //
                "exit\n" + //
                "EOF\n", portfolio), commandStr);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "C:/temp/my_report.csv",
        "/home/ec2-user/geneva-reports/my_report.csv"
    })
    void testOutputPathOptions(final String outputPath) {
        when(runrepArgumentProviderMock.getOutputPath()).thenReturn(outputPath);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);

        final var commandStr = rslCommand.getCommand();
        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "read \"my_positions.rsl\"\n" + //
                "runfile \"my_positions\" -f csv -o \"%s\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56\n" + //
                "exit\n" + //
                "EOF\n", outputPath), commandStr);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "myrsl",
        "netassets",
        "bisTaxlot",
        "profitloss",
    })
    void testRSLNameOptions(final String rslName) {
        when(runrepArgumentProviderMock.getRSLName()).thenReturn(rslName);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);

        final var commandStr = rslCommand.getCommand();
        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "read \"%s.rsl\"\n" + //
                "runfile \"%s\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56\n" + //
                "exit\n" + //
                "EOF\n", rslName, rslName), commandStr);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "myrsl.rsl",
        "netassets.rsl",
        "bisTaxlot.rsl",
        "profitloss.rsl",
    })
    void testRSLNameOptions2(final String rslName) {
        when(runrepArgumentProviderMock.getRSLName()).thenReturn(rslName);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);

        final var commandStr = rslCommand.getCommand();
        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "read \"%s\"\n" + //
                "runfile \"%s\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56\n" + //
                "exit\n" + //
                "EOF\n", rslName, rslName.substring(0, rslName.length() - 4)), commandStr);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1234",
        "5678"
    })
    void testOverridingGenevaAga(final String genevaAga) {
        when(runrepArgumentProviderMock.getGenevaAga()).thenReturn(genevaAga);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);
        final var commandStr = rslCommand.getCommand();

        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k %s\n" + //
                "read \"my_positions.rsl\"\n" + //
                "runfile \"my_positions\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56\n"
                + //
                "exit\n" + //
                "EOF\n", genevaAga), commandStr);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "-af \"il=ibm ibm, ff\"",
        "-ac 2012.JAN",
        "-ac \"2012.A\".\"JAN.2012\"",
        "-af \"iLBC=USD,EUR iCustodian=GS1\""
    })
    void testProvidingExtraFlags(final String extraFlags) {
        when(runrepArgumentProviderMock.getExtraFlags()).thenReturn(extraFlags);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);
        final var commandStr = rslCommand.getCommand();

        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "read \"my_positions.rsl\"\n" + //
                "runfile \"my_positions\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56 %s\n"
                + //
                "exit\n" + //
                "EOF\n", extraFlags), commandStr);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "-c1",
        "-c2",
        "-c3"
    })
    void testWithReportConsolidation(final String consolidation) {
        when(runrepArgumentProviderMock.getReportConsolidation()).thenReturn(consolidation);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);

        final var consolidationArg = BaseExecuteGeneva.NONE_CONSOLIDATED.getValue().equals(consolidation) ? null : consolidation;
        final var commandStr = rslCommand.getCommand();

        String expectedCommand;
        if (StringUtils.isNotBlank(consolidationArg)) {
            expectedCommand = String.format("runrep -f empty.lst -b << EOF\n" +
                "connect usr/pw -k 9999\n" +
                "read \"my_positions.rsl\"\n" +
                "runfile \"my_positions\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56 %s\n" +
                "exit\n" +
                "EOF\n", consolidationArg);
        } else {
            expectedCommand = "runrep -f empty.lst -b << EOF\n" +
                "connect usr/pw -k 9999\n" +
                "read \"my_positions.rsl\"\n" +
                "runfile \"my_positions\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56\n" +
                "exit\n" +
                "EOF\n";
        }

        assertEquals(expectedCommand, commandStr);
    }

    @Test
    void testExtraFlagsAndReportConsolidationProvided() {
        final var extraFlags = "-af \"il=ibm ibm, ff\"";
        final var consolidateAll = BaseExecuteGeneva.CONSOLIDATE_ALL.getValue();

        when(runrepArgumentProviderMock.getReportConsolidation()).thenReturn(consolidateAll);
        when(runrepArgumentProviderMock.getExtraFlags()).thenReturn(extraFlags);
        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);
        final var commandStr = rslCommand.getCommand();

        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "read \"my_positions.rsl\"\n" + //
                "runfile \"my_positions\" -f csv -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56 %s %s\n"
                + //
                "exit\n" + //
                "EOF\n", consolidateAll, extraFlags), commandStr);
    }

    @ParameterizedTest
    @CsvSource({
        "csv,/tmp/file.csv",
        "json,/tmp/file.json",
        "pdf,/tmp/file.pdf",
        "xml,/tmp/file.xml"
    })
    void testProvidingProvidingDifferentReportFormats(final String format, final String outputPath) {
        when(runrepArgumentProviderMock.getOutputFormat()).thenReturn(format);
        when(runrepArgumentProviderMock.getOutputPath()).thenReturn(outputPath);

        this.rslCommand = new RSLCommand(runrepArgumentProviderMock);
        final var commandStr = rslCommand.getCommand();

        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "read \"my_positions.rsl\"\n" + //
                "runfile \"my_positions\" -f %s -o \"%s\" -p 123,456,789 -ps 2023-01-01T00:00:00 -pe 2023-01-31T00:00:00 -k 2023-02-01T23:59:59 -pk 2022-12-01T12:34:56\n"
                + //
                "exit\n" + //
                "EOF\n", format, outputPath), commandStr);
    }
}
