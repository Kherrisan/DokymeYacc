package com.seu.dokyme.dokymeyacc;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

//CLASS

/**
 * @author Dokyme
 */
public class Parser {
    private static final String TOKEN_SPLITER = ",";
    private static final int TOKEN_TOKS_NUMBER = 3;

    private Stack<Integer> stateStack;
    private Stack<Symbol> symbolStack;
    private String outputPath;
    private String inputPath;
    private BufferedWriter outputWriter;
    private BufferedReader inputReader;
    private boolean debug = false;

    private String raw;
    private int innerCode;
    private int token;
    private String tokenStr;
    private int state;
    private Map<String, Integer> symbolMap;

    //VARIABLE

    public Parser() {
        stateStack = new Stack<>();
        symbolStack = new Stack<>();
        symbolMap = new HashMap<>();
        //TOKEN_ID
    }

    private void run() {
        try {
            while ((tokenStr = readToken()) != null) {
                //SWITCH
            }
            inputReader.close();
            outputWriter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private String readToken() throws Exception {
        String line = inputReader.readLine();
        if (line == null) {
            return null;
        }
        int lp = line.indexOf('(');
        int rp = line.indexOf(')');
        if (lp == -1 || rp == -1 || (!line.contains(TOKEN_SPLITER))) {
            throw new Exception("Token file wrong format!");
        }
        String[] toks = line.substring(lp + 1, rp).split(",");
        if (toks.length != TOKEN_TOKS_NUMBER) {
            throw new Exception("Token file wrong format!");
        }
        raw = toks[0];
        innerCode = Integer.valueOf(toks[2]);
        debug("Read token:<" + toks[0] + "," + toks[1] + "," + toks[2] + ">");
        token = symbolMap.get(toks[1]);
        return toks[1];
    }

    private void debug(String content) {
        if (debug) {
            System.out.println("[DEBUG]" + content);
        }
    }

    private void parseCmdArgs(String[] args) {
        if (args.length < 1) {

            System.out.println("Token file generated by lexical scanner unspecified.");
            System.out.println("Using -h for help.");
            System.exit(1);
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("-h".equals(arg)) {
                System.out.println("Usage : java Parser [Options] FILE");
                System.out.println("Options : ");
                System.out.println("\t-o\tOUTPUT\tSpecify the output path of file which contains AST.\n\t\t\t\tIf unspecified,the AST will only be printed on SYSOUT.");
                System.out.println("\t-d\t\t\tShow debug information.");
                System.out.println("\t-h\t\t\tShow help document.");
                System.out.println("\t-v\t\t\tShow version information.");
                System.exit(0);
            } else if ("-v".equals(arg)) {
                System.out.println("Parser generated by DokymeYacc.2018/1/2.\nHappy new year~");
                System.exit(0);
            } else if ("-o".equals(arg)) {
                i++;
                try {
                    outputPath = args[i];
                    outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    System.exit(1);
                }
            } else if ("-d".equals(arg)) {
                debug = true;
            } else {
                if (i != args.length - 1) {
                    System.out.println("Wrong arguement for '" + arg + "'");
                    System.out.println("Using -h for help.");
                } else {
                    inputPath = args[i];
                    try {
                        inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        System.exit(1);
                    }
                }
            }
        }
    }

    //PROGRAM

//    public static void main(String[] args) {
//        Parser parser = new Parser();
//        parser.parseCmdArgs(args);
//        parser.run();
//    }
}
