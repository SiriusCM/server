package com.sirius.server.item;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Data
@Entity
public class Item {
    @Id
    private Long id;

    private Long roleId;

    private Integer sn;
}
