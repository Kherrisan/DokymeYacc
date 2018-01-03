# DokymeYacc——语法分析器&语义分析器自动生成程序

## .dokycc文件结构

### 终结符声明段

每行一个终结符，必须与词法分析输出的token文件中的每个token的类型相同（也就是说你必须要用dokymelex生成的词法分析器进行词法分析，否则的话格式不正确这个是用不了的。**此乃捆绑销售**）。

token示例

```
<int,INT,1>
```

终结符声明段示例：

```
ID
MULTIPLY
ASSIGN
```

### 开始符声明段

声明一系列产生式的开始符。

```
START
```

### 全局变量声明段

以java成员变量的语法声明在语法分析过程中全局可见的变量，用于辅助语法分析或制导翻译，比如一个新的输出文件句柄，或者一个计数器变量。

```
private int counter;
```

### 语法规则段

语法制导翻译规则在{ }内，使用java函数体的语法进行编写，允许跨行写。如果某个非终结符有多个产生式并列，则要写多个推导规则。不允许使用形如标准yacc文件中的 | 并列多个推导规则。
翻译规则采用类似Java语言的语法进行编写。产生式左侧的非终结符用`left`来引用，右侧的非终结符用`right[i]`来引用。

**SDT规则约定：**
1. 每个符号属性的类型由用户决定，但属性的名称必须包含其类型信息，具体为该属性的前缀反映自己的类型。
如：i代表int，b代表boolean，str代表string，ch代表char。如果没有出现上述前缀，按照string处理。
2. 产生式左侧的符号由left引用，右侧的符号由相应符号引用。

```
expression:assignment_expression 
{
    left.ivalue = assignment_expression.ivalue;
}
expression:expression ',' assignment_expression
{
    counter++;
}
```

### 程序段

以定义java成员函数的语法编写，在这里定义的函数可以在SDT规则中调用，可以实现带参数的方法。

默认提供一个main方法。

```
public static void main(String[] args) {
      Parser parser = new Parser();
      parser.parseCmdArgs(args);
      parser.run();
}
```

### 一个完整的.dokycc文件结构

假设有如下CFG，没有语法制导翻译的要求：
     
> S->L=R    
> S->R  
> L->*R     
> L->i          
> R->L


则.dokycc文件如下：

```
ID
MULTIPLY
ASSIGN
%%
START
%%

%%
START:LEFT ASSIGN RIGHT
START:RIGHT
LEFT:MULTIPLY RIGHT
LEFT:ID
RIGHT:LEFT
%%
public static void main(String[] args) {
      Parser parser = new Parser();
      parser.parseCmdArgs(args);
      parser.run();
}
```
