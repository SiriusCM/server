package com.sirius.server.db;

import com.sirius.server.Msg;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class DBClient {

    private final DBServiceGrpc.DBServiceBlockingStub blockingStub;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
}
