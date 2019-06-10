package com.kaworu.arknights.repository;

import com.kaworu.arknights.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
