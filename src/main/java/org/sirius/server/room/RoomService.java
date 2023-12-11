package org.sirius.server.room;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.sirius.server.sync.Sync;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author gaoliandi
 * @time 2023/8/7
 */
@Getter
@Service
@Scope("prototype")
public class RoomService {
    private Room room;
    private String roomName;
    private final List<ChannelHandlerContext> channelHandlerContextList = new ArrayList<>();

    public void registerRoomService(Room room, String roomName) {
        this.room = room;
        this.roomName = roomName;
    }

    @Sync
    public void enter(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContextList.add(channelHandlerContext);
    }

    @Sync
    public Object fight(Object object) throws InterruptedException {
        Thread.sleep(10);
        return new Random().nextDouble();
    }
}
