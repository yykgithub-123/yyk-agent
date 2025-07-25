package com.yyk.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "亚克个人网站.pdf";
        String content = "亚克个人网站 http://www.isyyk.top";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}