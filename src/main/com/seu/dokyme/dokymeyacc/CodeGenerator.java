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
    private static final String LABEL_REDUCE = "//REDUCE";
    private static final String LABEL_GOTO = "//GOTO";

    private static final String TYPE_PREFIX_INT = "i";
    private static final String TYPE_PREFIX_STRING = "str";
    private static final String TYPE_PREFIX_CHAR = "ch";
    private static final String TYPE_PREFIX_BOOLEAN = "b";

    private int indent;

    private BufferedWriter writer;
    private BufferedReader template;
    private DokymeYaccFile yaccFile;
    private LRParsingTable parsingTable;

    private List<Block> funcBlocks;
    private List<Block> clsBlocks;
    private GenericBlock declrBlock;
    private SwitchBlock mainSwitchBlock;
    private GenericBlock reduceBlock;
    private SwitchBlock gotoBlock;

    public CodeGenerator(DokymeYaccFile yaccFile, LRParsingTable parsingTable) {
        this.yaccFile = yaccFile;
        this.parsingTable = parsingTable;
        this.funcBlocks = new ArrayList<>();
        this.clsBlocks = new ArrayList<>();
        this.declrBlock = new GenericBlock();
        this.mainSwitchBlock = new SwitchBlock("stateStack.peek()");
        this.reduceBlock = new GenericBlock();
        this.gotoBlock = new SwitchBlock("state");
        this.gotoBlock.setBreak(false);
        this.gotoBlock.putDefaultCase(new GenericBlock("return error();"));

        for (int i = 0; i < parsingTable.tableEntries.size(); i++) {
            //对于LR分析表中的每一行，代表一个状态。
            LRParsingTable.TableEntry entry = parsingTable.tableEntries.get(i);
            //为这个状态建立一个新的函数。
            FuncDefBlock funcDef = new FuncDefBlock("state_" + i);
            funcDef.setThrows("Exception");
            //这个状态函数里有一个switch，用于根据当前token采取不同措施。
            SwitchBlock innerSwitch = new SwitchBlock("token.getClass().getSimpleName()");

            //构建这个LR分析表项的goto部分。
            SwitchBlock stateIGoto = new SwitchBlock("symbol.getClassName()");
            stateIGoto.setBreak(false);
            stateIGoto.putDefaultCase(new GenericBlock("return error();"));
            for (Symbol gotoItem : entry.gotos.keySet()) {
                stateIGoto.putCase("\"" + gotoItem.getClassName() + "\"", new GenericBlock("return " + entry.gotos.get(gotoItem) + ";"));
            }
            gotoBlock.putCase("" + i, stateIGoto);

            for (Symbol actionItem : entry.actions.keySet()) {
                //根据LR分析表中该项目的actions项目决定是要移进还是规约。
                GenericBlock caseInnerBlock = new GenericBlock();
                String actionStr = entry.actions.get(actionItem);
                if (actionStr.contains("S")) {
                    //如果是移进item，将当前读头下token的类的对应的新的实例（在readtoken函数中实例化）压入符号栈，把状态压入状态栈。并读下一个符号。
                    int nextState = Integer.valueOf(actionStr.substring(actionStr.indexOf("S") + 1));
                    caseInnerBlock.putBlock("symbolStack.push(token);");
                    caseInnerBlock.putBlock("stateStack.push(" + nextState + ");");
                    caseInnerBlock.putBlock("token = readToken();");
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
                        //将产生式左侧的符号压入符号栈。
                        caseInnerBlock.putBlock("symbolStack.push(new " + production.left.getClassName() + "());");
                        //通过符号栈栈顶（刚刚压入的非终结符符号）和goto表得到状态，压入状态栈。
                        caseInnerBlock.putBlock(new GenericBlock("newState = gott(stateStack.peek(),new " + production.left.getClassName() + "());"));
                        caseInnerBlock.putBlock(new GenericBlock("stateStack.push(newState);"));
                        //输出产生式。
                        caseInnerBlock.putBlock("output(symbolStack.peek());");
                        //调用reduce进行规约处理。
                        caseInnerBlock.putBlock("translate(" + production.id + ");");
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


        //在产生式翻译规则中识别出每个符号所拥有的属性，存入class块。
        Map<Symbol, ClassBlock> classAttributes = new HashMap<>();
        for (Symbol symbol : yaccFile.allSymbols) {
            classAttributes.put(symbol, new ClassBlock(symbol.name.toUpperCase()));
        }

        for (Production production : yaccFile.productions) {
            //对于每个产生式。
            for (String line : production.translations) {
                //对于该产生式的翻译规则的每行。
                if (line.contains("left")) {
                    int indexOfAttribute = line.indexOf("left.") + 5;
                    int indexOfEnd = line.indexOf(":");
                    String attributeName = line.substring(indexOfAttribute, indexOfEnd).trim();
                    ClassBlock classBlock = classAttributes.get(production.left);
                    classBlock.putAttr(attributeName);
                }
                for (Symbol symbol : yaccFile.allSymbols) {
                    //寻找出现的每一个符号。
                    if (line.contains(symbol.toString())) {
                        int start = line.indexOf(symbol.toString()) + symbol.toString().length() + 1;
                        int end = line.indexOf(" ", start);
                        String attributeName = line.substring(start, end).trim();
                        ClassBlock classBlock = classAttributes.get(symbol);
                        classBlock.putAttr(attributeName);
                    }
                }
            }
        }

        for (Symbol eachClass : classAttributes.keySet()) {
            clsBlocks.add(classAttributes.get(eachClass));
        }

        //符号栈栈顶的就是这次规约所使用产生式的右部。
        reduceBlock.putLine("Symbol left = symbolStack.peek();");
        reduceBlock.putLine("Collections.reverse(reduce);");
        SwitchBlock reduceSwitch = new SwitchBlock("production");
        for (Production production : yaccFile.productions) {
            GenericBlock caseBlock = new GenericBlock();
            for (int i = 0; i < production.rights.size(); i++) {
                //将reduce list中的符号与翻译规则中的符号对应起来，一个一个赋值。。。
                caseBlock.putLine(production.rights.get(i).name.toUpperCase() + " " + production.rights.get(i).name + " = reduce.get(" + i + ");");
            }
            for (String line : production.translations) {
                caseBlock.putBlock(line);
            }
            reduceSwitch.putCase("" + production.id, caseBlock);
        }
        reduceBlock.putBlock(reduceBlock);

        return;
    }

    public void generate(String outputPath, String templatePath) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
            template = new BufferedReader(new InputStreamReader(new FileInputStream(templatePath)));

            String line;
            while ((line = template.readLine()) != null) {
                if (line.contains(LABEL_CLASS)) {
                    for (Block block : clsBlocks) {
                        block.generate();
                    }
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
                } else if (line.contains(LABEL_REDUCE)) {
                    reduceBlock.generate();
                } else if (line.contains(LABEL_GOTO)) {
                    gotoBlock.generate();
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
        private boolean hasBreak = true;

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

        public SwitchBlock setBreak(boolean b) {
            this.hasBreak = b;
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
                if (hasBreak) {
                    writeLine("break;");
                }
                indent--;
            }
            writeLine("default:");
            indent++;
            defaultBlock.generate();
            if (hasBreak) {
                writeLine("break;");
            }
            indent--;
            indent--;
            writeLine("}");
        }
    }

    private class ClassBlock implements Block {
        private Map<String, String> attributes = new HashMap<>();
        private String name;

        public ClassBlock(String name) {
            //类名统一为全大写字符串，用以和翻译规则中的实例区分。
            this.name = name.toUpperCase();
        }

        public ClassBlock putAttr(String name) {
            String type;
            if (name.startsWith(TYPE_PREFIX_INT)) {
                type = "int";
            } else if (name.startsWith(TYPE_PREFIX_BOOLEAN)) {
                type = "boolean";
            } else if (name.startsWith(TYPE_PREFIX_CHAR)) {
                type = "char";
            } else {
                type = "String";
            }
            attributes.put(name, type);
            return this;
        }

        @Override
        public void generate() {
            writeLine("class " + name + " extends Symbol{");
            indent++;
            for (String attr : attributes.keySet()) {
                writeLine("public " + attributes.get(attr) + " " + attr + ";");
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
        private String exception;
        private Map<String, String> paramaters = new LinkedHashMap<>();
        private Block body;

        public FuncDefBlock(String name) {
            this.name = name;
        }

        public FuncDefBlock setThrows(String exception) {
            this.exception = exception;
            return this;
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
            if (exception == null) {
                writeLine("public " + returnType + " " + name + "(" + params + ") {");
            } else {
                writeLine("public " + returnType + " " + name + "(" + params + ") throws " + exception + " {");
            }

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
        generator.generate("./src/main/generated/Parser.java", "./template.java");
//        Symbol symbol=new Symbol("nnn");
//        System.out.println(symbol.getClass().getSimpleName());
//        try {
//            Class.forName("com.seu.dokyme.dokymeyacc.Symbol").newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
