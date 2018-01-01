package com.seu.dokyme.dokymeyacc;

import java.util.*;

public class DokymeYaccFile {

    private enum Segment {
        TOKENS, START, DECLARATIONS, PRODUCTIONS, TRANSLATIONS, PROGRAMS
    }

    private List<String> programs;
    private List<String> declarations;
    private Set<Symbol> allSymbols;
    private List<Production> productions;
    private Symbol realStart;
    private Symbol start;
    private Map<Symbol, Set<Symbol>> first;
    private Map<Symbol, Set<Symbol>> follow;

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
                        yaccFile.allSymbols.add(yaccFile.start);
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
        yaccFile.productions.add(production);
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
        first = new HashMap<>();
        follow = new HashMap<>();
    }

    public Production addRealStartSymbol() {
        realStart = new Symbol("%START");
        Production startProduction = new Production();
        startProduction.left = realStart;
        startProduction.rights.add(start);
        start.in.add(startProduction);
        productions.add(startProduction);
        return startProduction;
    }

    public void findFollowSet() {
        follow.get(start).add(Symbol.DollarR);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Symbol B : allSymbols) {
                for (Production production : B.in) {
                    int indexB = production.rights.indexOf(B);
                    Symbol A = production.left;
                    if (indexB == production.rights.size() - 1) {
                        changed = changed || follow.get(B).addAll(follow.get(A));
                    } else {
                        Symbol beta = production.rights.get(indexB + 1);
                        changed = changed || follow.get(B).addAll(first.get(beta));
                        follow.get(B).remove(Symbol.Null);
                        if (beta.canDeduceToNull()) {
                            changed = changed || follow.get(B).addAll(follow.get(A));
                        }
                    }
                }
            }
        }
        return;
    }

    public void findFirstSet() {
        for (Symbol x : allSymbols) {
            if (x.isTerminal()) {
                first.get(x).add(x);
            }
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Symbol x : allSymbols) {
                //如果symbol是个非终结符，那么：
                //1.symbol推出的第一个符号是终结符a，那么first(X)必定包含a。该情况由递归调用的终结情况控制。
                //2.symbol推出的第一个符号是非终结符Y，那么first(X)必定包含first(Y)中所有的非空符号。
                //3.symbol推出的前i个符号都能够推出空，那么first(X)必定包含前i个符号的first的非空符号和第i+1个符号的first的所有符号。
                for (Production production : x.productions) {
                    boolean allCanNull = false;
                    for (int i = 0; i < production.rights.size(); i++) {
                        Symbol Yi = production.rights.get(i);
                        changed = changed || first.get(x).addAll(first.get(Yi));
                        if (Yi.canDeduceToNull()) {
                            first.get(x).remove(Symbol.Null);
                        } else {
                            break;
                        }
                        if (i == production.rights.size() - 1) {
                            allCanNull = true;
                        }
                    }
                    if (allCanNull) {
                        changed = changed || first.get(x).add(Symbol.Null);
                    }
                }
            }
        }
        return;
    }

    public void findFirstFollowSet() {
        for (Symbol symbol : allSymbols) {
            first.put(symbol, new HashSet<>());
            follow.put(symbol, new HashSet<>());
        }
        findFirstSet();
        findFollowSet();
    }

    public Set<Symbol> first(List<Symbol> betaA) {
        Set<Symbol> firstSet = new HashSet<>();
        for (Symbol Yi : betaA) {
            if (Yi.equals(Symbol.PlaceHolder)) {
                firstSet.add(Symbol.PlaceHolder);
                continue;
            }
            firstSet.addAll(first.get(Yi));
            if (Yi.canDeduceToNull()) {
                firstSet.remove(Symbol.Null);
            } else {
                break;
            }
        }
        return firstSet;
    }

    public Set<Symbol> follow(Symbol s) {
        return follow.get(s);
    }

}
