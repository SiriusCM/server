package org.sirius.server.match;


import org.sirius.server.remote.ServiceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.RemoteException;

/**
 * @author gaoliandi
 * @time 2023/8/17
 */
@RestController
@RequestMapping("/match")
public class MatchController {
    @Autowired
    private MatchService matchService;

    @PostMapping(value = "/registerRoomService")
    public ServiceInfo registerRoomService() throws RemoteException {
        return matchService.registerRoomService();
    }
}
