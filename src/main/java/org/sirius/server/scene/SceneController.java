package org.sirius.server.scene;


import org.sirius.server.dispatch.MsgId;
import org.sirius.server.match.MatchService;
import org.sirius.server.remote.ServiceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author gaoliandi
 * @time 2023/8/5
 */
@RestController
@Scope("prototype")
public class SceneController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Random random;
    private SceneService sceneService;

    @MsgId(id = 1101)
    public void match(int msgId, byte[] data) {
        Set<String> set = stringRedisTemplate.opsForSet().members(MatchService.class.getSimpleName());
        assert set != null;
        int target = random.nextInt(set.size());
        var iterator = set.iterator();
        int index = 0;
        String matchName = null;
        while (iterator.hasNext() && index < target) {
            matchName = iterator.next();
            index++;
        }
        RestTemplate restTemplate = new RestTemplate();
        ServiceInfo serviceInfo = restTemplate.postForObject(matchName + "registerSceneService", Map.of(), ServiceInfo.class);
    }

    @MsgId(id = 1103)
    public void fight(int msgId, byte[] data) {
        sceneService.fight(1);
    }
}
