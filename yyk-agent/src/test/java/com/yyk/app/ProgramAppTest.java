package com.yyk.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class ProgramAppTest {


    @Resource
    private ProgramApp programApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员亚克";
        String answer = programApp.doChat(message, chatId);
        // 第二轮
        message = "我想学习Java";
        answer = programApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我的名字叫什么来着？刚跟你说过，帮我回忆一下";
        answer = programApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是程序员亚克，我想学习Java，但我不知道该怎么做";
        ProgramApp.ProgramReport programReport = programApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(programReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我现在是一个Java的初学者，我不知道应该如何学习，怎么办？";
        String answer = programApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("Java的学习路线");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近学习Java遇到了问题，看看博主（www.isyyk.top）是怎么解决的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张Java相关的图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的编程档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘Java学习计划’PDF，包含学习路线、学习资源、学习方法、学习工具");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = programApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
//         测试地图 MCP
//        String message = "我居住在上海静安区，请帮我找到 5 公里内的Java公司";
//        String answer = programApp.doChatWithMcp(message, chatId);
//        Assertions.assertNotNull(answer);
        // 测试图片搜索 MCP
        String message = "帮我搜索一些图片用来学习Java";
        String answer =  programApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
