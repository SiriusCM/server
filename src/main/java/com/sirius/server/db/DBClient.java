package com.sirius.server.db;

import com.sirius.server.msg.Msg;
import com.sirius.server.object.RoleObject;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class DBClient {

    private final DBServiceGrpc.DBServiceBlockingStub blockingStub;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final Map<Long, RoleObject> roleObjectMap = new ConcurrentHashMap<>();

    public DBClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        blockingStub = DBServiceGrpc.newBlockingStub(channel);
    }

    public void test(Msg.DBRequest request) {
        Msg.DBResponse response = blockingStub.sql(request);
    }

    public Future<Object> sql(Callable<Object> callable) {
        return executorService.submit(callable);
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void scheduled() {
        long time = System.currentTimeMillis() - 10 * 60 * 1000;
        roleObjectMap.values().removeIf(roleObject -> !roleObject.getSession().isOpen() && roleObject.getLogoutTime() < time);
    }
}
