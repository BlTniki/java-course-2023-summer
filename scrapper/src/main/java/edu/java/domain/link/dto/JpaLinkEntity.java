package edu.java.domain.link.dto;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "link")
@Entity(name = "link")
public class JpaLinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Type(UriUserType.class)
    @Column(columnDefinition = "text", name = "url", unique = true, nullable = false)
    private URI url;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", name = "tracked_data")
    private Map<String, String> trackedData;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_check")
    private OffsetDateTime lastCheck;

    public static class UriUserType implements UserType<URI> {
        @Override
        public int getSqlType() {
            return Types.LONGVARCHAR;
        }

        @Override
        public Class<URI> returnedClass() {
            return URI.class;
        }

        @Override
        public boolean equals(URI x, URI y) {
            if (x == y) {
                return true;
            }
            if (x == null || y == null) {
                return false;
            }
            return x.equals(y);
        }

        @Override
        public int hashCode(URI uri) {
            return uri.hashCode();
        }

        @Override
        public URI nullSafeGet(
            ResultSet resultSet,
            int i,
            SharedSessionContractImplementor sharedSessionContractImplementor,
            Object o
        ) throws SQLException {
            String uriString = resultSet.getString(i);
            return uriString == null ? null : URI.create(uriString);
        }

        @Override
        public void nullSafeSet(
            PreparedStatement preparedStatement,
            URI uri,
            int i,
            SharedSessionContractImplementor sharedSessionContractImplementor
        ) throws SQLException {
            if (uri == null) {
                preparedStatement.setNull(i, getSqlType());
            } else {
                preparedStatement.setString(i, uri.toString());
            }
        }

        @Override
        public URI deepCopy(URI uri) {
            return URI.create(uri.toString());
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public Serializable disassemble(URI uri) {
            return uri.toString();
        }

        @Override
        public URI assemble(Serializable serializable, Object o) {
            return URI.create((String) serializable);
        }
    }
}
