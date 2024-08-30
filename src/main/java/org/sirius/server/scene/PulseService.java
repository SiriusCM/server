package org.sirius.server.scene;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
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

/**
 * @author gaoliandi
 * @time 2023/8/19
 */
@Service
@Aspect
@Scope("prototype")
public class PulseService implements InitializingBean, DisposableBean {
    @Autowired
    private ThreadFactory threadFactory;
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private Runnable runnable = () -> {
    };
    private Thread thread;
    private boolean starting;

    @After("@annotation(org.sirius.server.scene.StartPulse) && args(runnable)")
    public void startPulse(Runnable runnable) {
        this.runnable = runnable;
    }

    @Around("@annotation(org.sirius.server.scene.Sync)")
    public void sync(ProceedingJoinPoint joinPoint) {
        workQueue.add(() -> {
            try {
                joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterPropertiesSet() {
        starting = true;
        thread = threadFactory.newThread(() -> {
            while (starting) {
                while (!workQueue.isEmpty()) {
                    Runnable runnable = workQueue.poll();
                    if (runnable != null) {
                        runnable.run();
                    }
                }
                this.runnable.run();
            }
        });
        thread.start();
    }

    @Override
    public void destroy() {
        starting = false;
    }
}
