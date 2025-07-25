package com.yyk.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProgramAppDocumentLoaderTest {

    @Resource
    private ProgramAppDocumentLoader parogramAppDocumentLoader;

    @Test
    void loadMarkdowns() {
        parogramAppDocumentLoader.loadMarkdowns();
    }
}