package tech.silva.connectcrm.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import tech.silva.connectcrm.exceptions.ErrorMessage;
import tech.silva.connectcrm.exceptions.InvalidCredencialException;
import tech.silva.connectcrm.exceptions.ObjectNotFoundException;
import tech.silva.connectcrm.jwt.JwtToken;
import tech.silva.connectcrm.jwt.JwtUserDetails;
import tech.silva.connectcrm.jwt.JwtUserDetailsService;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.repositories.IUserRepository;
import tech.silva.connectcrm.services.UserService;

@Tag(name = "Authentication", description = "Resource to proceed with API authentication")
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

    @Operation(summary = "Authenticate to API", description = "API authentication feature",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful authentication and return of a bearer token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid credentials",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid field(s)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
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

    @Operation(summary = "Authenticated user", description = "API authenticated user feature",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful and return of a user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User not allowed to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> userLogged(@AuthenticationPrincipal JwtUserDetails userDetails){
        AppUser user =  userService.findById(userDetails.getId());
        return ResponseEntity.ok().body(UserResponseDTO.ToUserDto(user));
    }

    @Operation(summary = "Register User", description = "API register user feature",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful register user and return of a user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid credentials",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid field(s)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO userDto){
        AppUser user = userService.saveUser(UserCreateDTO.ToUser(userDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.ToUserDto(user));
    }

}
