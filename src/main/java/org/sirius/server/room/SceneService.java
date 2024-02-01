package org.sirius.server.room;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

/**
 * @author gaoliandi
 * @time 2023/8/7
 */
@Getter
@Setter
@Service
@Scope("prototype")
public class SceneService extends UnicastRemoteObject implements ISceneService {
    private String name;

    protected SceneService() throws RemoteException {
    }

    @Override
    public Object fight(Object object) throws InterruptedException, RemoteException {
        Thread.sleep(10);
        return new Random().nextDouble();
    }
}
