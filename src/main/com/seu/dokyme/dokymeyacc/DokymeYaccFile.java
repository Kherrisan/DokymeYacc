package com.seu.dokyme.dokymeyacc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DokymeYaccFile {

    private enum Segment {
        TOKENS, START, DECLARATIONS, PRODUCTIONS, TRANSLATIONS, PROGRAMS
    }

    private List<String> programs;
    private List<String> declarations;
    private Set<Symbol> allSymbols;
    private List<Production> productions;
    private Symbol start;

    public static DokymeYaccFile read(String filePath) {
        FileReader reader = new FileReader(filePath);

        DokymeYaccFile yaccFile = new DokymeYaccFile();

        String line;
        Production currentProduction = new Production();
        Segment segment = Segment.TOKENS;
        try {
            while ((line = reader.readline()) != null) {
                switch (segment) {
                    case TOKENS:
                        if (line.equals("%%")) {
                            segment = Segment.START;
                            break;
                        }
                        yaccFile.allSymbols.add(new Symbol(line.trim()));
                        break;
                    case START:
                        if (line.equals("%%")) {
                            segment = Segment.DECLARATIONS;
                            break;
                        }
                        yaccFile.start = new Symbol(line.trim());
                        break;
                    case DECLARATIONS:
                        if (line.equals("%%")) {
                            segment = Segment.PRODUCTIONS;
                            break;
                        }
                        yaccFile.declarations.add(line);
                        break;
                    case PRODUCTIONS:
                        if (line.equals("%%")) {
                            segment = Segment.PROGRAMS;
                            break;
                        } else if (line.equals("{")) {
                            segment = Segment.TRANSLATIONS;
                            break;
                        }
                        currentProduction = parseRule(yaccFile, line);
                        break;
                    case TRANSLATIONS:
                        if (line.equals("}")) {
                            segment = Segment.PRODUCTIONS;
                            break;
                        }
                        currentProduction.translations.add(line.trim());
                        break;
                    case PROGRAMS:
                        yaccFile.programs.add(line);
                        break;
                }
            }
        } catch (Exception e) {
            Logger.error(e);
        }

        reader.release();
        return yaccFile;
    }

    private static Production parseRule(DokymeYaccFile yaccFile, String line) throws Exception {
        int column = line.indexOf(':');
        if (column == -1) {
            throw new Exception("Dokycc file format error");
        }
        Production production = new Production();
        String left = line.substring(0, column).trim();
        String right = line.substring(column + 1).trim();
        Symbol leftSide = parseSymbol(yaccFile, left);
        production.left = leftSide;
        leftSide.productions.add(production);
        for (String word : right.split(" ")) {
            Symbol rightSide = parseSymbol(yaccFile, word);
            rightSide.in.add(production);
            production.rights.add(rightSide);
        }
        return production;
    }

    private static Symbol parseSymbol(DokymeYaccFile yaccFile, String word) {
        word = word.trim();
        for (Symbol symbol : yaccFile.allSymbols) {
            if (symbol.name.equals(word)) {
                return symbol;
            }
        }
        Symbol n = new Symbol(word);
        yaccFile.allSymbols.add(n);
        return n;
    }

    public DokymeYaccFile() {
        programs = new ArrayList<>();
        declarations = new ArrayList<>();
        productions = new ArrayList<>();
        allSymbols = new HashSet<>();
    }
}
