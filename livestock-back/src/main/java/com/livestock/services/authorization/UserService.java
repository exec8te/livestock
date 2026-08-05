package com.livestock.services.authorization;

import com.livestock.exceptions.BadRequestException;
import com.livestock.exceptions.authorization.EmailAlreadyExistsException;
import com.livestock.exceptions.authorization.UserNotFoundException;
import com.livestock.mappers.authorization.UserMapper;
import com.livestock.models.dtos.authorization.login.RegisterRequestDto;
import com.livestock.models.dtos.authorization.user.*;
import com.livestock.models.entities.authorization.Role;
import com.livestock.models.entities.authorization.User;
import com.livestock.models.principals.authorization.UserPrincipal;
import com.livestock.repositories.authorization.UserRepository;
import com.livestock.specifications.authorization.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;


    public UserResponseDto getUserById(String userId) {

        User user = findEntityById(userId);

        return userMapper.toResponseDto(user);
    }


    public Page<UserResponseDto> getAll(UserFilterDto filter, Pageable pageable) {

        validateSort(pageable.getSort());

        Specification<User> spec = UserSpecification.withFilters(filter);

        return userRepository.findAll(spec, pageable)
                .map(userMapper::toResponseDto);
    }

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("fullName", "email", "createdAt", "updatedAt", "lastLoginAt");

    private void validateSort(Sort sort) {
        sort.forEach(order -> {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new BadRequestException("Tri invalide sur le champ : " + order.getProperty());
            }
        });
    }


    @Transactional
    public UserResponseDto createUser(
            RegisterRequestDto dto,
            UserPrincipal principal
    ) {

        if (userRepository.existsByEmail(dto.getEmail())) {

            log.warn(
                    "event=USER_CREATED actorId={} reason=EMAIL_ALREADY_EXISTS status=FAILED",
                    principal != null ? principal.getId() : "PUBLIC"
            );

            throw new EmailAlreadyExistsException(dto.getEmail());
        }


        User user = userMapper.toCreateEntity(dto);

        user.setPasswordHash(
                passwordEncoder.encode(dto.getPassword())
        );


        User savedUser = userRepository.save(user);


        log.info(
                "event=USER_CREATED actorType={} actorId={} targetType=USER targetId={} status=SUCCESS",
                principal != null ? "USER" : "SYSTEM",
                principal != null ? principal.getId() : "PUBLIC",
                savedUser.getId()
        );


        return userMapper.toResponseDto(savedUser);
    }


    @Transactional
    public UserResponseDto updateUser(
            String userId,
            UserUpdateDto dto
    ) {

        User user = findEntityById(userId);


        if (!Objects.equals(user.getEmail(), dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {

            throw new EmailAlreadyExistsException(dto.getEmail());
        }


        if (!hasChanges(dto, user)) {

            throw new BadRequestException(
                    "No changes were made"
            );
        }


        userMapper.updateEntity(dto, user);


        log.info(
                "event=USER_UPDATED targetType=USER targetId={} status=SUCCESS",
                userId
        );


        return userMapper.toResponseDto(user);
    }

    @Transactional
    public void updateDateLastLogin(String email){

        userRepository.updateDateLastLogin(email);
    }


    @Transactional
    public void updatePassword(
            String userId,
            UserPasswordUpdateDto dto
    ) {

        User user = findEntityById(userId);


        if (!passwordEncoder.matches(
                dto.getOldPassword(),
                user.getPasswordHash()
        )) {

            log.warn(
                    "event=PASSWORD_UPDATED targetId={} reason=INVALID_OLD_PASSWORD status=FAILED",
                    userId
            );

            throw new BadRequestException(
                    "Old password is incorrect"
            );
        }


        if (passwordEncoder.matches(
                dto.getNewPassword(),
                user.getPasswordHash()
        )) {

            throw new BadRequestException(
                    "New password can't be the same as old"
            );
        }


        user.setPasswordHash(
                passwordEncoder.encode(dto.getNewPassword())
        );


        log.info(
                "event=PASSWORD_UPDATED targetType=USER targetId={} status=SUCCESS",
                userId
        );
    }


    @Transactional
    public void deleteUser(
            String userId,
            UserPrincipal principal
    ) {


        if (Objects.equals(userId, principal.getId())) {

            log.warn(
                    "event=USER_DELETED actorId={} targetId={} reason=SELF_DELETE status=FAILED",
                    principal.getId(),
                    userId
            );

            throw new AccessDeniedException(
                    "Cannot delete yourself"
            );
        }


        User user = findEntityById(userId);


        userRepository.softDelete(userId);


        log.warn(
                "event=USER_SOFT_DELETED actorId={} targetType=USER targetId={} status=SUCCESS",
                principal.getId(),
                user.getId()
        );
    }


    public void updateRoleForUsers(
            Long oldRoleId,
            Long newRoleId
    ) {

        userRepository.updateRoleForUsers(
                oldRoleId,
                newRoleId
        );
    }


    @Transactional
    public User register(RegisterRequestDto dto) {


        if (userRepository.existsByEmail(dto.getEmail())) {

            throw new EmailAlreadyExistsException(
                    dto.getEmail()
            );
        }


        User user = userMapper.toCreateEntity(dto);
        user.setDisabled(false);

        user.setPasswordHash(
                passwordEncoder.encode(dto.getPassword())
        );


        user.setRole(
                roleService.getDefaultRole()
        );


        User savedUser = userRepository.save(user);


        log.info(
                "event=USER_REGISTERED actorType=SYSTEM actorId=PUBLIC targetType=USER targetId={} status=SUCCESS",
                savedUser.getId()
        );


        return savedUser;
    }


    @Transactional
    public UserResponseDto updateUserByAdmin(
            String userId,
            UserUpdateAdminDto dto,
            UserPrincipal principal
    ) {


        User user = findEntityById(userId);


        if (!Objects.equals(user.getEmail(), dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {

            throw new EmailAlreadyExistsException(
                    dto.getEmail()
            );
        }


        Role role =
                roleService.findEntityById(dto.getRoleId());


        if (!hasChanges(dto, user)) {

            throw new BadRequestException(
                    "No changes were made"
            );
        }


        userMapper.updateByAdminEntity(dto, user);

        user.setRole(role);


        log.info(
                "event=USER_UPDATED_BY_ADMIN actorId={} targetType=USER targetId={} status=SUCCESS",
                principal.getId(),
                userId
        );


        return userMapper.toResponseDto(user);
    }


    protected User findEntityById(String id) {

        return userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id)
                );
    }


    private boolean hasChanges(
            UserUpdateDto dto,
            User user
    ) {

        return !Objects.equals(
                user.getEmail(),
                dto.getEmail()
        )
                || !Objects.equals(
                user.getFullName(),
                dto.getFullName()
        );
    }


    private boolean hasChanges(
            UserUpdateAdminDto dto,
            User user
    ) {

        return !Objects.equals(
                user.getEmail(),
                dto.getEmail()
        )
                || !Objects.equals(
                user.getFullName(),
                dto.getFullName()
        )
                || !Objects.equals(
                user.getRole().getId(),
                dto.getRoleId()
        );
    }
}