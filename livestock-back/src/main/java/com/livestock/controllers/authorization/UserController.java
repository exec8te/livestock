package com.livestock.controllers.authorization;

import com.livestock.models.dtos.authorization.login.RegisterRequestDto;
import com.livestock.models.dtos.authorization.user.*;
import com.livestock.models.principals.authorization.UserPrincipal;
import com.livestock.services.authorization.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAuthority('USER_READ_ALL')")
    @GetMapping
    public Page<UserResponseDto> getAll(
            @ModelAttribute UserFilterDto filter,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return userService.getAll(filter, pageable);
    }

    @PreAuthorize("hasAuthority('USER_CREATE')")
    @PostMapping
    public ResponseEntity<UserResponseDto> create(
            @Valid @RequestBody RegisterRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponseDto user = userService.createUser(dto, principal);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PreAuthorize("hasAuthority('USER_READ_SELF') or hasAuthority('USER_READ_ALL')")
    @GetMapping("/me")
    public UserResponseDto getMe(
            @AuthenticationPrincipal UserPrincipal principal) {

        return userService.getUserById(principal.getId());
    }

    @PreAuthorize("hasAuthority('USER_UPDATE_SELF') or hasAuthority('USER_READ_ALL')")
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateMe(
            @Valid @RequestBody UserUpdateDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        UserResponseDto user = userService.updateUser(
                principal.getId(),
                dto
        );

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PreAuthorize("hasAuthority('USER_UPDATE_SELF')")
    @PutMapping("/me/password")
    public ResponseEntity<Void> updateMePassword(
            @Valid @RequestBody UserPasswordUpdateDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {

        userService.updatePassword(
                principal.getId(),
                dto
        );

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('USER_READ_ALL')")
    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable String id) {

        return userService.getUserById(id);
    }

    @PreAuthorize("hasAuthority('USER_UPDATE_ALL')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateAdminDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponseDto user = userService.updateUserByAdmin(
                id,
                dto,
                principal
        );

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PreAuthorize("hasAuthority('USER_UPDATE_ALL')")
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable String id,
            @Valid @RequestBody UserPasswordUpdateDto dto) {

        userService.updatePassword(
                id,
                dto
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE_ALL')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        userService.deleteUser(id, principal);

        return ResponseEntity.noContent().build();
    }
}