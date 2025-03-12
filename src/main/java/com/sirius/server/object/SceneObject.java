package com.sirius.server.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class SceneObject extends WorldObject {

    private final Queue<Consumer<SceneObject>> consumerQueue = new LinkedBlockingQueue<>();

    private final Map<Long, WorldObject> worldObjectMap = new HashMap<>();

    @Override
    public void pulse() {
        while (!consumerQueue.isEmpty()) {
            try {
                Consumer<SceneObject> consumer = consumerQueue.poll();
                consumer.accept(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (WorldObject worldObject : worldObjectMap.values()) {
            try {
                worldObject.pulse();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
