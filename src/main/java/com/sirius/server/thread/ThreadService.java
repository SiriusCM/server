package com.sirius.server.thread;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Service
public class ThreadService implements CommandLineRunner {
    @Autowired
    private EventLoopGroup workerGroup;

    public final List<LogicThread> logicThreadList = new ArrayList<>();

    public final Map<EventLoop, DBThread> dbThreadMap = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
        for (EventExecutor eventExecutor : workerGroup) {
            EventLoop eventLoop = (EventLoop) eventExecutor;
            DBThread dbThread = new DBThread("db-" + eventLoop.hashCode());
            dbThread.start();
            dbThreadMap.put(eventLoop, dbThread);
        }

        int processors = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < processors; i++) {
            LogicThread logicThread = new LogicThread("logic-" + i);
            logicThread.start();
            logicThreadList.add(logicThread);
        }
    }
}
