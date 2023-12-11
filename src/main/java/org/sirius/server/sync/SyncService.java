package org.sirius.server.sync;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.LockSupport;

/**
 * @author gaoliandi
 * @time 2023/8/19
 */
@Service
@Aspect
@Scope("prototype")
public class SyncService implements InitializingBean, DisposableBean {
    @Autowired
    private ThreadFactory threadFactory;
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private Thread thread;
    private boolean starting;

    @Around("@annotation(org.sirius.server.sync.Sync)")
    public void proxy(ProceedingJoinPoint joinPoint) {
        workQueue.offer(() -> {
            try {
                joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        LockSupport.unpark(thread);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        starting = true;
        thread = threadFactory.newThread(() -> {
            while (starting) {
                while (!workQueue.isEmpty()) {
                    Runnable runnable = workQueue.poll();
                    if (runnable != null) {
                        runnable.run();
                    }
                }
                LockSupport.park();
            }
        });
        thread.start();
    }

    @Override
    public void destroy() throws Exception {
        starting = false;
    }
}
