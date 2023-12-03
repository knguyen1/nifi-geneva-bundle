package com.github.knguyen.processors.utils;

import java.nio.file.Paths;
import java.util.UUID;

public class StringUtils {
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
    public static String getGuidFilename(String directory) {
        String guid = UUID.randomUUID().toString().toLowerCase();
        String filename = guid + ".csv";
        return Paths.get(directory, filename).toString();
    }
}
