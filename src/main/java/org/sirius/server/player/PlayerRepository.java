package org.sirius.server.player;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author gaoliandi
 * @time 2023/7/27
 */

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
