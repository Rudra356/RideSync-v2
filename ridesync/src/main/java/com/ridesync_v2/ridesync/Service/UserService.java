package com.ridesync_v2.ridesync.Service;

import com.ridesync_v2.ridesync.Config.PasswordConfig;
import com.ridesync_v2.ridesync.Entity.User;
import com.ridesync_v2.ridesync.Repo.UserRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordConfig passwordConfig;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User register(@Valid User user) {
        user.setPassword(passwordConfig.bCryptPasswordEncoder().encode(user.getPassword()));
        return userRepo.save(user);
    }


    public String verify(@Valid User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword()));
        if(authentication.isAuthenticated()){
            return "Verified";
        }
        throw new UsernameNotFoundException("User not valid");
    }
}
