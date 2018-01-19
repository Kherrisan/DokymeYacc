package com.seu.dokyme.dokymeyacc;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

interface CodeFormatter {
    void doBeforeBlock();

    void doAfterBlock();

    void write(String line);
}


/**
 * @author Dokyme
 */
public class CodeGenerator {
    private class AutoIndentFormatter implements CodeFormatter {
        @Override
        public void doBeforeBlock() {
            indent++;
        }

        @Override
        public void doAfterBlock() {
            indent--;
        }

        @Override
        public void write(String line) {
            CodeGenerator.this.writeLine(line);
        }
    }

    private class ZipFormatter implements CodeFormatter {
        @Override
        public void doBeforeBlock() {

        }

        @Override
        public void doAfterBlock() {

        }

        @Override
        public void write(String line) {
            CodeGenerator.this.write(line);
        }
    }


    private static final String LABEL_PACKAGE = "//PACKAGE";
    private static final String LABEL_CLASS = "//CLASS";
    private static final String LABEL_SWITCH = "//SWITCH";
    private static final String LABEL_VARIABLE = "//VARIABLE";
    private static final String LABEL_PROGRAM = "//PROGRAM";
    private static final String LABEL_GOTO = "//GOTO";
    private static final String LABEL_REDUCTIONS = "//REDUCTIONS";

    private static final String TYPE_PREFIX_INT = "i";
    private static final String TYPE_PREFIX_STRING = "str";
    private static final String TYPE_PREFIX_CHAR = "ch";
    private static final String TYPE_PREFIX_BOOLEAN = "b";

    private int indent;

    public AutoIndentFormatter autoIndentFormatter = new AutoIndentFormatter();
    public ZipFormatter zipFormatter = new ZipFormatter();

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
    private List<Block> reductionFuncBlocks;

