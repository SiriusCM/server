package org.sirius.server.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Service;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Map;

/**
 * @author Gaoliandi
 * @Date 2024/1/12
 */
@Service
public class RmiService {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private Registry registry;
    @Autowired
    private Map<String, Remote> remoteMap;
    @Value("${server.host}")
    private String host;
    @Value("${rmi.port}")
    private int port;

    public synchronized ServiceInfo rebind(String name, Remote obj) throws RemoteException {
        ServiceInfo serviceInfo = new ServiceInfo(name, host, port);
        remoteMap.put(name, obj);
        registry.rebind(serviceInfo.toString(), obj);
        return serviceInfo;
    }

    public synchronized void unbind(String name) throws NotBoundException, RemoteException {
        registry.unbind(name);
        Remote remote = remoteMap.remove(name);
        configurableListableBeanFactory.destroyBean(remote);
    }
}
