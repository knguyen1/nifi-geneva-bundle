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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GSQLCommandTest {
    @Mock
    private IRunrepArgumentProvider runrepArgumentProviderMock;

    private GSQLCommand gsqlCommand;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(runrepArgumentProviderMock.getGenevaUser()).thenReturn("usr");
        when(runrepArgumentProviderMock.getGenevaPassword()).thenReturn("pw");
        when(runrepArgumentProviderMock.getGenevaAga()).thenReturn("9999");
        when(runrepArgumentProviderMock.getOutputFilename())
                .thenReturn("/usr/advent/geneva-20.0.0/share/rslspecs/my-report.xml");
        when(runrepArgumentProviderMock.getOutputDirectory()).thenReturn("/usr/advent/geneva-20.0.0/share/rslspecs");
        when(runrepArgumentProviderMock.getOutputPath())
                .thenReturn("/usr/advent/geneva-20.0.0/share/rslspecs/my-report.xml");
        when(runrepArgumentProviderMock.getPortfolioList()).thenReturn("123-MyPortfolio");
        when(runrepArgumentProviderMock.getPeriodStartDate()).thenReturn("2023-01-01T00:00:00");
        when(runrepArgumentProviderMock.getPeriodEndDate()).thenReturn("2023-01-31T00:00:00");
        when(runrepArgumentProviderMock.getKnowledgeDate()).thenReturn("2023-02-01T23:59:59");
        when(runrepArgumentProviderMock.getPriorKnowledgeDate()).thenReturn("2022-12-01T12:34:56");
        when(runrepArgumentProviderMock.getAccountingRunType())
                .thenReturn(BaseExecuteGeneva.DYNAMIC_ACCOUNTING.getValue());
        when(runrepArgumentProviderMock.getReportConsolidation())
                .thenReturn(BaseExecuteGeneva.NONE_CONSOLIDATED.getValue());
        when(runrepArgumentProviderMock.getExtraFlags()).thenReturn(org.apache.nifi.util.StringUtils.EMPTY);
        when(runrepArgumentProviderMock.getGSQLQuery()).thenReturn("SELECT\n" + //
                "{ AmortizationPrice(Local,PeriodEnd,\"2013/08/09\") }\n" + //
                "FROM bisLocalPosition GIVEN Portfolio = :Portfolio,PeriodStartDate = :PeriodStartDate\n" + //
                "WHERE { investment.key } == \"Bond252\";");
        when(runrepArgumentProviderMock.getFileExtension()).thenReturn(".xml");
        when(runrepArgumentProviderMock.getOutputFormat()).thenReturn("xml");
    }

    @Test
    void testGetCommandStringFromControlCase() {
        this.gsqlCommand = new GSQLCommand(runrepArgumentProviderMock);
        final var commandStr = gsqlCommand.getCommand();

        assertEquals("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "rungsql -f xml -o \"/usr/advent/geneva-20.0.0/share/rslspecs/my-report.xml\" --Portfolio 123-MyPortfolio --PeriodStartDate 2023-01-01T00:00:00 --PeriodEndDate 2023-01-31T00:00:00 --KnowledgeDate 2023-02-01T23:59:59 --PriorKnowledgeDate 2022-12-01T12:34:56\n" + //
                "SELECT\n" + //
                "{ AmortizationPrice(Local,PeriodEnd,\"2013/08/09\") }\n" + //
                "FROM bisLocalPosition GIVEN Portfolio = :Portfolio,PeriodStartDate = :PeriodStartDate\n" + //
                "WHERE { investment.key } == \"Bond252\";\n" + //
                "exit\n" + //
                "EOF\n", commandStr);
    }
}
