package org.sirius.server.room;


import org.sirius.server.dispatch.MsgId;
import org.sirius.server.match.MatchService;
import org.sirius.server.remote.ServiceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

/**
 * @author gaoliandi
 * @time 2023/8/5
 */
@Controller
@Scope("prototype")
public class RoomController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MatchService matchService;
    private IRoomService iRoomService;

    @MsgId(id = 1101)
    public void match(int msgId, byte[] data) throws RemoteException, NotBoundException {
        String matchName = (String) stringRedisTemplate.opsForHash().get(MatchService.class.getSimpleName(), String.valueOf(1));
        RestTemplate restTemplate = new RestTemplate();
        ServiceInfo serviceInfo = restTemplate.postForObject(matchName + "registerRoomService", Map.of(), ServiceInfo.class);
        Registry registry = LocateRegistry.getRegistry(serviceInfo.getHost(), serviceInfo.getPort());
        iRoomService = (IRoomService) registry.lookup(serviceInfo.toString());
    }

    @MsgId(id = 1103)
    public void fight(int msgId, byte[] data) throws InterruptedException, RemoteException {
        iRoomService.fight(1);
    }
}
