
package com.ridesync_v2.ridesync.Service;

import com.ridesync_v2.ridesync.Entity.User;
import com.ridesync_v2.ridesync.Entity.UserPrincipal;
import com.ridesync_v2.ridesync.Repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "No user found with email: " + email
                        ));

        return new UserPrincipal(user);
    }
}
