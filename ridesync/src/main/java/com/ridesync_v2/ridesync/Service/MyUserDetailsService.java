package com.ridesync_v2.ridesync.Service;

import com.ridesync_v2.ridesync.Entity.User;
import com.ridesync_v2.ridesync.Entity.UserPrincipal;
import com.ridesync_v2.ridesync.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUserName(username);
        return new UserPrincipal(user);
    }
}
