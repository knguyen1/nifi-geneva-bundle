package com.github.knguyen.processors.geneva;

public class GenevaException extends Exception {
    private String genevaErrorMessage;
    private String command;

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
