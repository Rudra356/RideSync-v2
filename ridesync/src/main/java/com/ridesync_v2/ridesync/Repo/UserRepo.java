package com.ridesync_v2.ridesync.Repo;

import com.ridesync_v2.ridesync.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    User findByUserName(String username);
}
