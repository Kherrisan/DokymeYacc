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
        this.mainSwitchBlock = new SwitchBlock("stateStack.peek()");
        this.symbolMap = new HashMap<>();

        for (int i = 0; i < parsingTable.tableEntries.size(); i++) {
            //对于LR分析表中的每一行，代表一个状态。
            LRParsingTable.TableEntry entry = parsingTable.tableEntries.get(i);
            //为这个状态建立一个新的函数。
            FuncDefBlock funcDef = new FuncDefBlock("state_" + i);
            //这个状态函数里有一个switch，用于根据当前token采取不同措施。
            SwitchBlock innerSwitch = new SwitchBlock("token");
            for (Symbol actionItem : entry.actions.keySet()) {
                //根据LR分析表中该项目的actions项目决定是要移进还是规约。
                GenericBlock caseInnerBlock = new GenericBlock();
                String actionStr = entry.actions.get(actionItem);
                if (actionStr.contains("S")) {
                    //如果是移进item，把当前读头下token压入符号栈，把状态压入状态栈。
                    int nextState = Integer.valueOf(actionStr.substring(actionStr.indexOf("S") + 1));
                    caseInnerBlock.putBlock("symbolStack.push(\"" + actionItem + "\");");
                    caseInnerBlock.putBlock("stateStack.push(" + nextState + ");");
                } else if (actionStr.contains("R")) {
                    //如果是规约项目，从符号栈中弹出多个可规约串的符号，并压入新规约后的符号，从状态栈弹出相等数量的状态。
                    if (actionStr.contains("accept")) {
                        //如果遇到accept，说明已经分析结束。
                        caseInnerBlock.putBlock("end();");
                    } else {
                        //得到要规约的production的id。
                        int productionIndex = Integer.valueOf(actionStr.substring(actionStr.indexOf("R") + 1));
                        //得到要弹出的符号个数。
                        Production production = yaccFile.findProductionById(productionIndex);
                        int symbolsNum = production.rights.size();
                        //构造一个for循环来弹出相同数量的符号和状态。
                        ForBlock forBlock = new ForBlock(symbolsNum);
                        GenericBlock forInnerBlock = new GenericBlock();
                        //在弹出符号的同时把符号加入一个列表中。
                        forInnerBlock.putBlock("reduce.add(symbolStack.pop());");
                        forInnerBlock.putBlock("stateStack.pop();");
                        forBlock.setBody(forInnerBlock);
                        //构造for循环结束。
                        caseInnerBlock.putBlock(forBlock);
                        //将产生式右侧的符号压入符号栈。
                        caseInnerBlock.putBlock("symbolStack.push(\"" + production.left + "\");");
                        //通过符号栈栈顶（刚刚压入的非终结符符号）和goto项目得到状态，压入状态栈。
                        //这里又需要一个switch块。
                        SwitchBlock gotoSwitchBlock = new SwitchBlock("symbolStack.peek()");
                        for (Symbol gotoItem : entry.gotos.keySet()) {
                            int gotoState = entry.gotos.get(gotoItem);
                            GenericBlock gotoCaseInnerBlock = new GenericBlock("stateStack.push(" + gotoState + ");");
                            gotoSwitchBlock.putCase(gotoItem.toString(), gotoCaseInnerBlock);
                        }
                        //构造goto的switch块结束。
                        caseInnerBlock.putBlock(gotoSwitchBlock);
                    }
                }
                innerSwitch.putCase("\"" + actionItem.toString() + "\"", caseInnerBlock);
            }
            funcDef.setBody(innerSwitch);
            funcBlocks.add(funcDef);
            mainSwitchBlock.putCase("" + i, new FuncCallBlock("state_" + i));
        }
        for (String declrLine : yaccFile.declarations) {
            declrBlock.putLine(declrLine);
        }
        return;
    }

    public void generate(String outputPath, String templatePath) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
            template = new BufferedReader(new InputStreamReader(new FileInputStream(templatePath)));

            String line;
            while ((line = template.readLine()) != null) {
                if (line.contains(LABEL_CLASS)) {
                    //TODO
                    continue;
                } else if (line.contains(LABEL_SWITCH)) {
                    int pos = line.indexOf(LABEL_SWITCH);
                    indent = pos / 4;
                    mainSwitchBlock.generate();
                } else if (line.contains(LABEL_VARIABLE)) {
                    int pos = line.indexOf(LABEL_VARIABLE);
                    indent = pos / 4;
                    declrBlock.generate();
                } else if (line.contains(LABEL_PROGRAM)) {
                    int pos = line.indexOf(LABEL_PROGRAM);
                    indent = pos / 4;
                    for (Block func : funcBlocks) {
                        func.generate();
                    }
                    for (String line1 : yaccFile.programs) {
                        writeLine(line1);
                    }
                } else {
                    writeLine(line);
                }
                indent = 0;
            }

            writer.close();
            template.close();
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
            return this;
        }

        @Override
        public void generate() {
            writeLine("switch(" + condition + ") {");
            indent++;
            for (String ca : cases.keySet()) {
                writeLine("case " + ca + ":");
                indent++;
                cases.get(ca).generate();
                writeLine("break;");
                indent--;
            }
            writeLine("default:");
            indent++;
            defaultBlock.generate();
            writeLine("break;");
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
            return this;
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
        private String returnType = "void";
        private Map<String, String> paramaters = new LinkedHashMap<>();
        private Block body;

        public FuncDefBlock(String name) {
            this.name = name;
        }

        public FuncDefBlock setBody(Block body) {
            this.body = body;
            return this;
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
        private List<Block> blocks = new ArrayList<>();
        private String line;

        public GenericBlock() {
        }

        public GenericBlock(String line) {
            this.line = line;
        }

        public GenericBlock putBlock(String line) {
            Block newBlock = new GenericBlock(line);
            blocks.add(newBlock);
            return this;
        }

        public GenericBlock putBlock(Block block) {
            blocks.add(block);
            return this;
        }

        public GenericBlock putLine(String line) {
            this.line = line;
            return this;
        }

        @Override
        public void generate() {
            if (line == null) {
                for (Block block : blocks) {
                    block.generate();
                }
            } else {
                writeLine(line);
            }
        }
    }

    private class ForBlock implements Block {
        private int number;
        private Block body;

        public ForBlock(int number) {
            this.number = number;
        }

        public ForBlock setBody(Block body) {
            this.body = body;
            return this;
        }

        @Override
        public void generate() {
            writeLine("for(int i=0;i<" + number + ";i++) {");
            indent++;
            body.generate();
            indent--;
            writeLine("}");
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
        DokymeYaccFile yaccFile = DokymeYaccFile.read("rules.dokycc");
        LRParsingTable parsingTable = LRParsingTable.build(yaccFile);
        CodeGenerator generator = new CodeGenerator(yaccFile, parsingTable);
        generator.generate("./src/main/com/seu/dokyme/dokymeyacc/Parser.java", "./template.java");
    }
}
