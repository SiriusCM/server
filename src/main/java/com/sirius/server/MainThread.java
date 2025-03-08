package com.sirius.server;

import com.sirius.server.object.SceneObject;
import lombok.Getter;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Getter
public class MainThread extends Thread implements IPulse {

    private final Queue<Consumer<MainThread>> consumerQueue = new LinkedBlockingQueue<>();

    private final Map<Integer, SceneObject> sceneObjectMap = new ConcurrentHashMap<>();

    public MainThread(String name) {
        super(name);
    }

    @Override
    public void pulse() throws Exception {
        while (!consumerQueue.isEmpty()) {
            Consumer<MainThread> consumer = consumerQueue.poll();
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
