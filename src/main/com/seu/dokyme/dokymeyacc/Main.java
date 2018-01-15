package com.seu.dokyme.dokymeyacc;

import org.apache.commons.cli.*;

public class Main {
    public static boolean debug;
    public static String yaccFilePath;
    public static String sourceFilePath;
    public static String packageName;

    public static void main(String[] args) {
        initCmdParamaters(args);
        DokymeYaccFile yaccFile = DokymeYaccFile.read(yaccFilePath);
        LRParsingTable parsingTable = LRParsingTable.build(yaccFile);
        CodeGenerator generator = new CodeGenerator(yaccFile, parsingTable);
        generator.generate(sourceFilePath, packageName);
    }

    public static void initCmdParamaters(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        try {
            options.addOption("h", "help", false, "Print the help information.");
            options.addOption("v", "version", false, "Print the version information.");
            options.addOption("y", "yacc", true, "The path of yacc file.");
            options.addOption("o", "output", true, "The path to generate new parser source .java file.");
            options.addOption("d", "debug", false, "Print debug output.");
            options.addOption("p", "package", true, "Specified the generated source file package name.Default:defualt");
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Options", options);
                System.exit(0);
            } else if (cmd.hasOption("v")) {
                System.out.println("DokymeLex parser generator from Southeast University Software Academy.\nVersion:1.0.0.\n2017-12-20");
                System.exit(0);
            }
            if (cmd.hasOption("y")) {
                yaccFilePath = cmd.getOptionValue("y");
            } else {
                System.out.println("Error:yacc file path required!");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Options", options);
                System.exit(0);
            }
            if (cmd.hasOption("o")) {
                sourceFilePath = cmd.getOptionValue("o");
            } else {
                sourceFilePath = "Parser.java";
            }
            if (cmd.hasOption("p")) {
                packageName = cmd.getOptionValue("p");
            }
            debug = cmd.hasOption("d");
        } catch (ParseException e) {
            System.out.println("Error:Unpected option.Try -h for usage help.");
            System.out.println(options.getOptions());
            System.exit(0);
        }
    }
}
