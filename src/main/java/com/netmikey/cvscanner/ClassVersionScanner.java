package com.netmikey.cvscanner;

import java.io.File;
import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * Main class for running the Class Version Scanner.
 * 
 * @author netmikey
 */
public class ClassVersionScanner {
    /**
     * Main method.
     * 
     * @param argv
     *            Command-Line arguments.
     * @throws Exception
     *             Something went horribly wrong.
     */
    public static void main(String[] argv) throws Exception {
        // Command line stuff...
        Options opt = new Options();
        opt.addOption("d", "dir", true, "the root directory from which to search for class files and java archives "
            + "(if not set, the current working directory will be used).");
        opt.addOption("n", "newer", true, "only look for class files compiled for the specified JRE and newer");
        opt.addOption("o", "older", true, "only look for class files compiled for the specified JRE and older");
        // I know java logging can be configured in an "awesome" *cough* way,
        // but srsly, nobody wants to do that for a little command line tool
        // like this...
        opt.addOption("v", "verbose", false, "display more processing info");
        opt.addOption("h", "help", false, "display this help info");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(opt, argv);

        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(ClassVersionScanner.class.getName(), opt);
            return;
        }

        Scanner scanner = new Scanner();
        File baseDir = new File(cmd.hasOption("dir") ? cmd.getOptionValue("dir") : System.getProperty("user.dir"));
        scanner.setMinVersion(Scanner.VERSIONS.get(cmd.getOptionValue("newer")));
        scanner.setMaxVersion(Scanner.VERSIONS.get(cmd.getOptionValue("older")));
        if (cmd.hasOption("verbose")) {
            scanner.setFineLevel(Level.INFO);
        }
        scanner.scanForVersion(baseDir);
    }
}
