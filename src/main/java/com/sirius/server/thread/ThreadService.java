package com.sirius.server.thread;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Data
@Service
public class ThreadService implements CommandLineRunner {
    @Autowired
    private EventLoopGroup workerGroup;

    public final Map<EventLoop, LogicThread> logicThreadMap = new HashMap<>();

    public final Map<EventLoop, DBThread> dbThreadMap = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
        for (EventExecutor eventExecutor : workerGroup) {
            EventLoop eventLoop = (EventLoop) eventExecutor;
            LogicThread logicThread = new LogicThread("logic-" + eventLoop.hashCode());
            logicThread.start();
            logicThreadMap.put(eventLoop, logicThread);
            DBThread dbThread = new DBThread("db-" + eventLoop.hashCode());
            dbThread.start();
            dbThreadMap.put(eventLoop, dbThread);
        }
    }
}
