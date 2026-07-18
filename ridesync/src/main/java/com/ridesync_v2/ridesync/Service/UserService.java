package com.ridesync_v2.ridesync.Service;

import com.ridesync_v2.ridesync.Config.PasswordConfig;
import com.ridesync_v2.ridesync.Entity.User;
import com.ridesync_v2.ridesync.Repo.UserRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordConfig passwordConfig;

    public User register(@Valid User user) {
        user.setPassword(passwordConfig.bCryptPasswordEncoder().encode(user.getPassword()));
        return userRepo.save(user);
    }


}
