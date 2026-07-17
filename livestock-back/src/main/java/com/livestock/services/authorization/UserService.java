package com.livestock.services.authorization;

import com.livestock.exceptions.BadRequestException;
import com.livestock.exceptions.authorization.EmailAlreadyExistsException;
import com.livestock.exceptions.authorization.UserNotFoundException;
import com.livestock.mappers.authorization.UserMapper;
import com.livestock.models.dtos.authorization.login.RegisterRequestDto;
import com.livestock.models.dtos.authorization.user.UserPasswordUpdateDto;
import com.livestock.models.dtos.authorization.user.UserResponseDto;
import com.livestock.models.dtos.authorization.user.UserUpdateDto;
import com.livestock.models.entities.authorization.User;
import com.livestock.models.principals.authorization.UserPrincipal;
import com.livestock.repositories.authorization.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserResponseDto getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toResponseDto(user);
    }

    public Page<UserResponseDto> getAll(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(userMapper::toResponseDto);
    }

    public UserResponseDto createUser(RegisterRequestDto dto, UserPrincipal principal) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = userMapper.toCreateEntity(dto);

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        User newUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new UserNotFoundException(savedUser.getId()));


        if (principal != null) {
            log.info(
                    "event=USER_CREATED actorType=USER actorId={} actorAuthorities={} targetType=USER targetId={} status=SUCCESS",
                    principal.getId(),
                    principal.getAuthorities(),
                    newUser.getId()
            );
        } else {
            log.info(
                    "event=USER_CREATED actorType=SYSTEM actorId=PUBLIC_REGISTRATION targetType=USER targetId={} status=SUCCESS",
                    newUser.getId()
            );
        }

        return userMapper.toResponseDto(newUser);
    }

    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.getEmail().equals(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        if (!userMapper.hasChanges(dto, user)) {
            throw new BadRequestException("No changes were made");
        }

        userMapper.updateEntity(dto, user);

        return userMapper.toResponseDto(user);
    }

    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(
                dto.getOldPassword(),
                user.getPasswordHash()
        )) {
            throw new BadRequestException("Old password is incorrect");
        }

        if (passwordEncoder.matches(
                dto.getNewPassword(),
                user.getPasswordHash()
        )) {
            throw new BadRequestException("New password can't be the same as old");
        }


        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));

        userRepository.save(user);

        log.info("User {} updated his password", user.getId());
    }

    @Transactional
    public void deleteUser(Long idToDelete, UserPrincipal principal) {

        Long actorId = principal.getId();

        if (idToDelete.equals(actorId)) {
            throw new AccessDeniedException("Cannot delete yourself");
        }

        if (!userRepository.existsById(idToDelete)) {
            throw new UserNotFoundException(idToDelete);
        }

        userRepository.deleteById(idToDelete);

        log.info(
                "event=USER_DELETED actorId={} targetId={} status=SUCCESS",
                actorId,
                idToDelete
        );
    }

    public boolean existsByEmail(@NotBlank @Valid @Email String email) {
        return userRepository.existsByEmail(email);
    }

    public User register(RegisterRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = userMapper.toCreateEntity(dto);

        user.setPasswordHash(
                passwordEncoder.encode(dto.getPassword())
        );

        user.setRole(roleService.getDefaultRole());

        User savedUser = userRepository.save(user);

        log.info(
                "event={} actorType={} actorId={} targetType={} targetId={} status=SUCCESS",
                "USER_REGISTERED",
                "SYSTEM",
                "PUBLIC_REGISTRATION",
                "USER",
                savedUser.getId()
        );

        return savedUser;
    }
}
