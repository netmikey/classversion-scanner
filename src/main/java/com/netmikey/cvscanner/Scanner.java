package com.netmikey.cvscanner;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Encapsulates the actual class-version scanner logic.
 * 
 * @author netmikey
 */
public class Scanner {
    private static final Logger LOGGER = Logger.getLogger(Scanner.class.getName());

    /* package */static final Map<String, JavaVersion> VERSIONS;

    private static final Map<Long, JavaVersion> CLASS_VERSIONS;

    static {
        JavaVersion[] jVersions = new JavaVersion[] { new JavaVersion(1, 45, "1", "1.1"),
                new JavaVersion(2, 46, "2", "1.2"), new JavaVersion(3, 47, "3", "1.3"),
                new JavaVersion(4, 48, "4", "1.4"), new JavaVersion(5, 49, "5", "1.5", "5.0"),
                new JavaVersion(6, 50, "6", "1.6", "6.0"), new JavaVersion(7, 51, "7", "1.7", "7.0") };

        CLASS_VERSIONS = new HashMap<Long, JavaVersion>();
        VERSIONS = new HashMap<String, JavaVersion>();
        for (JavaVersion version : jVersions) {
            CLASS_VERSIONS.put(version.getClassFileVersion(), version);
            for (String versionAlias : version.getAliases()) {
                VERSIONS.put(versionAlias, version);
            }
        }
    }

    private JavaVersion minVersion;

    private JavaVersion maxVersion;

    private Level fineLevel = Level.FINE;

    /**
     * Main entry method to start scanning according to the current scanner
     * configuration.
     * 
     * @param baseDir
     *            The basedir to start scanning from.
     * @throws IOException
     *             Something went horribly wrong.
     */
    public void scanForVersion(File baseDir) throws IOException {
        LOGGER.log(fineLevel, "Scanning directory: " + baseDir);

        if (!baseDir.isDirectory()) {
            return;
        }

        File[] classFiles = baseDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isDirectory() && isClassFileName(file.getName());
            }
        });

        File[] archiveFiles = baseDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isDirectory() && isArchiveName(file.getName());
            }
        });

        for (File archiveFile : archiveFiles) {
            this.processArchiveFile(archiveFile);
        }

        for (File classFile : classFiles) {
            this.processClassFile(classFile);
        }

        File[] subDirs = baseDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        for (File subDir : subDirs) {
            scanForVersion(subDir);
        }
    }

    private void processClassFile(InputStream inputStream, String filename) throws IOException {
        byte[] versionBytes = new byte[2];
        inputStream.skip(6);
        inputStream.read(versionBytes);

        long decimalVersion = 0;
        for (int i = 0; i < versionBytes.length; i++) {
            decimalVersion = (decimalVersion << 8) + (versionBytes[i] & 0xff);
        }

        boolean filtered = false;

        if (minVersion != null && decimalVersion < minVersion.getClassFileVersion()) {
            filtered = true;
        }
        if (maxVersion != null && decimalVersion > maxVersion.getClassFileVersion()) {
            filtered = true;
        }

        if (!filtered) {
            JavaVersion jVersion = CLASS_VERSIONS.get(decimalVersion);
            String version = jVersion == null ? "(Unknown Classfile Version " + decimalVersion + ")" : String
                .valueOf(jVersion.getVersion());
            LOGGER.info("Found matching class file: " + filename + " compiled for Java version: " + version);
        }
    }

    private void processClassFile(File classFile) throws IOException {
        LOGGER.log(fineLevel, "Processing class file: " + classFile);

        try {
            InputStream fis = new FileInputStream(classFile);
            this.processClassFile(fis, classFile.getPath());
            fis.close();
        } catch (IOException e) {
            System.err.println("Error trying to process class file " + classFile + ": " + e.getMessage());
            throw e;
        }
    }

    private void processArchiveFile(File archiveFile) throws IOException {
        LOGGER.log(fineLevel, "Processing archive file: " + archiveFile);
        ZipInputStream zis = new ZipInputStream(new FileInputStream(archiveFile));
        processArchiveFile(zis, archiveFile.getPath());
        zis.close();
    }

    /**
     * This method was inspired by Layton Smith's ZipReader posted on:
     * http://stackoverflow
     * .com/questions/5075615/java-searching-inside-zips-inside-zips
     * 
     * @param zis
     * @param archivePath
     * @throws IOException
     */
    private void processArchiveFile(final ZipInputStream zis, String archivePath) throws IOException {
        InputStream zipReader = new InputStream() {
            @Override
            public int read() throws IOException {
                if (zis.available() > 0) {
                    return zis.read();
                } else {
                    return -1;
                }
            }

            @Override
            public void close() throws IOException {
                zis.close();
            }
        };

        ZipEntry zipEntry;
        while ((zipEntry = zis.getNextEntry()) != null) {
            if (!zipEntry.isDirectory()) {
                if (isClassFileName(zipEntry.getName())) {
                    LOGGER.log(fineLevel, "Processing class file: " + archivePath + "/" + zipEntry.getName());
                    processClassFile(zipReader, archivePath + "/" + zipEntry.getName());
                }

                if (isArchiveName(zipEntry.getName())) {
                    LOGGER.log(fineLevel, "Recursing into: " + zipEntry.getName());
                    ZipInputStream inner = new ZipInputStream(zipReader);
                    processArchiveFile(inner, archivePath + "/" + zipEntry.getName());
                }
            }
        }
    }

    private boolean isClassFileName(String filename) {
        return filename.toLowerCase().endsWith(".class");
    }

    private boolean isArchiveName(String filename) {
        String[] suffixes = new String[] { ".jar", ".ear", ".war", ".sar", ".rar" };
        for (String suffix : suffixes) {
            if (filename.toLowerCase().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the minVersion.
     * 
     * @return Returns the minVersion.
     */
    public JavaVersion getMinVersion() {
        return minVersion;
    }

    /**
     * Set the minVersion.
     * 
     * @param minVersion
     *            The minVersion to set.
     */
    public void setMinVersion(JavaVersion minVersion) {
        this.minVersion = minVersion;
    }

    /**
     * Get the maxVersion.
     * 
     * @return Returns the maxVersion.
     */
    public JavaVersion getMaxVersion() {
        return maxVersion;
    }

    /**
     * Set the maxVersion.
     * 
     * @param maxVersion
     *            The maxVersion to set.
     */
    public void setMaxVersion(JavaVersion maxVersion) {
        this.maxVersion = maxVersion;
    }

    /**
     * Get the fineLevel.
     * 
     * @return Returns the fineLevel.
     */
    public Level getFineLevel() {
        return fineLevel;
    }

    /**
     * Set the fineLevel.
     * 
     * @param fineLevel
     *            The fineLevel to set.
     */
    public void setFineLevel(Level fineLevel) {
        this.fineLevel = fineLevel;
    }
}
