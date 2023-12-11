package org.sirius.server.match;

import lombok.Getter;
import org.sirius.server.redirect.RedirectInfo;
import org.sirius.server.room.Room;
import org.sirius.server.room.RoomRepository;
import org.sirius.server.room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaoliandi
 * @time 2023/8/7
 */
@Getter
@Service
public class MatchService {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private Random random;
    @Value("${server.id}")
    private String serverId;
    @Value("${server.host}")
    private String host;
    @Value("${tcp.port}")
    private int port;
    private String roomHost;
    private final Map<String, RoomService> roomServiceMap = new ConcurrentHashMap<>();

    @EventListener({ApplicationReadyEvent.class})
    public void onReady(ApplicationReadyEvent event) {
        String matchName = MatchService.class.getSimpleName();
        stringRedisTemplate.opsForHash().put(matchName, serverId, "http://10.4.4.208:1026/match/");
        roomHost = RoomService.class.getSimpleName() + "/";
    }

    public RedirectInfo registerRoomService() {
        Room room = new Room();
        roomRepository.save(room);
        String roomName = roomHost + room.getId();
        RoomService roomService = configurableListableBeanFactory.createBean(RoomService.class);
        roomService.registerRoomService(room, roomName);
        roomServiceMap.put(roomName, roomService);
        return new RedirectInfo(roomName, host, port);
    }

    public void disRegisterRoomService(String name) {
        RoomService roomService = roomServiceMap.remove(name);
        configurableListableBeanFactory.destroyBean(roomService);
    }
}
