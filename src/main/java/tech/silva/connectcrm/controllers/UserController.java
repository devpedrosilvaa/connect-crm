package tech.silva.connectcrm.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.silva.connectcrm.dtos.UserCreateDTO;
import tech.silva.connectcrm.dtos.UserResponseDTO;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO userDto){
        AppUser user = userService.saveUser(UserCreateDTO.ToUser(userDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.ToUserDto(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listAllUser(){
        List<AppUser> users = userService.listAllUsers();
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.toList(users));
    }
}
