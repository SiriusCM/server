package com.sirius.server.id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IdService {
    @Value("${server.id}")
    private int serverId;

    private long lastId = 0;

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
