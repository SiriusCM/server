package com.sirius.server.thread;

import com.sirius.server.object.RoleObject;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DBThread extends Thread implements IPulse {

    private final Map<Long, Queue<Consumer<DBThread>>> longQueueMap = new ConcurrentHashMap<>();

    public DBThread(String name) {
        super(name);
    }

    @Override
    public void pulse() throws Exception {
        for (Queue<Consumer<DBThread>> queue : longQueueMap.values()) {
            while (!queue.isEmpty()) {
                queue.poll().accept(this);
            }
        }
    }

    public void addDBQueue(RoleObject roleObject) {
        longQueueMap.put(roleObject.getId(), roleObject.getDbQueue());
    }

    public void removeDBQueue(RoleObject roleObject) {
        longQueueMap.remove(roleObject.getId());
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
