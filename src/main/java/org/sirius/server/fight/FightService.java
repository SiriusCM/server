package org.sirius.server.fight;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.sirius.server.bean.AutoBean;
import org.sirius.server.boot.BootService;
import org.sirius.server.data.AutoDB;
import org.sirius.server.match.MatchService;
import org.sirius.server.redirect.Connect;
import org.sirius.server.redirect.Redirect;
import org.sirius.server.redirect.RedirectInfo;
import org.sirius.server.room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author gaoliandi
 * @time 2023/8/7
 */
@Service
@Scope("prototype")
public class FightService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BootService bootService;
    @Autowired
    private MatchService matchService;
    @AutoBean
    private ChannelHandlerContext channelHandlerContext;
    @AutoDB
    private Fight fight;
    private RoomService roomService;

    @Connect
    public Channel match(int msgId, byte[] data) throws InterruptedException {
        String matchName = (String) stringRedisTemplate.opsForHash().get(MatchService.class.getSimpleName(), String.valueOf(1));
        RestTemplate restTemplate = new RestTemplate();
        RedirectInfo redirectInfo = restTemplate.postForObject(matchName + "registerRoomService", Map.of(), RedirectInfo.class);
        return bootService.connect(redirectInfo.getHost(), redirectInfo.getPort(), channelHandlerContext);
    }

    @Redirect
    public void enter(int msgId, byte[] data) {
        for (RoomService roomService : matchService.getRoomServiceMap().values()) {
            this.roomService = roomService;
            break;
        }
        roomService.enter(channelHandlerContext);
    }

    @Redirect
    public void fight(int msgId, byte[] data) throws InterruptedException {
        roomService.fight(1);
    }
}
