# Java基础代码示例

本文件提供了一些Java编程的基础代码片段，帮助初学者快速理解和上手。

---

### 1. "Hello, World!" 程序

- **描述:** 这是学习任何编程语言的第一个传统程序，用于验证开发环境是否配置正确。
- **代码:**
  ```java
  // 定义一个名为 HelloWorld 的公共类
  public class HelloWorld {
      // main 方法是Java程序的入口点
      public static void main(String[] args) {
          // 使用 System.out.println 在控制台打印文本
          System.out.println("Hello, World!");
      }
  }
  ```

---

### 2. 变量和基本数据类型

- **描述:** 演示如何声明变量并使用Java的几种基本数据类型。
- **代码:**
  ```java
  public class VariablesExample {
      public static void main(String[] args) {
          // 声明并初始化不同类型的变量
          String name = "小明";         // 字符串，用于存储文本
          int age = 10;                // 整数，用于存储整数值
          double score = 95.5;         // 双精度浮点数，用于存储小数值
          char initial = 'X';          // 字符，用于存储单个字符
          boolean isStudent = true;    // 布尔值，用于存储 true 或 false

          // 打印变量的值
          System.out.println("姓名: " + name);
          System.out.println("年龄: " + age);
          System.out.println("分数: " + score);
          System.out.println("是学生吗? " + isStudent);
      }
  }
  ```

---

### 3. 条件语句 (if-else)

- **描述:** 根据不同的条件执行不同的代码块。
- **代码:**
  ```java
  public class ConditionalStatement {
      public static void main(String[] args) {
          int temperature = 25;

          if (temperature > 30) {
              System.out.println("天气炎热，适合游泳。");
          } else if (temperature > 20) {
              System.out.println("天气温和，适合散步。");
          } else {
              System.out.println("天气寒冷，请注意保暖。");
          }
      }
  }
  ```

---

### 4. 循环 (for 和 while)

- **描述:** 重复执行一段代码，直到满足特定条件为止。
- **for 循环代码:**
  ```java
  public class ForLoopExample {
      public static void main(String[] args) {
          // for循环，打印从1到5的数字
          System.out.println("For 循环示例:");
          for (int i = 1; i <= 5; i++) {
              System.out.println("Count: " + i);
          }
      }
  }
  ```
- **while 循环代码:**
  ```java
  public class WhileLoopExample {
      public static void main(String[] args) {
          System.out.println("\nWhile 循环示例:");
          int i = 1;
          while (i <= 5) {
              System.out.println("Count: " + i);
              i++; // 不要忘记更新循环变量，否则会导致无限循环
          }
      }
  }
  ```

---

**标签:** `Java`, `代码示例`, `基础语法`, `HelloWorld`, `变量`, `数据类型`, `if-else`, `for循环`, `while循环` 