package cz.cvut.fel.tmr.dto.user;

import cz.cvut.fel.tmr.dto.ImageReadDto;
import cz.cvut.fel.tmr.model.Image;
import cz.cvut.fel.tmr.model.Role;
import cz.cvut.fel.tmr.model.User;
import cz.cvut.fel.tmr.model.relations.UserOrganization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.cvut.fel.tmr.config.ConfigConstants.*;
import static cz.cvut.fel.tmr.config.ConfigConstants.AVATARS_PUBLIC_PATH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserReadDto implements Serializable {
    private Long id;
    private String username;
    private String name;
    private Map<Long, String> roles;
    private ImageReadDto avatar;

    public UserReadDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.roles = user.getUserOrganizations().stream()
                .collect(Collectors.toMap(e-> e.getOrganization().getId(),e->e.getRole().toString()));
        Image avatar = user.getAvatar();
        if(avatar != null)
            this.avatar = new ImageReadDto(avatar, AVATARS_PUBLIC_PATH);
    }
    public UserReadDto(UserOrganization user) {
        this.id = user.getUser().getId();
        this.username = user.getUser().getUsername();
        this.name = user.getUser().getName();
        this.roles = new HashMap<>();
        roles.put(user.getOrganization().getId(),user.getRole().getName());
        Image avatar = user.getUser().getAvatar();
        if(avatar != null)
            this.avatar = new ImageReadDto(avatar, AVATARS_PUBLIC_PATH);
    }
}
