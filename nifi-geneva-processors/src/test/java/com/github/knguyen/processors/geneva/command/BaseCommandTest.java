package com.github.knguyen.processors.geneva.command;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.knguyen.processors.geneva.BaseExecuteGeneva;
import com.github.knguyen.processors.geneva.argument.IRunrepArgumentProvider;

abstract class BaseCommandTest {
    @Mock
    protected IRunrepArgumentProvider runrepArgumentProviderMock;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Common mock behaviors
        when(runrepArgumentProviderMock.getGenevaUser()).thenReturn("usr");
        when(runrepArgumentProviderMock.getGenevaPassword()).thenReturn("pw");
        when(runrepArgumentProviderMock.getGenevaAga()).thenReturn("9999");
        when(runrepArgumentProviderMock.getOutputDirectory()).thenReturn("/usr/advent/geneva-20.0.0/share/rslspecs");
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
    }
}
