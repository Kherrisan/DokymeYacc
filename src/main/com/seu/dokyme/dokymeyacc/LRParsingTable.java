package com.seu.dokyme.dokymeyacc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LRParsingTable {

    public List<TableEntry> tableEntries;
    private DokymeYaccFile yaccFile;
    public List<LR1State> stateList;

    public static class TableEntry {
        private static int sid = 0;

        public int id;
        public Map<Symbol, String> actions;
        public Map<Symbol, Integer> gotos;

        public TableEntry() {
            id = sid++;
            actions = new HashMap<>();
            gotos = new HashMap<>();
        }

        @Override
        public String toString() {
            String str = id + "[Action:";
            for (Symbol symbol : actions.keySet()) {
                str += symbol + "->" + actions.get(symbol) + ",";
            }
            str += "][Goto:";
            for (Symbol symbol : gotos.keySet()) {
                str += symbol + "->" + gotos.get(symbol) + ",";
            }
            str += "]";
            return str;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        List<Symbol> terminals = new ArrayList<>(yaccFile.getAllTerminals());
        List<Symbol> nonTerminals = new ArrayList<>(yaccFile.getAllNonTerminals());
        for (Symbol terminal : terminals) {
            builder.append("\t").append(terminal.toString());
        }
        for (Symbol nonTerminal : nonTerminals) {
            builder.append("\t").append(nonTerminal.toString());
        }
        builder.append("\r\n");
        for (TableEntry entry : tableEntries) {
            builder.append(entry.id).append("\t");
            for (Symbol tm : terminals) {
                String actions = entry.actions.get(tm);
                if (actions == null) {
                    builder.append("\t");
                } else {
                    builder.append(actions).append("\t");
                }
            }
            for (Symbol ntm : nonTerminals) {
                Integer gotoState = entry.gotos.get(ntm);
                if (gotoState == null) {
                    builder.append("\t");
                } else {
                    builder.append(gotoState).append("\t");
                }
            }
            builder.append("\r\n");
        }
        builder.append("######\r\n");
        for (Production pro : yaccFile.productions) {
            builder.append(pro.id).append("\t").append(pro.toString()).append("\r\n");
        }
        return builder.toString();
    }

    public void buildTable() {
        LR1Item firstItem = new LR1Item(yaccFile.addRealStartSymbol());
        LR1State firstState = LR1State.build(yaccFile, firstItem);
        firstState.closure(yaccFile);
        stateList.add(firstState);
        TableEntry firstEntry = new TableEntry();
        tableEntries.add(firstEntry);

        for (int i = 0; i < stateList.size(); i++) {
            LR1State state = stateList.get(i);
            TableEntry entry = tableEntries.get(i);
            for (Symbol symbol : state.getAllShiftable()) {
                LR1State newState = state.shift(symbol, yaccFile);
                int newStateIndex = stateList.indexOf(newState);
                if (newStateIndex == -1) {
                    stateList.add(newState);
                    tableEntries.add(new TableEntry());
                    newStateIndex = stateList.size() - 1;
                }
                if (symbol.isTerminal()) {
                    entry.actions.put(symbol, "S" + newStateIndex);
                } else {
                    entry.gotos.put(symbol, newStateIndex);
                }
            }
            for (LR1Item item : state.getAllReducable()) {
                for (Symbol symbol : item.predicts) {
                    entry.actions.put(symbol, "R" + item.production.id);
                    if (item.production.left.equals(yaccFile.realStart)) {
                        entry.actions.put(symbol, "R" + item.production.id + "=accept");
                    }
                }
            }
        }
        return;
    }

    public static LRParsingTable build(DokymeYaccFile yaccFile) {
        LRParsingTable parsingTable = new LRParsingTable();
        parsingTable.yaccFile = yaccFile;
        parsingTable.tableEntries = new ArrayList<>();
        parsingTable.stateList = new ArrayList<>();
        yaccFile.findFirstFollowSet();
        parsingTable.buildTable();
        return parsingTable;
    }

    public static void main(String[] args) {
        DokymeYaccFile yaccFile = DokymeYaccFile.read("yacc.txt");
        LRParsingTable lrpt = LRParsingTable.build(yaccFile);
        System.out.println(lrpt);
    }
}
