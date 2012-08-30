package com.netmikey.cvscanner;

import java.util.Arrays;
import java.util.List;

/**
 * Wraps the required metadata of a java version/release.
 * 
 * @author netmikey
 */
public class JavaVersion {
    private int version;

    private List<String> aliases;

    private long classFileVersion;

    /**
     * Default constructor.
     * 
     * @param version
     *            The numeric value of the java version.
     * @param classFileVersion
     *            The class file version that corresponds to this java version.
     * @param aliases
     *            A list of aliases that match this version.
     */
    public JavaVersion(int version, long classFileVersion, String... aliases) {
        this.version = version;
        this.classFileVersion = classFileVersion;
        this.aliases = Arrays.asList(aliases);
    }

    /**
     * Get the version.
     * 
     * @return Returns the version.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Get the aliases.
     * 
     * @return Returns the aliases.
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Get the classFileVersion.
     * 
     * @return Returns the classFileVersion.
     */
    public long getClassFileVersion() {
        return classFileVersion;
    }

}
