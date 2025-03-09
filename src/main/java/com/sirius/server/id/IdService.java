package com.sirius.server.id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdService {
    @Value("${server.id}")
    private int serverId;

    private final AtomicLong roleId = new AtomicLong(1_0000_0000);

    private long lastId = 0;

    public long applyRoleId() {
        return roleId.incrementAndGet();
    }

    public synchronized long applyId() {
        long time = System.currentTimeMillis();
        long id = serverId * 1000_0000_0000_0000L + time;
        if (id == lastId) {
            return applyId();
        }
        lastId = id;
        return id;
    }
}
