package edu.java.domain.dao.link;

import edu.java.domain.dto.LinkDto;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class JdbcLinkDao implements LinkDao {
    private static final String FIND_ALL_QUERY = "SELECT * FROM link";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM link WHERE id = ?";
//    private static final String FIND_BY_URL_QUERY = "SELECT * FROM LI";

    @Override
    public List<LinkDto> findAll() {
        return null;
    }

    @Override
    public LinkDto findById(long id) {
        return null;
    }

    @Override
    public LinkDto findByUrl(URI uri) {
        return null;
    }

    @Override
    public List<LinkDto> findByLastUpdate(OffsetDateTime lastUpdate) {
        return null;
    }

    @Override
    public LinkDto add(LinkDto link) {
        return null;
    }

    @Override
    public LinkDto remove(long id) {
        return null;
    }
}
