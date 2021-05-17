package cz.cvut.fel.tmr.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class SecurityUser extends User implements Serializable {
    private Long id;
    public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public SecurityUser(cz.cvut.fel.tmr.model.User user){
        super(user.getUsername(), user.getPassword(), List.of(new SimpleGrantedAuthority("User")));
        id = user.getId();
    }

    public String getUsername(){
        return super.getUsername();
    }

    public Long getId() {
        return id;
    }

    public String getRole(){
        String role = null;
        for(GrantedAuthority authority : super.getAuthorities()){
            role =  authority.getAuthority();
        }
        return role;
    }
}
