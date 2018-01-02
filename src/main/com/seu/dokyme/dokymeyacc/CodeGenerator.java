package com.seu.dokyme.dokymeyacc;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @author Dokyme
 */
public class CodeGenerator {
    private static final String LABEL_CLASS = "//CLASS";
    private static final String LABEL_SWITCH = "//SWITCH";
    private static final String LABEL_VARIABLE = "//VARIABLE";
    private static final String LABEL_PROGRAM = "//PROGRAM";
    private static final String LABEL_TOKEN_ID = "//TOKEN_ID";

    private int indent;

    private BufferedWriter writer;
    private BufferedReader template;
    private DokymeYaccFile yaccFile;
    private LRParsingTable parsingTable;

    private Map<Symbol, Integer> symbolMap;
    private List<Block> funcBlocks;
    private List<Block> clsBlocks;
    private GenericBlock declrBlock;
    private GenericBlock tokenIdBlock;
    private SwitchBlock mainSwitchBlock;

    public CodeGenerator(DokymeYaccFile yaccFile, LRParsingTable parsingTable) {
        this.yaccFile = yaccFile;
        this.parsingTable = parsingTable;
        this.funcBlocks = new ArrayList<>();
        this.clsBlocks = new ArrayList<>();
        this.declrBlock = new GenericBlock();
        this.tokenIdBlock = new GenericBlock();
        this.mainSwitchBlock = new SwitchBlock("state");
        this.symbolMap = new HashMap<>();

        int id = 0;
        for (Symbol symbol : yaccFile.allSymbols) {
            tokenIdBlock.putLine("symbolMap.put(\"" + symbol.name + "\"," + (id++) + ");");
        }

        for (int i = 0; i < parsingTable.tableEntries.size(); i++) {
            LRParsingTable.TableEntry entry = parsingTable.tableEntries.get(i);
            Block funcDef = new FuncDefBlock("state_" + i);
            SwitchBlock innerSwitch = new SwitchBlock("token");
            for (Symbol action : entry.actions.keySet()) {
                innerSwitch.putCase("" + symbolMap.get(action), );
            }
            for (Symbol go : entry.gotos.keySet()) {

            }
            funcBlocks.add(funcDef);
            mainSwitchBlock.putCase("" + i, new FuncCallBlock("state_" + i));
        }
        for (String declrLine : yaccFile.declarations) {
            declrBlock.putLine(declrLine);
        }

    }

    public void generate(String outputPath, String templatePath) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
            template = new BufferedReader(new InputStreamReader(new FileInputStream(templatePath)));

            String line;
            while ((line = template.readLine()) != null) {
                if (line.contains(LABEL_TOKEN_ID)) {
                    tokenIdBlock.generate();
                } else if (line.contains(LABEL_CLASS)) {
                    //TODO
                    continue;
                } else if (line.contains(LABEL_SWITCH)) {
                    mainSwitchBlock.generate();
                } else if (line.contains(LABEL_VARIABLE)) {
                    declrBlock.generate();
                } else if (line.contains(LABEL_PROGRAM)) {
                    for (Block func : funcBlocks) {
                        func.generate();
                    }
                } else {
                    writeLine(line);
                }
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private interface Block {

        /**
         * 提供代码块生成代码的接口，由各自特定类型的代码块实现之并可以递归调用。
         */
        void generate();
    }

    private class SwitchBlock implements Block {
        private String condition;
        private Map<String, Block> cases = new LinkedHashMap<>();
        private Block defaultBlock = new GenericBlock();

        public SwitchBlock(String condition) {
            this.condition = condition;
        }

        public SwitchBlock putCase(String name, Block block) {
            this.cases.put(name, block);
            return this;
        }

        public SwitchBlock putDefaultCase(Block block) {
            this.defaultBlock = block;
        }

        @Override
        public void generate() {
            writeLine("switch(" + condition + ") {");
            indent++;
            for (String ca : cases.keySet()) {
                writeLine("case " + ca + ":");
                indent++;
                cases.get(ca).generate();
                writeLine("break");
                indent--;
            }
            writeLine("default:");
            indent++;
            defaultBlock.generate();
            writeLine("break");
            indent--;
            indent--;
            writeLine("}");
        }
    }

    private class ClassBlock implements Block {
        private List<String> attributes = new ArrayList<>();
        private String name;

        public ClassBlock(String name) {
            this.name = name;
        }

        public ClassBlock putAttr(String name) {
            this.attributes.add(name);
        }

        @Override
        public void generate() {
            writeLine("class " + name + "{");
            indent++;
            for (String attr : attributes) {
                writeLine("public String " + attr + ";");
            }
            indent--;
            writeLine("}");
        }
    }

    private class IfBlock implements Block {
        private String condition;
        private Block body;

        public IfBlock(String condition, Block body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public void generate() {
            writeLine("if(" + condition + ") {");
            indent++;
            body.generate();
            indent--;
            writeLine("{");
        }
    }

    private class FuncCallBlock implements Block {
        private String name;
        private List<String> arguements = new ArrayList<>();

        public FuncCallBlock(String name) {
            this.name = name;
        }

        public FuncCallBlock putArg(String name) {
            this.arguements.add(name);
            return this;
        }

        @Override
        public void generate() {
            String args = StringUtils.join(arguements.toArray(), ", ");
            writeLine(name + "(" + args + ");");
        }
    }

    private class FuncDefBlock implements Block {
        private String name;
        private String returnType;
        private Map<String, String> paramaters = new LinkedHashMap<>();
        private Block body;

        public FuncDefBlock(String name) {
            this.name = name;
        }

        public FuncDefBlock setBody(Block body) {
            this.body = body;
        }

        public FuncDefBlock setReturnType(Class cls) {
            returnType = cls.getSimpleName();
            return this;
        }

        public FuncDefBlock putParamater(String name, Class cls) {
            paramaters.put(name, cls.getSimpleName());
            return this;
        }

        @Override
        public void generate() {
            List<String> pairs = new ArrayList<>();
            for (String parmName : paramaters.keySet()) {
                pairs.add(paramaters.get(parmName) + " " + parmName);
            }
            String params = StringUtils.join(pairs.toArray(), ", ");
            writeLine("public " + returnType + " " + name + "(" + params + ") {");
            indent++;
            body.generate();
            indent--;
            writeLine("}");
        }
    }

    private class GenericBlock implements Block {
        private List<String> lines = new ArrayList<>();

        public void putLine(String line) {
            lines.add(line);
        }

        @Override
        public void generate() {
            for (String line : lines) {
                writeLine(line);
            }
        }
    }

    private void writeLine(String line) {
        StringBuilder builder = new StringBuilder(line);
        for (int i = 0; i < indent; i++) {
            builder.insert(0, "\t");
        }
        builder.append("\n");
        try {
            writer.write(builder.toString());
        } catch (IOException e) {
            Logger.error(e);
            System.exit(1);
        }
    }

    private void writeBlock(Block block) throws IOException {
        block.generate();
    }

    public static void main(String[] args) {

    }
}
