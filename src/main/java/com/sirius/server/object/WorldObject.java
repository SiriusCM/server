package com.sirius.server.object;

import com.sirius.server.thread.IPulse;
import lombok.Data;

@Data
public class WorldObject implements IPulse {

    protected long id;

    @Override
    public void pulse() throws Exception {

    }
}