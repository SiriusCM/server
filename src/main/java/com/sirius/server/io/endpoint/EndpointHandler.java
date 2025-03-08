package com.sirius.server.io.endpoint;

import com.sirius.server.object.RoleObject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

@Component
@ServerEndpoint(value = "/ws")
public class EndpointHandler implements ApplicationContextAware {

    private static ConfigurableApplicationContext configurableApplicationContext;

    private Session session;

    private RoleObject roleObject;

    @OnMessage
    public void onMessage(String data) {

    }

    @OnMessage
    public void onMessage(byte[] data) {
//        Msg.Message message = Msg.Message.parseFrom(data);
//        MsgInvoker msgInvoker = userService.buildMsg(message);
//        msgInvoker.invoke();
        ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
        threadLocal.set(1);
        threadLocal.remove();
        Deque<Integer> deque = new LinkedList<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.offer(1);
        queue.poll();
        queue.peek();

        Stack<Integer> stack = new Stack<>();
        stack.add(1);
        stack.pop();
        stack.peek();
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        roleObject = configurableApplicationContext.getBean(RoleObject.class);
        this.session = session;
        roleObject.setSession(session);
    }

    @OnClose
    public void onClose(CloseReason closeReason) {
        roleObject.setLogoutTime(System.currentTimeMillis());
    }

    @OnError
    public void onError(Throwable throwable) throws IOException {
        this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
    }
}
