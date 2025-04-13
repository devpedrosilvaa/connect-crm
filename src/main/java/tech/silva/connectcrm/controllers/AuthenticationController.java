package tech.silva.connectcrm.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.silva.connectcrm.dtos.UserCreateDTO;
import tech.silva.connectcrm.dtos.UserLoginDTO;
import tech.silva.connectcrm.dtos.UserResponseDTO;
import tech.silva.connectcrm.exceptions.InvalidCredencialException;
import tech.silva.connectcrm.exceptions.ObjectNotFoundException;
import tech.silva.connectcrm.jwt.JwtToken;
import tech.silva.connectcrm.jwt.JwtUserDetails;
import tech.silva.connectcrm.jwt.JwtUserDetailsService;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.repositories.IUserRepository;
import tech.silva.connectcrm.services.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final JwtUserDetailsService detailsService;
    private final AuthenticationManager authenticationManager;
    private final IUserRepository userRepository;
    private final UserService userService;

    public AuthenticationController(JwtUserDetailsService detailsService, AuthenticationManager authenticationManager, IUserRepository userRepository, UserService userService) {
        this.detailsService = detailsService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authentication(@RequestBody @Valid UserLoginDTO dto) {
        var username = "";
        try {
            username = dto.username();
            if (!dto.username().contains("@")){
                AppUser user = userRepository.findByEmail(dto.username())
                        .orElseThrow(
                                () -> new ObjectNotFoundException("User not found. Please check the user User and try again.")
                        );
                username = user.getEmail();
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, dto.password());

            authenticationManager.authenticate(authenticationToken);

            JwtToken token = detailsService.getTokenAuthenticated(username);

            return ResponseEntity.ok(token);
        }catch (AuthenticationException ex) {
            throw new InvalidCredencialException("Invalid credencial " + username);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> userLogged(@AuthenticationPrincipal JwtUserDetails userDetails){
        AppUser user =  userService.findById(userDetails.getId());
        return ResponseEntity.ok().body(UserResponseDTO.ToUserDto(user));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO userDto){
        AppUser user = userService.saveUser(UserCreateDTO.ToUser(userDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.ToUserDto(user));
    }

}
