package generated;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

abstract class Symbol {
    public String name;

    public String getName() {
        return this.getClass().getSimpleName();
    }
}

class DOLLARR extends Symbol{
}
class ADD extends Symbol{
}
class SUB extends Symbol{
}
class MUL extends Symbol{
}
class SEMI extends Symbol{
}
class CONSTANT extends Symbol{
}
class ASSIGNMENT_EXPRESSION extends Symbol{
}
class UNARY_EXPRESSION extends Symbol{
}
class DIV extends Symbol{
}
class ADDITIVE_EXPRESSION extends Symbol{
}
class STATEMENT extends Symbol{
}
class MULTPLICATIVE_EXPRESSION extends Symbol{
}
class ID extends Symbol{
}
class ASN extends Symbol{
}
class ASSIGNMENT_OPERATOR extends Symbol{
}
class STATEMENT_LIST extends Symbol{
}

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
    private Symbol token;
    private List<Symbol> reduce;
    private int newState;


    public Parser() {
        stateStack = new Stack<>();
        symbolStack = new Stack<>();
        reduce = new ArrayList<>();
    }

    private int gott(int state, Symbol symbol) {
switch(state) {
	case 0:
		switch(symbol.getName()) {
			case "STATEMENT":
				return 1;
			case "ADDITIVE_EXPRESSION":
				return 2;
			case "ASSIGNMENT_EXPRESSION":
				return 4;
			case "MULTPLICATIVE_EXPRESSION":
				return 5;
			case "UNARY_EXPRESSION":
				return 7;
			case "STATEMENT_LIST":
				return 8;
			default:
				return error();
		}
	case 1:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 2:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 3:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 4:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 5:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 6:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 7:
		switch(symbol.getName()) {
			case "ASSIGNMENT_OPERATOR":
				return 14;
			default:
				return error();
		}
	case 8:
		switch(symbol.getName()) {
			case "STATEMENT":
				return 16;
			case "ADDITIVE_EXPRESSION":
				return 2;
			case "ASSIGNMENT_EXPRESSION":
				return 4;
			case "MULTPLICATIVE_EXPRESSION":
				return 18;
			case "UNARY_EXPRESSION":
				return 20;
			default:
				return error();
		}
	case 9:
		switch(symbol.getName()) {
			case "MULTPLICATIVE_EXPRESSION":
				return 22;
			case "UNARY_EXPRESSION":
				return 24;
			default:
				return error();
		}
	case 10:
		switch(symbol.getName()) {
			case "MULTPLICATIVE_EXPRESSION":
				return 25;
			case "UNARY_EXPRESSION":
				return 24;
			default:
				return error();
		}
	case 11:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 12:
		switch(symbol.getName()) {
			case "UNARY_EXPRESSION":
				return 28;
			default:
				return error();
		}
	case 13:
		switch(symbol.getName()) {
			case "UNARY_EXPRESSION":
				return 29;
			default:
				return error();
		}
	case 14:
		switch(symbol.getName()) {
			case "ADDITIVE_EXPRESSION":
				return 2;
			case "ASSIGNMENT_EXPRESSION":
				return 30;
			case "MULTPLICATIVE_EXPRESSION":
				return 18;
			case "UNARY_EXPRESSION":
				return 20;
			default:
				return error();
		}
	case 15:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 16:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 17:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 18:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 19:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 20:
		switch(symbol.getName()) {
			case "ASSIGNMENT_OPERATOR":
				return 14;
			default:
				return error();
		}
	case 21:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 22:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 23:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 24:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 25:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 26:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 27:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 28:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 29:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 30:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 31:
		switch(symbol.getName()) {
			case "UNARY_EXPRESSION":
				return 35;
			default:
				return error();
		}
	case 32:
		switch(symbol.getName()) {
			case "UNARY_EXPRESSION":
				return 36;
			default:
				return error();
		}
	case 33:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 34:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 35:
		switch(symbol.getName()) {
			default:
				return error();
		}
	case 36:
		switch(symbol.getName()) {
			default:
				return error();
		}
	default:
		return error();
}
    }

    private void run() {
        try {
            stateStack.push(0);
            token = readToken();
            while (true) {
                if (token == null) {
                    token = new DOLLARR();
                }
                if (symbolStack.size() != 0) {
                    debug("The symbol on the top is " + symbolStack.peek().getName() + ".");
                }
				switch(stateStack.peek()) {
					case 0:
						state_0();
						break;
					case 1:
						state_1();
						break;
					case 2:
						state_2();
						break;
					case 3:
						state_3();
						break;
					case 4:
						state_4();
						break;
					case 5:
						state_5();
						break;
					case 6:
						state_6();
						break;
					case 7:
						state_7();
						break;
					case 8:
						state_8();
						break;
					case 9:
						state_9();
						break;
					case 10:
						state_10();
						break;
					case 11:
						state_11();
						break;
					case 12:
						state_12();
						break;
					case 13:
						state_13();
						break;
					case 14:
						state_14();
						break;
					case 15:
						state_15();
						break;
					case 16:
						state_16();
						break;
					case 17:
						state_17();
						break;
					case 18:
						state_18();
						break;
					case 19:
						state_19();
						break;
					case 20:
						state_20();
						break;
					case 21:
						state_21();
						break;
					case 22:
						state_22();
						break;
					case 23:
						state_23();
						break;
					case 24:
						state_24();
						break;
					case 25:
						state_25();
						break;
					case 26:
						state_26();
						break;
					case 27:
						state_27();
						break;
					case 28:
						state_28();
						break;
					case 29:
						state_29();
						break;
					case 30:
						state_30();
						break;
					case 31:
						state_31();
						break;
					case 32:
						state_32();
						break;
					case 33:
						state_33();
						break;
					case 34:
						state_34();
						break;
					case 35:
						state_35();
						break;
					case 36:
						state_36();
						break;
					default:
						error();
						break;
				}
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void translate(int production) {
        debug("Reduce using production id : " + production + ".");
Symbol left = symbolStack.peek();
        reduce.clear();
        return;
    }

    private void output(Symbol left) throws IOException {
        Collections.reverse(reduce);
        String out = left.getName() + "->";
        for (Symbol symbol : reduce) {
            out += symbol.getName() + " ";
        }
        System.out.println(out);
        if (outputWriter != null) {
            outputWriter.write(out + "\n");
        }
        return;
    }

    private void end() {
        debug("Finish parsing.");
        try {
            inputReader.close();
            if (outputWriter != null) {
                outputWriter.close();
            }
        } catch (Exception e) {

        }
        System.exit(0);
    }

    private int error() {
        System.out.println("Error in parsing.");
        System.exit(1);
        return -1;
    }

    private Symbol readToken() throws Exception {
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
        return (Symbol) Class.forName(this.getClass().getPackage().getName() + "." + toks[1]).newInstance();
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

	public void state_0() throws Exception {
		switch(token.getName()) {
			case "CONSTANT":
				symbolStack.push(token);
				stateStack.push(3);
				token = readToken();
				break;
			case "ID":
				symbolStack.push(token);
				stateStack.push(6);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_1() throws Exception {
		switch(token.getName()) {
			case "DOLLARR":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new STATEMENT_LIST());
				newState = gott(stateStack.peek(),new STATEMENT_LIST());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(13);
				break;
			case "CONSTANT":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new STATEMENT_LIST());
				newState = gott(stateStack.peek(),new STATEMENT_LIST());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(13);
				break;
			case "ID":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new STATEMENT_LIST());
				newState = gott(stateStack.peek(),new STATEMENT_LIST());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(13);
				break;
			default:
				error();
				break;
		}
	}
	public void state_2() throws Exception {
		switch(token.getName()) {
			case "ADD":
				symbolStack.push(token);
				stateStack.push(9);
				token = readToken();
				break;
			case "SUB":
				symbolStack.push(token);
				stateStack.push(10);
				token = readToken();
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ASSIGNMENT_EXPRESSION());
				newState = gott(stateStack.peek(),new ASSIGNMENT_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(9);
				break;
			default:
				error();
				break;
		}
	}
	public void state_3() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "ADD":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "SUB":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "MUL":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "ASN":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			default:
				error();
				break;
		}
	}
	public void state_4() throws Exception {
		switch(token.getName()) {
			case "SEMI":
				symbolStack.push(token);
				stateStack.push(11);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_5() throws Exception {
		switch(token.getName()) {
			case "DIV":
				symbolStack.push(token);
				stateStack.push(12);
				token = readToken();
				break;
			case "ADD":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(6);
				break;
			case "SUB":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(6);
				break;
			case "MUL":
				symbolStack.push(token);
				stateStack.push(13);
				token = readToken();
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(6);
				break;
			default:
				error();
				break;
		}
	}
	public void state_6() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "ADD":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "SUB":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "MUL":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "ASN":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			default:
				error();
				break;
		}
	}
	public void state_7() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "ADD":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "SUB":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "MUL":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "ASN":
				symbolStack.push(token);
				stateStack.push(15);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_8() throws Exception {
		switch(token.getName()) {
			case "DOLLARR":
				end();
				break;
			case "CONSTANT":
				symbolStack.push(token);
				stateStack.push(17);
				token = readToken();
				break;
			case "ID":
				symbolStack.push(token);
				stateStack.push(19);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_9() throws Exception {
		switch(token.getName()) {
			case "CONSTANT":
				symbolStack.push(token);
				stateStack.push(21);
				token = readToken();
				break;
			case "ID":
				symbolStack.push(token);
				stateStack.push(23);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_10() throws Exception {
		switch(token.getName()) {
			case "CONSTANT":
				symbolStack.push(token);
				stateStack.push(21);
				token = readToken();
				break;
			case "ID":
				symbolStack.push(token);
				stateStack.push(23);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_11() throws Exception {
		switch(token.getName()) {
			case "DOLLARR":
				for(int i=0;i<2;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new STATEMENT());
				newState = gott(stateStack.peek(),new STATEMENT());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(12);
				break;
			case "CONSTANT":
				for(int i=0;i<2;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new STATEMENT());
				newState = gott(stateStack.peek(),new STATEMENT());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(12);
				break;
			case "ID":
				for(int i=0;i<2;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new STATEMENT());
				newState = gott(stateStack.peek(),new STATEMENT());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(12);
				break;
			default:
				error();
				break;
		}
	}
	public void state_12() throws Exception {
		switch(token.getName()) {
			case "CONSTANT":
				symbolStack.push(token);
				stateStack.push(26);
				token = readToken();
				break;
			case "ID":
				symbolStack.push(token);
				stateStack.push(27);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_13() throws Exception {
		switch(token.getName()) {
			case "CONSTANT":
				symbolStack.push(token);
				stateStack.push(26);
				token = readToken();
				break;
			case "ID":
				symbolStack.push(token);
				stateStack.push(27);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_14() throws Exception {
		switch(token.getName()) {
			case "CONSTANT":
				symbolStack.push(token);
				stateStack.push(17);
				token = readToken();
				break;
			case "ID":
				symbolStack.push(token);
				stateStack.push(19);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_15() throws Exception {
		switch(token.getName()) {
			case "CONSTANT":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ASSIGNMENT_OPERATOR());
				newState = gott(stateStack.peek(),new ASSIGNMENT_OPERATOR());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(11);
				break;
			case "ID":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ASSIGNMENT_OPERATOR());
				newState = gott(stateStack.peek(),new ASSIGNMENT_OPERATOR());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(11);
				break;
			default:
				error();
				break;
		}
	}
	public void state_16() throws Exception {
		switch(token.getName()) {
			case "DOLLARR":
				for(int i=0;i<2;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new STATEMENT_LIST());
				newState = gott(stateStack.peek(),new STATEMENT_LIST());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(14);
				break;
			case "CONSTANT":
				for(int i=0;i<2;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new STATEMENT_LIST());
				newState = gott(stateStack.peek(),new STATEMENT_LIST());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(14);
				break;
			case "ID":
				for(int i=0;i<2;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new STATEMENT_LIST());
				newState = gott(stateStack.peek(),new STATEMENT_LIST());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(14);
				break;
			default:
				error();
				break;
		}
	}
	public void state_17() throws Exception {
		switch(token.getName()) {
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "ASN":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			default:
				error();
				break;
		}
	}
	public void state_18() throws Exception {
		switch(token.getName()) {
			case "DIV":
				symbolStack.push(token);
				stateStack.push(31);
				token = readToken();
				break;
			case "ADD":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(6);
				break;
			case "SUB":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(6);
				break;
			case "MUL":
				symbolStack.push(token);
				stateStack.push(32);
				token = readToken();
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(6);
				break;
			default:
				error();
				break;
		}
	}
	public void state_19() throws Exception {
		switch(token.getName()) {
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "ASN":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			default:
				error();
				break;
		}
	}
	public void state_20() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "MUL":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "ASN":
				symbolStack.push(token);
				stateStack.push(15);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_21() throws Exception {
		switch(token.getName()) {
			case "ADD":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "SUB":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			default:
				error();
				break;
		}
	}
	public void state_22() throws Exception {
		switch(token.getName()) {
			case "DIV":
				symbolStack.push(token);
				stateStack.push(12);
				token = readToken();
				break;
			case "ADD":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(7);
				break;
			case "SUB":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(7);
				break;
			case "MUL":
				symbolStack.push(token);
				stateStack.push(13);
				token = readToken();
				break;
			case "SEMI":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(7);
				break;
			default:
				error();
				break;
		}
	}
	public void state_23() throws Exception {
		switch(token.getName()) {
			case "ADD":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "SUB":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			default:
				error();
				break;
		}
	}
	public void state_24() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "ADD":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "SUB":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "MUL":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(3);
				break;
			default:
				error();
				break;
		}
	}
	public void state_25() throws Exception {
		switch(token.getName()) {
			case "DIV":
				symbolStack.push(token);
				stateStack.push(12);
				token = readToken();
				break;
			case "ADD":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(8);
				break;
			case "SUB":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(8);
				break;
			case "MUL":
				symbolStack.push(token);
				stateStack.push(13);
				token = readToken();
				break;
			case "SEMI":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ADDITIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new ADDITIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(8);
				break;
			default:
				error();
				break;
		}
	}
	public void state_26() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "ADD":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "SUB":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "MUL":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			default:
				error();
				break;
		}
	}
	public void state_27() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "ADD":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "SUB":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "MUL":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			default:
				error();
				break;
		}
	}
	public void state_28() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(5);
				break;
			case "ADD":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(5);
				break;
			case "SUB":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(5);
				break;
			case "MUL":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(5);
				break;
			case "SEMI":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(5);
				break;
			default:
				error();
				break;
		}
	}
	public void state_29() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(4);
				break;
			case "ADD":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(4);
				break;
			case "SUB":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(4);
				break;
			case "MUL":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(4);
				break;
			case "SEMI":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(4);
				break;
			default:
				error();
				break;
		}
	}
	public void state_30() throws Exception {
		switch(token.getName()) {
			case "SEMI":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new ASSIGNMENT_EXPRESSION());
				newState = gott(stateStack.peek(),new ASSIGNMENT_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(10);
				break;
			default:
				error();
				break;
		}
	}
	public void state_31() throws Exception {
		switch(token.getName()) {
			case "CONSTANT":
				symbolStack.push(token);
				stateStack.push(33);
				token = readToken();
				break;
			case "ID":
				symbolStack.push(token);
				stateStack.push(34);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_32() throws Exception {
		switch(token.getName()) {
			case "CONSTANT":
				symbolStack.push(token);
				stateStack.push(33);
				token = readToken();
				break;
			case "ID":
				symbolStack.push(token);
				stateStack.push(34);
				token = readToken();
				break;
			default:
				error();
				break;
		}
	}
	public void state_33() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "MUL":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(2);
				break;
			default:
				error();
				break;
		}
	}
	public void state_34() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "MUL":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			case "SEMI":
				for(int i=0;i<1;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new UNARY_EXPRESSION());
				newState = gott(stateStack.peek(),new UNARY_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(1);
				break;
			default:
				error();
				break;
		}
	}
	public void state_35() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(5);
				break;
			case "MUL":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(5);
				break;
			case "SEMI":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(5);
				break;
			default:
				error();
				break;
		}
	}
	public void state_36() throws Exception {
		switch(token.getName()) {
			case "DIV":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(4);
				break;
			case "MUL":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(4);
				break;
			case "SEMI":
				for(int i=0;i<3;i++) {
					reduce.add(symbolStack.pop());
					stateStack.pop();
				}
				symbolStack.push(new MULTPLICATIVE_EXPRESSION());
				newState = gott(stateStack.peek(),new MULTPLICATIVE_EXPRESSION());
				stateStack.push(newState);
				output(symbolStack.peek());
				translate(4);
				break;
			default:
				error();
				break;
		}
	}
	public static void main(String[] args) {
	      Parser parser = new Parser();
	      parser.parseCmdArgs(args);
	      parser.run();
	}

//    public static void main(String[] args) {
//        Parser parser = new Parser();
//        parser.parseCmdArgs(args);
//        parser.run();
//    }
}
