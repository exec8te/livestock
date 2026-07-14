package com.livestock.controllers;

import com.livestock.models.dtos.user.UserCreateDto;
import com.livestock.models.dtos.user.UserPasswordUpdateDto;
import com.livestock.models.dtos.user.UserResponseDto;
import com.livestock.models.dtos.user.UserUpdateDto;
import com.livestock.models.principals.UserPrincipal;
import com.livestock.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Page<UserResponseDto> getAll(
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        return userService.getAll(pageable);
    }

    @GetMapping("/me")
    public UserResponseDto getMe(
            @AuthenticationPrincipal UserPrincipal principal) {

        return userService.getUserById(principal.getId());
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(
            @Valid @RequestBody UserCreateDto dto) {

        UserResponseDto user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto dto) {
        UserResponseDto user = userService.updateUser(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserPasswordUpdateDto dto) {

        userService.updatePassword(principal.getId(), dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        userService.deleteUser(
                id,
                principal.getId()
        );

        return ResponseEntity.noContent().build();
    }
}