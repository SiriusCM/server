package org.sirius.server.redirect;

import lombok.Data;

/**
 * @author gaoliandi
 * @time 2023/8/17
 */
@Data
public class RedirectInfo {
    private String name;
    private String host;
    private int port;

    public RedirectInfo() {
    }

    public RedirectInfo(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }
}
