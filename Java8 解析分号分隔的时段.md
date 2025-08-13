title: Java8 解析分号分隔的时段并输出为“start:...;end:...”用“和”拼接
goal: |
在 Java 8 环境下实现一个解析器，将如下格式的字符串：
"yyyy-MM-dd HH:mm:ss-HH:mm:ss; yyyy-MM-dd HH:mm:ss-HH:mm:ss; ..."
解析为若干片段，并格式化输出为：
"start:YYYY-MM-DD HH:mm:ss;end:YYYY-MM-DD HH:mm:ss和start:...;end:..."
需支持全角分隔符容错与空格清理；可选支持跨天容错（若 end < start，则 end += 1 天，输出时携带正确日期）。

environment:
language: "Java 8"
build: "Maven"
test: "JUnit 4.13.2"

deliverables:
- path: src/main/java/com/example/util/TimeRangeFormatter.java
  desc: 解析与格式化工具类（纯静态方法）
- path: src/test/java/com/example/util/TimeRangeFormatterTest.java
  desc: JUnit4 单元测试（覆盖正常/异常/容错）
- path: pom-snippet.txt
  desc: JUnit4 依赖与 surefire 插件片段

requirements:
parsing:
- 输入使用分号分隔，允许出现全角分隔符（；、，），需统一为半角分号 ';'
- 允许任意空格；连字符两侧的空格需清理（"08:00:00 - 08:45:00" → "08:00:00-08:45:00"）
- 单个片段格式必须严格匹配：yyyy-MM-dd HH:mm:ss-HH:mm:ss
- 不合规时抛出 IllegalArgumentException，异常消息包含问题片段原文
formatting:
- 对每段输出："start:YYYY-MM-DD HH:mm:ss;end:YYYY-MM-DD HH:mm:ss"
- 所有段之间用 "和" 连接（中文“和”字）
cross_day:
- 可选容错：若解析为 LocalDateTime 后 end < start，认为跨天，end += 1 天
- 输出时 end 应包含正确的年月日
code_style:
- 仅使用 JDK 标准库（java.util.regex、java.time）
- 线程安全、纯函数；不修改入参
- 完整 Javadoc 与关键步骤注释

api_design:
class: com.example.util.TimeRangeFormatter
methods:
- name: toStartEndJoined
signature: "public static String toStartEndJoined(String raw)"
javadoc: |
将原始字符串解析为片段，并按 "start:...;end:..." 格式用“和”拼接返回。
支持全角分隔符与空格清理；严格校验片段格式；跨天时 end 自动 +1 天。
- name: splitSegments
signature: "public static java.util.List<String> splitSegments(String raw)"
javadoc: "归一化（全角→半角；清理空格；规范连字符），按分号切段并去空串。"
regex: "^(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}:\\d{2})-(\\d{2}:\\d{2}:\\d{2})$"
datetime_pattern: "yyyy-MM-dd HH:mm:ss"

tests:
framework: JUnit4
class: com.example.util.TimeRangeFormatterTest
cases:
- name: basic_two_segments
input: "2025-05-21 16:00:00-16:45:00; 2025-05-22 16:00:00-16:45:00"
expect: "start:2025-05-21 16:00:00;end:2025-05-21 16:45:00和start:2025-05-22 16:00:00;end:2025-05-22 16:45:00"
- name: fullwidth_and_spaces
input: "2025-05-21 16:00:00 - 16:45:00； 2025-05-22 16:00:00-16:45:00 、 "
expect: "start:2025-05-21 16:00:00;end:2025-05-21 16:45:00和start:2025-05-22 16:00:00;end:2025-05-22 16:45:00"
- name: invalid_should_throw
input: "2025/05/21 16:00:00-16:45:00; 2025-05-22 16:00:00-16:45:00"
expect_exception: "IllegalArgumentException"
- name: cross_day_optional
input: "2025-05-21 23:30:00-00:15:00"
expect: "start:2025-05-21 23:30:00;end:2025-05-22 00:15:00"

golden_case:
title: 用户提供的真实数据
input: "2025-06-02 08:00:00-08:45:00; 2025-06-03 08:00:00-08:45:00; 2025-06-04 08:00:00-08:45:00; 2025-06-05 08:00:00-08:45:00; 2025-06-06 08:00:00-08:45:00; 2025-06-02 08:55:00-09:40:00; 2025-06-03 08:55:00-09:40:00"
expect: "start:2025-06-02 08:00:00;end:2025-06-02 08:45:00和start:2025-06-03 08:00:00;end:2025-06-03 08:45:00和start:2025-06-04 08:00:00;end:2025-06-04 08:45:00和start:2025-06-05 08:00:00;end:2025-06-05 08:45:00和start:2025-06-06 08:00:00;end:2025-06-06 08:45:00和start:2025-06-02 08:55:00;end:2025-06-02 09:40:00和start:2025-06-03 08:55:00;end:2025-06-03 09:40:00"

implementation_notes:
- 使用 Pattern/Matcher 基于给定正则进行严格校验
- 使用 java.time.LocalDateTime 与 DateTimeFormatter("yyyy-MM-dd HH:mm:ss")
- splitSegments 步骤：
    1) 替换全角分隔符 '；'、'，'、'、' → ';'
    2) 规范分号空格："\\s*;\\s*" → ";"
    3) 规范连字符空格："\\s*-\\s*" → "-"
    4) trim 后以 ';' 分割、去空串
- toStartEndJoined 对每段：
    1) 正则提取 date、startTime、endTime
    2) 组装为 LocalDateTime start/end，若 end.isBefore(start) 则 end = end.plusDays(1)
    3) 输出 "start:...;end:..."，最后用 "和" join

pom_snippet: |
  <!-- 复制到 pom.xml 中（或合并） -->
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.13.2</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.2.5</version>
      </plugin>
    </plugins>
  </build>
