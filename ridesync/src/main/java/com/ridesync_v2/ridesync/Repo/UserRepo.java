
    package com.ridesync_v2.ridesync.Repo;

    import com.ridesync_v2.ridesync.Entity.User;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.Optional;

    public interface UserRepo extends JpaRepository<User, String> {

        Optional<User> findByUserName(String userName);

        Optional<User> findByEmailIgnoreCase(String email);

        Optional<User> findByGoogleSubject(String googleSubject);

        boolean existsByEmailIgnoreCase(String email);

        boolean existsByUserName(String userName);
    }
