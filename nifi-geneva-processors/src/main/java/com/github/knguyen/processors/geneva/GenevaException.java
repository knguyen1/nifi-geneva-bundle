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

public class GenevaException extends Exception {
    private final String genevaErrorMessage;
    private final String command;

    public GenevaException(final String message, final String errorMessage, final String command) {
        super(message);
        this.genevaErrorMessage = errorMessage;
        this.command = command;
    }

    // Getter for errorMessage
    public String getGenevaErrorMessage() {
        return genevaErrorMessage;
    }

    // Getter for command
    public String getCommand() {
        return command;
    }

    // Custom toString method for more informative error messages
    @Override
    public String toString() {
        return "GenevaError{" + "message='" + getMessage() + '\'' + ", errorMessage='" + genevaErrorMessage + '\''
                + ", command='" + command + '\'' + '}';
    }

    // Method to check if the error is related to a specific command
    public boolean isCommandError(String cmd) {
        return command != null && command.equals(cmd);
    }

    // Method to provide a detailed report of the error
    public String getDetailedReport() {
        return "Error occurred during command execution: " + command + "\nError Message: " + genevaErrorMessage
                + "\nDetailed Message: " + getMessage();
    }
}
