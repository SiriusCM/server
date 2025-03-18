package com.sirius.server.io.endpoint;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sirius.server.Msg;
import com.sirius.server.object.RoleObject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
    public void onMessage(byte[] data) throws InvalidProtocolBufferException {
        Msg.Message message = Msg.Message.parseFrom(data);
        roleObject.dispatchMsg(message);
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
