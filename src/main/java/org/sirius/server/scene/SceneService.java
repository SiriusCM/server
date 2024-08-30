package org.sirius.server.scene;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoliandi
 * @time 2023/8/7
 */
@Getter
@Setter
@Service
@Scope("prototype")
public class SceneService implements IPulseObject {
    private String name;
    private List<IPulseObject> worldObjectList = new ArrayList<>();

    @Sync
    public void fight(Object object) {

    }

    @StartPulse
    public void startPulse(Runnable runnable) {

    }

    @Override
    public void pulse() {
        worldObjectList.forEach(IPulseObject::pulse);
    }
}
