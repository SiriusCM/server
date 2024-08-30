package org.sirius.server.match;

import org.sirius.server.mapping.MappingService;
import org.sirius.server.remote.ServiceInfo;
import org.sirius.server.scene.SceneService;
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
    private MappingService mappingService;
    @Value("${server.id}")
    private String serverId;
    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private String port;
    private String sceneHost;

    @EventListener({ApplicationReadyEvent.class})
    public void onReady(ApplicationReadyEvent event) {
        String matchName = MatchService.class.getSimpleName();
        stringRedisTemplate.opsForSet().add(matchName, "http://" + host + ":" + port + "/match/");
        sceneHost = SceneService.class.getSimpleName() + "/";
    }

    public ServiceInfo registerSceneService() throws RemoteException {
        SceneService sceneService = configurableListableBeanFactory.createBean(SceneService.class);
        String sceneName = sceneHost + System.currentTimeMillis();
        sceneService.setName(sceneName);
        mappingService.registerMapping(sceneName, sceneService);
        sceneService.startPulse(sceneService::pulse);
        return null;
    }

    public void unRegisterSceneService(String name) throws NotBoundException, RemoteException {
        mappingService.unRegisterMapping(name, SceneService.class);
    }
}
