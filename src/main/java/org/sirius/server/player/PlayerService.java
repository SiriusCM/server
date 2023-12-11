package org.sirius.server.player;


import org.sirius.server.cache.CachingService;
import org.sirius.server.data.AutoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author gaoliandi
 * @time 2023/6/28
 */
@Service
@Scope("prototype")
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private CachingService cachingService;
    @AutoDB
    private Player player;
    @AutoDB
    private List<Player> list;
    @AutoDB
    private Map<Long, Player> map;

    public void fun() {

    }
}