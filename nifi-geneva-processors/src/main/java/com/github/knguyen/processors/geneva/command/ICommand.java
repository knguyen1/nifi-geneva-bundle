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

/**
 * Interface representing a command with both obfuscated and unobfuscated forms. This interface provides methods to
 * retrieve different representations of a command, including an obfuscated version for secure contexts, an unobfuscated
 * version for execution, and a loggable part that can be safely logged or displayed without exposing sensitive
 * information.
 *
 * Implementations of this interface should ensure that the obfuscation process effectively masks any sensitive data,
 * while the loggable part should include enough detail for meaningful logging without compromising security.
 */
public interface ICommand {

    /**
     * Retrieves the obfuscated version of the command. This version should mask any sensitive information to prevent
     * its exposure in logs or displays.
     *
     * @return A {@link String} representing the obfuscated command.
     */
    String getObfuscatedCommand();

    /**
     * Retrieves the unobfuscated version of the command. This is the complete command as it should be executed,
     * including sensitive information.
     *
     * @return A {@link String} representing the unobfuscated command.
     */
    String getCommand();

    /**
     * Retrieves a part of the command that can be safely logged. This should exclude any sensitive information while
     * providing enough context for logging purposes.
     *
     * @return A {@link String} representing the loggable part of the command.
     */
    String getLoggablePart();

    /**
     * Retrieves the resource identifier (such as a file or URI) where the output of the command is directed. This
     * method is used to identify the destination for the command's output, which could be a file path, a URI, or any
     * other form of resource identifier. The returned value should be the exact path or URI as used by the command for
     * its output.
     *
     * Note: Depending on the implementation, this resource identifier might contain sensitive information. Hence, it
     * should be handled with the same security considerations as the unobfuscated command.
     *
     * @return A {@link String} representing the output resource identifier for the command.
     */
    String getOutputResource();

    void validate();
}
