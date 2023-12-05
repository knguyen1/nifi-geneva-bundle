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
    public static String getGuidFilename(final String directory, final FlowFile flowfile) {
        final String flowfileId = flowfile.getAttribute(CoreAttributes.UUID.key());
        final String filename = flowfileId + ".csv";
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
