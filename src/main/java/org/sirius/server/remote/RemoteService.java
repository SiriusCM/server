package org.sirius.server.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Service;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gaoliandi
 * @Date 2024/1/12
 */
@Service
public class RemoteService {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private Registry registry;
    @Value("${server.host}")
    private String host;
    @Value("${rmi.port}")
    private int port;
    private final Map<String, Remote> RemoteMap = new HashMap<>();

    public synchronized ServiceInfo rebind(String name, Remote obj) throws RemoteException {
        RemoteMap.put(name, obj);
        registry.rebind(name, obj);
        return new ServiceInfo(name, host, port);
    }

    public synchronized void unbind(String name) throws NotBoundException, RemoteException {
        registry.unbind(name);
        Remote remote = RemoteMap.remove(name);
        configurableListableBeanFactory.destroyBean(remote);
    }
}
