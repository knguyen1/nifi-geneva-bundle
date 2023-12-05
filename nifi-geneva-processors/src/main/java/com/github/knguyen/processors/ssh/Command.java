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
package com.github.knguyen.processors.ssh;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.nifi.util.StringUtils;

/**
 * Represents a command with both its original (unobfuscated) and obfuscated forms. This class encapsulates a command
 * string that might contain sensitive information and its safer, obfuscated counterpart for use in contexts where the
 * original command needs to be concealed, such as logging or user displays.
 *
 * The class provides methods to retrieve both the original and obfuscated command strings, as well as a method to
 * retrieve a part of the command that can be safely logged or displayed. This class is typically used in scenarios
 * where commands are executed in a secure environment and there's a need to handle sensitive information cautiously.
 */
public class Command implements ICommand {
    private final String commandToExecute;
    private final String obfuscatedCommand;
    private final String outputResource;

    /**
     * Constructs a new Command instance with specified unobfuscated and obfuscated command strings.
     *
     * @param command
     *            The unobfuscated command string.
     * @param obfuscatedCommand
     *            The obfuscated version of the command string.
     *
     * @throws IllegalArgumentException
     *             if either command or obfuscatedCommand is null.
     */
    public Command(String command, String obfuscatedCommand, String outputResource) {
        if (StringUtils.isBlank(command) || StringUtils.isBlank(obfuscatedCommand)) {
            throw new IllegalArgumentException("Command and obfuscated command must not be null.");
        }
        this.commandToExecute = command;
        this.obfuscatedCommand = obfuscatedCommand;
        this.outputResource = outputResource;
    }

    @Override
    public String getCommand() {
        return commandToExecute;
    }

    @Override
    public String getObfuscatedCommand() {
        return obfuscatedCommand;
    }

    /**
     * Returns a loggable part of the command. In this implementation, it returns the unobfuscated command, but it can
     * be modified to return a version that masks or removes sensitive information.
     *
     * @return A loggable part of the command.
     */
    @Override
    public String getLoggablePart() {
        return getObfuscatedCommand();
    }

    /**
     * Retrieves the output resource path from the command. This method utilizes the extractPath method to parse the
     * command string and extract the path or resource identifier specified after the '-o' option in the command. This
     * path can be a file path, a URI, or any other form of resource identifier where the command directs its output.
     *
     * Note: If the command does not include an '-o' option or if the pattern is not matched, this method will return
     * null.
     *
     * @return A String representing the output resource path, or null if it cannot be extracted from the command.
     */
    @Override
    public String getOutputResource() {
        if (StringUtils.isNotBlank(outputResource))
            return outputResource;

        return extractPath(commandToExecute);
    }

    /**
     * Extracts and returns the path from the given command string. This method searches for a specific pattern in the
     * command string that follows '-o', which typically represents an output resource (like a file path or URI). The
     * method uses regular expressions to identify and extract this part of the command.
     *
     * Note: This method assumes that the path immediately follows the '-o' flag and is separated by a space. It will
     * return null if the pattern is not found in the input string.
     *
     * @param input
     *            The command string from which to extract the path.
     *
     * @return The extracted path as a String, or null if the pattern is not found.
     */
    private static String extractPath(String input) {
        // Regular expression pattern to find the path after '-o'
        final String regex = "-o\\s+(\\\"[^\\\"]+\\\"|[^\\s]+)";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(input);

        // Check if the pattern is found in the string
        if (matcher.find()) {
            // Return the first captured group
            return matcher.group(1);
        }

        // Return null if no match is found
        return null;
    }
}
