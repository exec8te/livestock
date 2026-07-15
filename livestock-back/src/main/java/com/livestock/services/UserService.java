package com.livestock.services;

import com.livestock.exceptions.BadRequestException;
import com.livestock.exceptions.EmailAlreadyExistsException;
import com.livestock.exceptions.RoleNotFoundException;
import com.livestock.exceptions.UserNotFoundException;
import com.livestock.mappers.UserMapper;
import com.livestock.models.dtos.login.RegisterRequestDto;
import com.livestock.models.dtos.user.UserPasswordUpdateDto;
import com.livestock.models.dtos.user.UserResponseDto;
import com.livestock.models.dtos.user.UserUpdateDto;
import com.livestock.models.entities.Role;
import com.livestock.models.entities.User;
import com.livestock.repositories.RoleRepository;
import com.livestock.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserResponseDto getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toResponseDto(user);
    }

    public Page<UserResponseDto> getAll(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(userMapper::toResponseDto);
    }

    public UserResponseDto createUser(RegisterRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = userMapper.toCreateEntity(dto);

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        User newUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new UserNotFoundException(savedUser.getId()));

        return userMapper.toResponseDto(newUser);
    }

    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RoleNotFoundException(dto.getRoleId()));

        user.setRole(role);

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
    }

    @Transactional
    public void deleteUser(Long idToDelete, Long currentUserId) {

        if (idToDelete.equals(currentUserId)) {
            throw new AccessDeniedException(
                    "Cannot delete yourself"
            );
        }

        if (!userRepository.existsById(idToDelete)) {
            throw new UserNotFoundException(idToDelete);
        }

        userRepository.deleteById(idToDelete);
    }


}
