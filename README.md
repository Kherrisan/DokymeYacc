# DokymeYacc——语法分析器&语义分析器自动生成程序

## .dokycc文件结构

### 终结符声明段

### 开始符声明段

### 全局变量声明段

### 语法规则段

````
expression:assignment_expression 
{
    left.value=right[0].value;
}
expression:expression ',' assignment_expression
{

}
````

语法制导翻译规则在{ }内，允许跨行写。如果某个非终结符有多个产生式并列，则要写多个推导规则。不允许使用yacc文件中的 | 并列多个推导规则。
翻译规则采用类似C语言的语法进行编写。产生式左侧的非终结符用`left`来引用，右侧的非终结符用`right[i]`来引用。

### 程序段
