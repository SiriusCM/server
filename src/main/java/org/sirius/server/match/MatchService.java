package org.sirius.server.match;

import org.sirius.server.remote.RmiService;
import org.sirius.server.remote.ServiceInfo;
import org.sirius.server.room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * @author gaoliandi
 * @time 2023/8/7
 */
@Service
public class MatchService {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RmiService rmiService;
    @Value("${server.id}")
    private String serverId;
    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private String port;
    private String roomHost;

    @EventListener({ApplicationReadyEvent.class})
    public void onReady(ApplicationReadyEvent event) {
        String matchName = MatchService.class.getSimpleName();
        stringRedisTemplate.opsForHash().put(matchName, serverId, "http://" + host + ":" + port + "/match/");
        roomHost = RoomService.class.getSimpleName() + "/";
    }

    public ServiceInfo registerRoomService() throws RemoteException {
        RoomService roomService = configurableListableBeanFactory.createBean(RoomService.class);
        String roomName = roomHost + System.currentTimeMillis();
        roomService.setRoomName(roomName);
        return rmiService.rebind(roomName, roomService);
    }

    public void unRegisterRoomService(String name) throws NotBoundException, RemoteException {
        rmiService.unbind(name);
    }
}