    public CodeGenerator(DokymeYaccFile yaccFile, LRParsingTable parsingTable) {
        this.yaccFile = yaccFile;
        this.parsingTable = parsingTable;
        this.funcBlocks = new ArrayList<>();
        this.clsBlocks = new ArrayList<>();
        this.reductionFuncBlocks = new ArrayList<>();
        this.declrBlock = new GenericBlock();
        this.mainSwitchBlock = new SwitchBlock("stateStack.peek()");
        this.reduceBlock = new GenericBlock();
        this.gotoBlock = new SwitchBlock("state");
        this.gotoBlock.setBreak(false);
        this.gotoBlock.putDefaultCase(new GenericBlock("return error();"));

        for (Symbol symbol : yaccFile.allSymbols) {
            clsBlocks.add(new ClassBlock(symbol.getClassName()));
        }

        for (int i = 0; i < parsingTable.tableEntries.size(); i++) {
            //对于LR分析表中的每一行，代表一个状态。
            LRParsingTable.TableEntry entry = parsingTable.tableEntries.get(i);
            //为这个状态建立一个新的函数。
            FuncDefBlock funcDef = new FuncDefBlock("state_" + i);
            //这个状态函数里有一个switch，用于根据当前token采取不同措施。
            SwitchBlock stateInnerSwitch = new SwitchBlock("token.getName()");
            //构建这个LR分析表项的goto部分。
            SwitchBlock stateIGoto = new SwitchBlock("symbol.getName()");
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
                    caseInnerBlock.putBlock(new FuncCallBlock("pushAndReadNext").putArg("" + nextState));
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
                        FuncCallBlock popStatesCall = new FuncCallBlock("popStates");
                        popStatesCall.putArg("" + symbolsNum);
                        caseInnerBlock.putBlock(popStatesCall);
                        //从符号栈中弹出与产生式右部相同数目的符号，并保留子类型引用。
                        List<String> keptSymbols = new ArrayList<>();
                        String thisSymbolName = production.left.toString();
                        FuncCallBlock callBlock = new FuncCallBlock("translate_" + production.id);
                        callBlock.putArg(thisSymbolName);
                        List<String> reversed = new ArrayList<>();
                        keptSymbols.add(thisSymbolName);
                        caseInnerBlock.putBlock(production.left.getClassName() + " " + thisSymbolName + " = new " + production.left.getClassName() + "();");
                        for (int j = production.rights.size() - 1; j >= 0; j--) {
                            thisSymbolName = production.rights.get(j).toString();
                            if (keptSymbols.contains(thisSymbolName)) {
                                thisSymbolName = thisSymbolName + "_" + keptSymbols.lastIndexOf(thisSymbolName);
                            }
                            caseInnerBlock.putBlock(production.rights.get(j).getClassName() + " " + thisSymbolName + " = (" + production.rights.get(j).getClassName() + ")symbolStack.pop();");
                            keptSymbols.add(thisSymbolName);
                            reversed.add(thisSymbolName);
//                            callBlock.putArg(thisSymbolName);
                        }
                        for (int j = reversed.size() - 1; j >= 0; j--) {
                            callBlock.putArg(reversed.get(j));
                        }
                        caseInnerBlock.putBlock(callBlock);
                        FuncCallBlock pushAndGotoCall = new FuncCallBlock("pushAndGoto");
                        pushAndGotoCall.putArg(production.left.toString());
                        caseInnerBlock.putBlock(pushAndGotoCall);
                    }
                }
                stateInnerSwitch.putCase("\"" + actionItem.toString().toUpperCase() + "\"", caseInnerBlock);
            }
            funcDef.setBody(stateInnerSwitch);
            funcBlocks.add(funcDef);
            mainSwitchBlock.putCase("" + i, new FuncCallBlock("state_" + i));
        }
        for (String declrLine : yaccFile.declarations) {
            declrBlock.putBlock(declrLine);
        }
        yaccFile.productions.remove(yaccFile.productions.size() - 1);

        for (Production production : yaccFile.productions) {
            FuncDefBlock funcDefBlock = new FuncDefBlock("translate_" + production.id);
            String left;
            if (production.rights.contains(production.left)) {
                left = production.left.toString() + "_l";
            } else {
                left = production.left.toString();
            }
            funcDefBlock.putParamater(left, production.left.getClassName());
            FuncCallBlock defaultOutput = new FuncCallBlock("output");
            defaultOutput.putArg(left);
            List<String> keptSymbols = new ArrayList<>();
            for (Symbol right : production.rights) {
                String thisSymbolName = right.toString();
                if (keptSymbols.contains(thisSymbolName)) {
                    thisSymbolName = thisSymbolName + "_" + keptSymbols.lastIndexOf(thisSymbolName);
                }
                funcDefBlock.putParamater(thisSymbolName, right.getClassName());
                defaultOutput.putArg(thisSymbolName);
                keptSymbols.add(thisSymbolName);
            }
            GenericBlock body = new GenericBlock();
            body.putBlock(new AnnotationBlock(production.toString()));
            body.putBlock(defaultOutput);
            funcDefBlock.setBody(body);
            reductionFuncBlocks.add(funcDefBlock);
        }

        //符号栈栈顶的就是这次规约所使用产生式的右部。
        reduceBlock.putBlock("Symbol left = symbolStack.peek();");
        for (Production production : yaccFile.productions) {
            GenericBlock caseBlock = new GenericBlock();
            FuncCallBlock callBlock = new FuncCallBlock("translate_" + production.id);
            List<String> keptSymbols = new ArrayList<>();
            callBlock.putArg(production.left.toString());
            List<Symbol> rights = new ArrayList<>();
            for (int i = 0; i < production.rights.size(); i++) {
                String thisSymbolName = production.rights.get(i).toString();
                if (keptSymbols.contains(thisSymbolName)) {
                    thisSymbolName = production.rights.get(i).toString() + "_" + keptSymbols.lastIndexOf(thisSymbolName);
                }
                caseBlock.putBlock(production.rights.get(i).name.toUpperCase() + " " + thisSymbolName + " = reduce.get(" + i + ");");
                rights.add(production.rights.get(i));
                keptSymbols.add(production.rights.get(i).toString());
            }
            Collections.reverse(rights);
            for (Symbol right : rights) {
                callBlock.putArg(right.toString());
            }
            caseBlock.putBlock(callBlock);
        }

    }

    public void generate(String outputPath, String packageName, CodeFormatter formatter) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
