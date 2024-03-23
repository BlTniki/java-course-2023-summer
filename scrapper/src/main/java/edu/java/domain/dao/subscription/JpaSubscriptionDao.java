package edu.java.domain.dao.subscription;

import edu.java.domain.dto.JpaSubscriptionEntity;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSubscriptionDao extends JpaRepository<JpaSubscriptionEntity, Long> {
    @Override
    @EntityGraph(value = "chatAndLink")
    @NotNull List<JpaSubscriptionEntity> findAll();

    @EntityGraph(value = "chatAndLink")
    @NotNull Optional<JpaSubscriptionEntity> findById(long id);

    @EntityGraph(value = "chatAndLink")
    @NotNull List<JpaSubscriptionEntity> findByChatId(long chatId);

    @EntityGraph(value = "chatAndLink")
    @NotNull List<JpaSubscriptionEntity> findByLinkId(long linkId);

    @EntityGraph(value = "chatAndLink")
    @NotNull Optional<JpaSubscriptionEntity> findByChatIdAndAlias(long chatId, @NotNull String alias);
}
