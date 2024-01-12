package org.sirius.server.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * @author Gaoliandi
 * @Date 2024/1/12
 */
@Data
public class RemoteEvent implements Serializable {
    private long playerId;
    private ApplicationEvent event;
}