//            template = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/template.txt")));
            template = new BufferedReader(new InputStreamReader(new FileInputStream("./template.txt")));

            String line;
            while ((line = template.readLine()) != null) {
                if (line.contains(LABEL_PACKAGE)) {
                    if (packageName == null) {
                        new GenericBlock("package default;").generate(formatter);
                    } else {
                        new GenericBlock("package " + packageName + ";").generate(formatter);
                    }
                } else if (line.contains(LABEL_REDUCTIONS)) {
                    adjustIndentToLabel(line, LABEL_REDUCTIONS);
                    for (Block block : reductionFuncBlocks) {
                        block.generate(autoIndentFormatter);
                    }
                } else if (line.contains(LABEL_CLASS)) {
                    adjustIndentToLabel(line, LABEL_CLASS);
                    for (Block block : clsBlocks) {
                        block.generate(autoIndentFormatter);
                    }
                } else if (line.contains(LABEL_SWITCH)) {
                    adjustIndentToLabel(line, LABEL_SWITCH);
                    int pos = line.indexOf(LABEL_SWITCH);
                    indent = pos / 4;
                    mainSwitchBlock.generate(formatter);
                } else if (line.contains(LABEL_VARIABLE)) {
                    adjustIndentToLabel(line, LABEL_VARIABLE);
                    int pos = line.indexOf(LABEL_VARIABLE);
                    indent = pos / 4;
                    declrBlock.generate(formatter);
                } else if (line.contains(LABEL_PROGRAM)) {
                    adjustIndentToLabel(line, LABEL_PROGRAM);
                    int pos = line.indexOf(LABEL_PROGRAM);
                    indent = pos / 4;
                    for (Block func : funcBlocks) {
                        func.generate(formatter);
                    }
                    for (String line1 : yaccFile.programs) {
                        writeLine(line1);
                    }
                } else if (line.contains(LABEL_GOTO)) {
                    adjustIndentToLabel(line, LABEL_GOTO);
                    gotoBlock.generate(formatter);
                } else {
                    writeLine(line);
                }
                indent = 0;
            }

            writer.flush();
            writer.close();
            template.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e);
        }
    }

    private void adjustIndentToLabel(String line, String label) {
        int blanks = line.indexOf(label);
        blanks++;
        indent = blanks / 4;
    }

    private interface Block {

        /**
         * 提供代码块生成代码的接口，由各自特定类型的代码块实现之并可以递归调用。
         */
        void generate(CodeFormatter formatter);
    }

    private class SwitchBlock implements Block {

        private class Pair {
            public String label;
            public Block body;

            public Pair(String label, Block body) {
                this.label = label;
                this.body = body;
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof Pair))
                    return false;
                return label.equals(((Pair) obj).label) && body.equals(((Pair) obj).body);
            }
        }

        private String condition;
        private List<Pair> cases = new ArrayList<>();
        private Block defaultBlock = new GenericBlock("error();");
        private boolean hasBreak = true;

        public SwitchBlock(String condition) {
            this.condition = condition;
        }

        /**
         * 针对switch case 中相连的case内部结构相同进行的代码优化。
         *
         * @param
         */
        private void sequentialCaseOptimization() {
            if (cases.isEmpty()) {
                return;
            }
            Pair firstOfSeq = cases.get(0);
            for (int i = 1; i < cases.size() + 1; i++) {
                if (i == cases.size()) {
                    if (cases.get(i - 1).body == null) {
                        cases.get(i - 1).body = firstOfSeq.body;
                        if (!firstOfSeq.equals(cases.get(i - 1)))
                            firstOfSeq.body = null;
                    }
                    return;
                }
                Pair currentPair = cases.get(i);
                if (!currentPair.body.equals(firstOfSeq.body)) {
                    cases.get(i - 1).body = firstOfSeq.body;
                    if (!firstOfSeq.equals(cases.get(i - 1)))
                        firstOfSeq.body = null;
                    firstOfSeq = cases.get(i);
                } else {
                    cases.get(i).body = null;
                }
            }


        }

        public SwitchBlock putCase(String name, Block block) {
            this.cases.add(new Pair(name, block));
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
        public void generate(CodeFormatter formatter) {
            sequentialCaseOptimization();
            formatter.write("switch(" + condition + ") {");
            formatter.doBeforeBlock();
            for (Pair pair : cases) {
                if (pair.body == null) {
                    formatter.write("case " + pair.label + ":");
                } else {
                    formatter.write("case " + pair.label + ":{");
                    formatter.doBeforeBlock();
                    pair.body.generate(formatter);
                    if (hasBreak) {
                        formatter.write("break;}");
                    } else {
                        formatter.write("}");
                    }
                    formatter.doAfterBlock();
                }
            }
            formatter.write("default:{");
            formatter.doBeforeBlock();
            defaultBlock.generate(formatter);
            if (hasBreak) {
                formatter.write("break;}");
            } else {
                formatter.write("}");
            }
            formatter.doAfterBlock();
            formatter.doAfterBlock();
            formatter.write("}");
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SwitchBlock)) {
                return false;
            }
            return condition.equals(((SwitchBlock) obj).condition)
                    && cases.equals(((SwitchBlock) obj).cases)
                    && defaultBlock.equals(((SwitchBlock) obj).defaultBlock)
                    && hasBreak == ((SwitchBlock) obj).hasBreak;
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
        public void generate(CodeFormatter formatter) {
            formatter.write("class " + name + " extends Symbol{");
            formatter.doBeforeBlock();
            for (String attr : attributes.keySet()) {
                formatter.write("public " + attributes.get(attr) + " " + attr + ";");
            }
            formatter.write("");
            formatter.doAfterBlock();
            formatter.write("}");
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
        public void generate(CodeFormatter formatter) {
            formatter.write("if(" + condition + ") {");
            formatter.doBeforeBlock();
            body.generate(formatter);
            formatter.doAfterBlock();
            formatter.write("{");
        }
    }

    private class FuncCallBlock implements Block {
        private String name;
        private List<String> arguements = new ArrayList<>();

        public FuncCallBlock(String name) {
            this.name = name;
        }

        public FuncCallBlock reverseArgs() {
            Collections.reverse(arguements);
            return this;
        }

        public FuncCallBlock putArg(String name) {
            this.arguements.add(name);
            return this;
        }

        @Override
        public void generate(CodeFormatter formatter) {
            String args = StringUtils.join(arguements.toArray(), ", ");
            formatter.write(name + "(" + args + ");");
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FuncCallBlock)) {
                return false;
            }
            return name.equals(((FuncCallBlock) obj).name) && arguements.equals(((FuncCallBlock) obj).arguements);
        }
    }

    private class FuncDefBlock implements Block {
        private String name;
        private String returnType = "void";
        private String exception;
        private Map<String, String> paramaters = new LinkedHashMap<>();
        private Block body = new GenericBlock();

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

        public FuncDefBlock putParamater(String name, String clsName) {
            paramaters.put(name, clsName);
            return this;
        }

        public FuncDefBlock putParamater(String name, Class cls) {
            paramaters.put(name, cls.getSimpleName());
            return this;
        }

        @Override
        public void generate(CodeFormatter formatter) {
            List<String> pairs = new ArrayList<>();
            for (String parmName : paramaters.keySet()) {
                pairs.add(paramaters.get(parmName) + " " + parmName);
            }
            String params = StringUtils.join(pairs.toArray(), ", ");
            if (exception == null) {
                formatter.write("public " + returnType + " " + name + "(" + params + ") {");
            } else {
                formatter.write("public " + returnType + " " + name + "(" + params + ") throws " + exception + " {");
            }

            formatter.doBeforeBlock();
            body.generate(formatter);
            formatter.doAfterBlock();
            formatter.write("}");
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

        public boolean isSimple() {
            return blocks.size() == 0;
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

        @Override
        public void generate(CodeFormatter formatter) {
            if (line != null) {
                formatter.write(line);
            }
            for (Block block : blocks) {
                block.generate(formatter);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof GenericBlock)) {
                return false;
            }
            if (line != null) {
                return line.equals(((GenericBlock) obj).line) && blocks.equals(((GenericBlock) obj).blocks);
            } else if (((GenericBlock) obj).line != null) {
                return false;
            } else {
                return blocks.equals(((GenericBlock) obj).blocks);
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
        public void generate(CodeFormatter formatter) {
            formatter.write("for(int i=0;i<" + number + ";i++) {");
            formatter.doBeforeBlock();
            body.generate(formatter);
            formatter.doAfterBlock();
            formatter.write("}");
        }
    }

    private class AnnotationBlock implements Block {
        private List<String> annotations = new ArrayList<>();

        public AnnotationBlock() {

        }

        public AnnotationBlock(String line) {
            annotations.add(line);
        }

        public AnnotationBlock putAnnotations(String line) {
            annotations.add(line);
            return this;
        }

        @Override
        public void generate(CodeFormatter formatter) {
            for (String line : annotations) {
                formatter.write("//" + line);
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
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void write(String line) {
        try {
            writer.write(line);
        } catch (IOException e) {
            Logger.error(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
//        DokymeYaccFile yaccFile = DokymeYaccFile.read("./testcase/rules_arithmetic.dokycc");
//        LRParsingTable parsingTable = LRParsingTable.build(yaccFile);
//        CodeGenerator generator = new CodeGenerator(yaccFile, parsingTable);
//        generator.generate("./src/main/generated/Parser.java");
//        Symbol symbol=new Symbol("nnn");
//        System.out.println(symbol.getClass().getSimpleName());
//        try {
//            Class.forName("com.seu.dokyme.dokymeyacc.Symbol").newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
