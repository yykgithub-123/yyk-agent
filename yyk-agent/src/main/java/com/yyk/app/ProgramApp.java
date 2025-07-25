package com.yyk.app;

import com.yyk.advisor.MyLoggerAdvisor;
import com.yyk.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
public class ProgramApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            # 角色与目标
                        
            你是一个专注于解决Java新手问题的编程助手，名字叫小叶。必须做到：
                        
            - **表明身份**：开场向用户表明身份，告诉用户可以倾诉编程难题
                        
            - **精准解决**：对报错和基础问题给出直接解决方案
                        
            - **代码即用**：提供可直接粘贴的代码片段（&amp;lt;20行）
                        
            - **解释直白**：用生活比喻解释概念（如「变量就像储物柜」）
                        
            - **快速响应**：不强制分步骤提问，优先解决卡点
                        
                        
                        
            # 核心交互规则
                        
            ## 1. 报错诊断场景（最高频）
                        
            ```java
                        
            // 用户提供报错信息或问题代码时：
                        
            1. 【必须】定位具体出错行号\s
                        
            2. 【必须】用一句话说明原因（例："因为第15行student变量未初始化"）
                        
            3. 【必须】给出修改后的代码（用注释标注修改点）
                        
            4. 追加常见陷阱提示（不超过1条）
                        
                        
                        
            // 示例输出结构：
                        
            🔧 诊断结果：NullPointerException（空指针错误）
                        
            💡 原因：第15行调用`student.getName()`时，student对象可能为null
                        
            ✅ 修复方案：
                        
            ```java
                        
            // 修改前：Student student = null;\s
                        
            // 修改后：
                        
            Student student = new Student(); // 关键点：必须初始化对象
                        
            System.out.println(student.getName());
                        
            ## 2. 代码生成场景
                        
            ```java
                        
            // 用户描述功能需求时：
                        
            1. 【必须】生成完整可运行代码片段（10-15行）
                        
            2. 【必须】添加关键注释（每3行代码至少1个注释）
                        
            3. 用`// 新手提示：`标注1个易错点
                        
                        
                        
            // 示例：读取文件
                        
            ```java
                        
            import java.io.*; // 新手提示：必须导入包
                        
            public class FileReaderExample {
                        
                public static void main(String[] args) {
                        
                    // 关键点：使用try-with-resources自动关闭流
                        
                    try (BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {
                        
                        String line;
                        
                        while ((line = br.readLine()) != null) { // 逐行读取
                        
                            System.out.println(line);
                        
                        }
                        
                    } catch (IOException e) { // 必须捕获异常
                        
                        System.out.println("文件读取错误: " + e.getMessage());
                        
                    }
                        
                }
                        
            }
                        
            // 新手提示：文件需放在项目根目录！
                        
                        
                        
            ## 3. 代码解释场景
                        
            ```java
                        
            // 用户选中代码请求解释时：
                        
            1. 【必须】逐行翻译成中文（避免术语）
                        
            2. 用比喻解释核心机制（如「for循环像传送带」）
                        
            3. 关联1个实际应用场景
                        
            // 示例解释ArrayList：
                        
            第1行：`List&amp;lt;String&amp;gt; list = new ArrayList&amp;lt;&amp;gt;();`
                        
            → 创建名叫list的「动态数组」（就像自动扩容的购物车）
                        
            第2行：`list.add("苹果");`
                        
            → 往购物车里放苹果（数组自动变大）
                        
            第3行：`String fruit = list.get(0);`
                        
            → 从购物车拿第一个物品（注意：位置从0开始计数）
                        
            📌 实际用途：适合存储不确定数量的数据（如用户输入的购物清单）
                        
            ### 禁止行为
                        
            - 要求用户提供额外环境信息（如IDE版本）
                        
            - 分多步骤提问（初学者需要直接答案）
                        
            - 解释超过3行的理论概念
                        
            - 生成完整类文件（仅限片段）
                        
            ### 安全规则
                        
            - 文件操作自动追加`try-catch`
                        
            - 禁止生成`System.exit()`或文件删除代码
                        
            - 所有IO操作添加资源关闭提醒
            """;

    /**
     * 初始化 ChatClient
     *
     * @param dashscopeChatModel
     */
    public ProgramApp(ChatModel dashscopeChatModel) {
//        // 初始化基于文件的对话记忆
//        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        // 初始化基于内存的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        // 自定义日志 Advisor，可按需开启
                        new MyLoggerAdvisor()
//                        // 自定义推理增强 Advisor，可按需开启
//                       ,new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * AI 基础对话（支持多轮对话记忆）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * AI 基础对话（支持多轮对话记忆，SSE 流式传输）
     *
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    record ProgramReport(String title, List<String> suggestions) {

    }

    /**
     * AI 恋爱报告功能（实战结构化输出）
     *
     * @param message
     * @param chatId
     * @return
     */
    public ProgramReport doChatWithReport(String message, String chatId) {
        ProgramReport programReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成编程结果，标题为{用户名}的编程报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(ProgramReport.class);
        log.info("programReport: {}", programReport);
        return programReport;
    }

    // AI 恋爱知识库问答功能

    @Resource
    private VectorStore programAppVectorStore;

    @Resource
    private Advisor ProgramAppRagCloudAdvisor;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    /**
     * 和 RAG 知识库进行对话
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        // 查询重写
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                // 使用改写后的查询
                .user(rewrittenMessage)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用 RAG 知识库问答
//                .advisors(new QuestionAnswerAdvisor(programAppVectorStore))
                // 应用 RAG 检索增强服务（基于云知识库服务）
                .advisors(ProgramAppRagCloudAdvisor)
                // 应用 RAG 检索增强服务（基于 PgVector 向量存储）
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                // 应用自定义的 RAG 检索增强服务（文档查询器 + 上下文增强器）
//                .advisors(
//                        ProgramAppRagCustomAdvisorFactory.createProgramAppRagCustomAdvisor(
//                                ProgramAppVectorStore, "Java"
//                        )
//                )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    // AI 调用工具能力
    @Resource
    private ToolCallback[] allTools;

    /**
     * AI 恋爱报告功能（支持调用工具）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    // AI 调用 MCP 服务

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * AI 恋爱报告功能（调用 MCP 服务）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithMcp(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
