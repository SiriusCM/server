package com.sirius.server.thread;

import com.sirius.server.object.SceneObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Data
public class LogicThread extends Thread implements IPulse {

    private final Queue<Consumer<LogicThread>> consumerQueue = new LinkedBlockingQueue<>();

    private final Map<Long, SceneObject> sceneObjectMap = new HashMap<>();

    public LogicThread(String name) {
        super(name);
    }

    @Override
    public void pulse() throws Exception {
        while (!consumerQueue.isEmpty()) {
            Consumer<LogicThread> consumer = consumerQueue.poll();
            consumer.accept(this);
        }
        for (SceneObject sceneObject : sceneObjectMap.values()) {
            sceneObject.pulse();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
                pulse();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
