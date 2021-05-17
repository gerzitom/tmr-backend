package cz.cvut.fel.tmr.model;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role {
    USER("USER"),
    PROJECT_MANAGER("PROJECT_MANAGER"),
    CUSTOMER("CUSTOMER");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
