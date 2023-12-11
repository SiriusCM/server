package org.sirius.server.player;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


/**
 * @author gaoliandi
 * @time 2023/7/27
 */

@Data
@Entity
public class Player {
    @Id
    private long id;

    private String name;
}
