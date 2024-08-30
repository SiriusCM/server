package org.sirius.server;

import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.sirius.server.scene.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Aspect
class ServerApplicationTests {
    @Autowired
    private SceneService sceneService;

    @Test
    void contextLoads() {
        sceneService.fight(1);
    }
}
