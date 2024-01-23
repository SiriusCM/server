package org.sirius.server.player;


import org.sirius.server.data.AutoDB;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gaoliandi
 * @time 2023/6/28
 */
@Service
@Scope("prototype")
public class PlayerService {
    @AutoDB
    private Player player;
    @AutoDB
    private List<Player> list;

    public void fun() {

    }
}