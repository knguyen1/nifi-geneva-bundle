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
package com.github.knguyen.processors.utils;

import java.nio.file.Paths;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;

public final class StringUtils {
    /**
     * Generates a filename with a GUID and appends it to the given directory. The method ensures the correct file path
     * format, even if the directory does not end with a slash. The GUID is generated in lowercase and the filename has
     * a '.csv' extension.
     *
     * @param directory
     *            The directory path to which the filename will be appended.
     *
     * @return A string representing the full path with the generated filename.
     */
    public static String getGuidFilename(final String directory, final FlowFile flowfile, final String extension) {
        final String flowfileId = flowfile.getAttribute(CoreAttributes.UUID.key());
        final String filename = flowfileId + extension;
        return Paths.get(directory, filename).toString();
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * This utility class is not meant to be instantiated. It provides utility methods for String.
     */
    private StringUtils() {
    }
}
