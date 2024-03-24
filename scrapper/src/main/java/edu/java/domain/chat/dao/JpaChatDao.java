package edu.java.domain.chat.dao;

import edu.java.domain.chat.dto.JpaChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatDao extends JpaRepository<JpaChatEntity, Long> {
}
