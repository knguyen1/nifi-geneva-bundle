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
package com.github.knguyen.processors.geneva.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class StoredQueryCommandTest extends BaseCommandTest {
    private ICommand storedQueryCommand;

    @BeforeEach
    void setup() {
        super.setup();

        // some args for store query command
        when(runrepArgumentProviderMock.getRunCommandName()).thenReturn("run");
        when(runrepArgumentProviderMock.getRunCommandTarget()).thenReturn("Tax Lot Appraisal with Accruals");
        when(runrepArgumentProviderMock.getPortfolioList()).thenReturn("Green");

        // we've tested this in other classes
        // let's just blank them to simplify testing here
        when(runrepArgumentProviderMock.getPeriodStartDate()).thenReturn(null);
        when(runrepArgumentProviderMock.getPeriodEndDate()).thenReturn(null);
        when(runrepArgumentProviderMock.getKnowledgeDate()).thenReturn(null);
        when(runrepArgumentProviderMock.getPriorKnowledgeDate()).thenReturn(null);
    }

    @Test
    void testControlCase() {
        storedQueryCommand = new StoredQueryCommand(runrepArgumentProviderMock);
        final var command = storedQueryCommand.getCommand();

        Assertions.assertDoesNotThrow(() -> storedQueryCommand.validate());
        assertEquals("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "run \"Tax Lot Appraisal with Accruals\" -f csv -o /usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv -p Green\n"
                + //
                "exit\n" + //
                "EOF\n", command);
    }

    // this is documented on page 166 of 'Maintaining Geneva' ver. 20.2
    @ParameterizedTest
    @CsvSource({
        "run,Tax Lot Appraisal with Accruals,\"Tax Lot Appraisal with Accruals\"",
        "runfile,taxlotappacc,taxlotappacc",
        "runf,taxlotappacc,taxlotappacc",
        "runnumber,117,117",
        "runquery,TaxLotAppraisalAccruals,TaxLotAppraisalAccruals",
        "runfile,mypositions.rsl,mypositions.rsl"
    })
    void testAllPossibleCases(final String runName, final String runTarget, final String expectedTarget) {
        when(runrepArgumentProviderMock.getRunCommandName()).thenReturn(runName);
        when(runrepArgumentProviderMock.getRunCommandTarget()).thenReturn(runTarget);
        storedQueryCommand = new StoredQueryCommand(runrepArgumentProviderMock);
        final var command = storedQueryCommand.getCommand();

        Assertions.assertDoesNotThrow(() -> storedQueryCommand.validate());
        assertEquals(String.format("runrep -f empty.lst -b << EOF\n" + //
                "connect usr/pw -k 9999\n" + //
                "%s %s -f csv -o /usr/advent/geneva-20.0.0/share/rslspecs/my-report.csv -p Green\n" + //
                "exit\n" + //
                "EOF\n", runName, expectedTarget), command);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "foo",
        "bar",
        "baz",
        "gotta frop the frook before you frek"
    })
    void testInvalidCommandsShouldThrow(final String runName) {
        when(runrepArgumentProviderMock.getRunCommandName()).thenReturn(runName);
        when(runrepArgumentProviderMock.getRunCommandTarget()).thenReturn("foo");

        storedQueryCommand = new StoredQueryCommand(runrepArgumentProviderMock);
        Assertions.assertThrows(IllegalArgumentException.class, () -> storedQueryCommand.validate());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "-Allen"
    })
    void testInvalidCommandTargetShouldThrow(final String runTarget) {
        when(runrepArgumentProviderMock.getRunCommandName()).thenReturn("runf");
        when(runrepArgumentProviderMock.getRunCommandTarget()).thenReturn(runTarget);

        storedQueryCommand = new StoredQueryCommand(runrepArgumentProviderMock);
        Assertions.assertThrows(IllegalArgumentException.class, () -> storedQueryCommand.validate());
    }
}
