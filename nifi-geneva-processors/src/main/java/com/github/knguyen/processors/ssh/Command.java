package com.github.knguyen.processors.ssh;

public class Command implements ICommand {
    private final String command;
    private final String obfuscatedCommand;

    public Command(String command, String obfuscatedCommand) {
        this.command = command;
        this.obfuscatedCommand = obfuscatedCommand;
    }

    public String getCommand() {
        return command;
    }

    public String getObfuscatedCommand() {
        return obfuscatedCommand;
    }

    public String getLoggablePart() {
        return getCommand();
    }
}
