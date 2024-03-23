package edu.java.domain.dao.link;

import edu.java.domain.dto.JpaLinkEntity;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkDao extends JpaRepository<JpaLinkEntity, Long> {
    @NotNull Optional<JpaLinkEntity> findByUrl(@NotNull URI uri);

    /**
     * Возвращает все {@link JpaLinkEntity} у которых lastUpdate равен или до данного.
     * @param lastCheck значение lastCheck
     * @return все {@link JpaLinkEntity} у которых lastUpdate равен или до данного
     */
    @Query("SELECT * FROM link WHERE last_check <= ?")
    @NotNull List<JpaLinkEntity> findFromLastCheck(OffsetDateTime lastCheck);
}
