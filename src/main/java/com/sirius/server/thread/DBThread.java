package com.sirius.server.thread;

import com.sirius.server.object.RoleObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

public class DBThread extends Thread implements IPulse {

    private final Map<Long, Queue<Consumer<DBThread>>> longQueueMap = new HashMap<>();

    public DBThread(String name) {
        super(name);
    }

    @Override
    public synchronized void pulse() throws Exception {
        for (Queue<Consumer<DBThread>> queue : longQueueMap.values()) {
            while (!queue.isEmpty()) {
                queue.poll().accept(this);
            }
        }
    }

    public synchronized void addDBQueue(RoleObject roleObject) {
        longQueueMap.put(roleObject.getId(), roleObject.getDbQueue());
    }

    public synchronized void removeDBQueue(RoleObject roleObject) {
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
