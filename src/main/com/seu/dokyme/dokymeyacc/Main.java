package com.seu.dokyme.dokymeyacc;

import org.apache.commons.cli.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class Main {
    public static boolean debug;
    public static String yaccFilePath;
    public static String sourceFilePath;
    public static String packageName = "com";
    public static boolean compressed = false;

    public static void main(String[] args) {
        initCmdParamaters(args);
        DokymeYaccFile yaccFile = DokymeYaccFile.read(yaccFilePath);
        LRParsingTable parsingTable = LRParsingTable.build(yaccFile);
        CodeGenerator generator = new CodeGenerator(yaccFile, parsingTable);
        if (compressed)
            generator.generate(sourceFilePath, packageName, generator.zipFormatter);
        else
            generator.generate(sourceFilePath, packageName, generator.autoIndentFormatter);
    }

    public static void generateParsingTable(String outputPath) {
        try {
            DokymeYaccFile yaccFile = DokymeYaccFile.read(yaccFilePath);
            byte[] parsingTable = LRParsingTable.build(yaccFile).toString().getBytes();
            FileOutputStream outputStream = new FileOutputStream(outputPath);
            outputStream.write(parsingTable);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initCmdParamaters(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        try {
            options.addOption(Option.builder("t").longOpt("table").hasArg(true).argName("output file").optionalArg(false).desc("Only generate LR parsing table.").build());
            options.addOption("s", "sample", false, "Generate a sample yacc file.");
            options.addOption("c", "compressed", false, "Compressed the source file to a large extent with user-unfriendly format.");
            options.addOption("h", "help", false, "Print the help information.");
            options.addOption("v", "version", false, "Print the version information.");
            options.addOption(Option.builder("y").longOpt("yacc").hasArg(true).argName("input file").optionalArg(false).desc("Specified the input yacc file path.").build());
            options.addOption(Option.builder("o").longOpt("out").hasArg(true).argName("output file").optionalArg(true).desc("Specified the output java source file path.").build());
            options.addOption("d", "debug", false, "Print debug output.");
            options.addOption(Option.builder("p").longOpt("package").hasArg(true).argName("package name").optionalArg(false).desc("Specified the package name of the generated JAVA source file.").build());
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("t")) {
                yaccFilePath = cmd.getOptionValue("y");
                String tablePath = cmd.getOptionValue("t");
                generateParsingTable(tablePath);
                System.out.println("Generating LR parsing table finished.");
                System.exit(0);
            }
            if (cmd.hasOption("c")) {
                compressed = true;
            }
            if (cmd.hasOption("s")) {
                try {
                    BufferedInputStream inputStream = new BufferedInputStream(Main.class.getResourceAsStream("/yacc.txt"));
                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream("./yacc.txt"));
                    byte[] buffer = new byte[4096];
                    inputStream.read(buffer);
                    outputStream.write(buffer);
                    inputStream.close();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                System.out.println("Generate a sample yacc file in the current directory.");
                System.exit(0);
            }
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
