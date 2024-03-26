package edu.java.domain.dao.chat;

import edu.java.domain.dto.JpaChatDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatDao extends JpaRepository<JpaChatDto, Long> {
}
