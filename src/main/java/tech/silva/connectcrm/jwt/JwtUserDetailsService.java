package tech.silva.connectcrm.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import tech.silva.connectcrm.exceptions.UsernameNotFoundException;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.services.UserService;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userService.findByEmail(username);
        return new JwtUserDetails(user);
    }

    public JwtToken getTokenAuthenticated(String username) {
        AppUser user = userService.findByEmail(username);
        return JwtUtils.createToken(username, user.getRole().name().substring("ROLE_".length()));
    }


}
