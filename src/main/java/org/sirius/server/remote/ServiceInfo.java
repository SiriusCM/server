package org.sirius.server.remote;

import lombok.Data;

/**
 * @author gaoliandi
 * @time 2023/8/17
 */
@Data
public class ServiceInfo {
    private String name;
    private String host;
    private int port;

    public ServiceInfo() {
    }

    public ServiceInfo(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }
}
