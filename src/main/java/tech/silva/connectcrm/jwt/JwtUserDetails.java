package tech.silva.connectcrm.jwt;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import tech.silva.connectcrm.models.AppUser;

public class JwtUserDetails extends User {

    private AppUser user;

    public JwtUserDetails(AppUser user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public Long getId() {
        return this.user.getId();
    }

    public String getRole() {
        return this.user.getRole().name();
    }

}
