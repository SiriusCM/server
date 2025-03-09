package com.sirius.server.global;

import com.sirius.server.object.RoleObject;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
@Service
public class GlobalService {

    private Map<Long, RoleObject> roleObjectMap = new ConcurrentHashMap<>();

    private Queue<RoleObject> loginQueue = new ConcurrentLinkedQueue<>();

    @Scheduled(fixedRate = 3000)
    public void schedule() {
        int roleNum = roleObjectMap.size();
        int subNum = 4000 - roleNum;
        if (subNum > 0) {
            for (int i = 0; i < subNum && !loginQueue.isEmpty(); i++) {
                RoleObject roleObject = loginQueue.poll();
                roleObject.loginFinish();
            }
        }
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void scheduled() {
        long time = System.currentTimeMillis() - 10 * 60 * 1000;
        roleObjectMap.values().removeIf(roleObject -> !roleObject.getSession().isOpen() && roleObject.getLogoutTime() < time);
    }
}
