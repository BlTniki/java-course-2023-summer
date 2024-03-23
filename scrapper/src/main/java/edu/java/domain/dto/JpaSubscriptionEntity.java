package edu.java.domain.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "subscription",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"chat_id", "link_id"}),
        @UniqueConstraint(columnNames = {"chat_id", "alias"})
    }
)
@NamedEntityGraph(name = "chatAndLink",
                  attributeNodes = {
                      @NamedAttributeNode("chat"),
                      @NamedAttributeNode("link")
                  })
@Entity
public class JpaSubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "chat_id", nullable = false)
    private JpaChatDto chat;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "link_id", nullable = false)
    private JpaLinkEntity link;

    @Column(name = "alias", length = 10)
    private String alias;
}
