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

    @Operation(
            summary = "Authenticate to API",
            description = "Authenticates the user and returns a bearer token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authentication successful, bearer token returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid credentials",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid input fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtToken> authenticate(@RequestBody @Valid UserLoginDTO dto) {
        String username = dto.username();
        try {
            if (!username.contains("@")) {
                AppUser user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new ObjectNotFoundException("User not found. Please check the username and try again."));
                username = user.getEmail();
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, dto.password());

            authenticationManager.authenticate(authenticationToken);

            JwtToken token = detailsService.getTokenAuthenticated(username);
            return ResponseEntity.ok(token);

        } catch (AuthenticationException ex) {
            throw new InvalidCredencialException("Invalid credentials for user: " + username);
        }
    }

    @Operation(
            summary = "Get authenticated user",
            description = "Returns the currently authenticated user's details",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authenticated user returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Access forbidden",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getAuthenticatedUser(@AuthenticationPrincipal JwtUserDetails userDetails) {
        AppUser user = userService.findById(userDetails.getId());
        return ResponseEntity.ok().body(UserResponseDTO.toUserDto(user));
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully registered",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data or credentials",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid input fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO userDto) {
        AppUser user = userService.saveUser(UserCreateDTO.toUser(userDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.toUserDto(user));
    }


}
