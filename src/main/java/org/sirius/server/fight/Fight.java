package org.sirius.server.fight;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


/**
 * @author gaoliandi
 * @time 2023/8/7
 */
@Data
@Entity
public class Fight {
    @Id
    private long id;

    private int sn;

    private String name;
}
