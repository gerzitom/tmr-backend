package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.exception.AuthenticationException;
import cz.cvut.fel.tmr.model.SecurityUser;
import cz.cvut.fel.tmr.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User userDto = userService.findByUsername(s);
        if(userDto.isRemoved()) throw new AuthenticationException("User was removed");
        return new SecurityUser(userDto);
    }
}
