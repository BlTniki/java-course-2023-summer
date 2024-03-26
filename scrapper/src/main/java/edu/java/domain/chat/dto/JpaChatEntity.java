package edu.java.domain.chat.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat")
@Entity
public class JpaChatEntity {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
}
