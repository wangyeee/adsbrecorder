package adsbrecorder.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import adsbrecorder.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByUsername(String username);

    /**
     * Find all users, results are orderde by User.username
     * @param page0 0 based page number
     * @param amount amount
     * @return List of users
     */
    default List<User> findAll(int page0, int amount) {
        PageRequest page = PageRequest.of(page0, amount, new Sort(Sort.Direction.ASC, "username"));
        return findAll(page).getContent();
    }
}
