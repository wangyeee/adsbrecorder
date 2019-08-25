package adsbrecorder.user.repo;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import adsbrecorder.user.entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    @Query("select a from Authority a where a.authority = :name")
    Optional<Authority> findOneByName(@Param("name") String name);

    default Authority syncOne(Authority authority) {
        Optional<Authority> exist = findOneByName(authority.getAuthority());
        if (exist.isEmpty()) {
            return save(authority);
        }
        return exist.get();
    }

    default void syncAll(Collection<Authority> authorities) {
        authorities.forEach(authority -> syncOne(authority));
    }
}
