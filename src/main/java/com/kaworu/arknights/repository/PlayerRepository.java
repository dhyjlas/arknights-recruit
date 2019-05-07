package com.kaworu.arknights.repository;

import com.kaworu.arknights.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByAccessLike(String access);
}
