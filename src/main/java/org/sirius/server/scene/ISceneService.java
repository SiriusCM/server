package org.sirius.server.scene;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Gaoliandi
 * @Date 2024/1/12
 */
public interface ISceneService extends Remote {
    Object fight(Object object) throws InterruptedException, RemoteException;
}
