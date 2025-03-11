package com.sirius.server.global;

import com.sirius.server.object.RoleObject;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Service
public class GlobalService {

    private AtomicInteger roleCount = new AtomicInteger(0);

    private Map<Long, RoleObject> roleObjectMap = new ConcurrentHashMap<>();

    private Queue<RoleObject> loginQueue = new ConcurrentLinkedQueue<>();

    public void loginRequest(RoleObject roleObject) {
        if (roleCount.incrementAndGet() < 4000) {
            roleObjectMap.put(roleObject.getId(), roleObject);
            roleObject.loginFinish();
        } else {
            loginQueue.add(roleObject);
        }
    }

    @Scheduled(fixedRate = 3000)
    public void loginHandle() {
        int roleNum = roleObjectMap.size();
        int subNum = 4000 - roleNum;
        if (subNum > 0) {
            for (int i = 0; i < subNum && !loginQueue.isEmpty(); i++) {
                RoleObject roleObject = loginQueue.poll();
                roleObject.loginFinish();
                roleObjectMap.put(roleObject.getId(), roleObject);
            }
            roleCount = new AtomicInteger(roleObjectMap.size());
        }
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void removeOffline() {
        long time = System.currentTimeMillis() - 10 * 60 * 1000;
        List<RoleObject> list = roleObjectMap.values().stream().filter(
                roleObject -> !roleObject.getSession().isOpen() && roleObject.getLogoutTime() < time && roleObject.getDbQueue().isEmpty()).toList();
        for (RoleObject roleObject : list) {
            roleObjectMap.remove(roleObject.getId());
            roleObject.logoutFinish();
        }
        roleCount = new AtomicInteger(roleObjectMap.size());
    }
}
