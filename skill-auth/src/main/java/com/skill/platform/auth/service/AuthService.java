package com.skill.platform.auth.service;

import com.skill.platform.auth.model.Role;
import com.skill.platform.auth.model.User;
import com.skill.platform.auth.model.UserRole;
import com.skill.platform.auth.model.dto.ChangePasswordRequest;
import com.skill.platform.auth.model.dto.LoginRequest;
import com.skill.platform.auth.model.dto.LoginResponse;
import com.skill.platform.auth.model.dto.RefreshTokenRequest;
import com.skill.platform.auth.model.dto.RegisterRequest;
import com.skill.platform.auth.model.dto.UserInfoResponse;
import com.skill.platform.auth.repository.RoleRepository;
import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.auth.repository.UserRoleRepository;
import com.skill.platform.auth.security.JwtService;
import com.skill.platform.auth.security.PasswordService;
import com.skill.platform.auth.security.PermissionService;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final PermissionService permissionService;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 30;

    public AuthService(UserRepository userRepository,
                       UserRoleRepository userRoleRepository,
                       RoleRepository roleRepository,
                       PasswordService passwordService,
                       JwtService jwtService,
                       PermissionService permissionService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.permissionService = permissionService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new BusinessException("AUTH001", "Invalid credentials", 401));

        // Check account status
        if ("locked".equals(user.getStatus())) {
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
                throw new BusinessException("AUTH002", "Account is locked", 423);
            }
            // Unlock if lock period expired
            user.setStatus("active");
            user.setLoginFailCount(0);
            user.setLockedUntil(null);
        }

        if ("pending".equals(user.getStatus())) {
            throw new BusinessException("AUTH003", "Account pending approval", 403);
        }

        if ("disabled".equals(user.getStatus())) {
            throw new BusinessException("AUTH004", "Account is disabled", 403);
        }

        // Validate password
        if (!passwordService.matches(request.getPassword(), user.getPasswordHash())) {
            handleLoginFailure(user);
            throw new BusinessException("AUTH001", "Invalid credentials", 401);
        }

        // Login success
        user.setLoginFailCount(0);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        // Get user roles
        List<String> roles = getUserRoles(user.getId());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user.getId().toString(), user.getEmployeeId(), roles);
        String refreshToken = jwtService.generateRefreshToken(user.getId().toString());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(7200)
                .userInfo(buildUserInfo(user, roles))
                .build();
    }

    @Transactional
    public void register(RegisterRequest request) {
        // Validate unique employeeId
        if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BusinessException("AUTH006", "Employee ID already exists", 409);
        }

        // Validate unique email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("AUTH007", "Email already exists", 409);
        }

        // Create user
        User user = User.builder()
                .employeeId(request.getEmployeeId())
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordService.encode(request.getPassword()))
                .department(request.getDepartment())
                .status("pending")
                .loginFailCount(0)
                .build();

        user = userRepository.save(user);

        // Assign default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new BusinessException("SYSTEM001", "Default role not found", 500));

        UserRole ur = UserRole.builder()
                .user(user)
                .role(userRole)
                .build();
        userRoleRepository.save(ur);

        log.info("User registered: {}", user.getEmployeeId());
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String userId = jwtService.validateRefreshToken(request.getRefreshToken());
        if (userId == null) {
            throw new BusinessException("AUTH005", "Invalid or expired refresh token", 401);
        }

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException("AUTH001", "User not found", 401));

        // Invalidate old refresh token
        jwtService.invalidateRefreshToken(request.getRefreshToken());

        // Generate new tokens
        List<String> roles = getUserRoles(user.getId());
        String accessToken = jwtService.generateAccessToken(user.getId().toString(), user.getEmployeeId(), roles);
        String refreshToken = jwtService.generateRefreshToken(user.getId().toString());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(7200)
                .userInfo(buildUserInfo(user, roles))
                .build();
    }

    public UserInfoResponse getCurrentUser() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("AUTH005", "Not authenticated", 401);
        }

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException("AUTH001", "User not found", 401));

        List<String> roles = getUserRoles(user.getId());
        return buildUserInfo(user, roles);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("AUTH005", "Not authenticated", 401);
        }

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException("AUTH001", "User not found", 401));

        if (!passwordService.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException("AUTH001", "Invalid old password", 401);
        }

        user.setPasswordHash(passwordService.encode(request.getNewPassword()));
        userRepository.save(user);

        // Clear permission cache
        permissionService.clearPermissionCache(user.getId().toString());
    }

    private void handleLoginFailure(User user) {
        int failCount = user.getLoginFailCount() + 1;
        user.setLoginFailCount(failCount);

        if (failCount >= MAX_LOGIN_ATTEMPTS) {
            user.setStatus("locked");
            user.setLockedUntil(Instant.now().plus(Duration.ofMinutes(LOCK_DURATION_MINUTES)));
            log.warn("Account locked due to too many failed attempts: {}", user.getEmployeeId());
        }

        userRepository.save(user);
    }

    private List<String> getUserRoles(UUID userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .map(ur -> ur.getRole().getName())
                .toList();
    }

    private UserInfoResponse buildUserInfo(User user, List<String> roles) {
        return UserInfoResponse.builder()
                .id(user.getId().toString())
                .employeeId(user.getEmployeeId())
                .name(user.getName())
                .email(user.getEmail())
                .department(user.getDepartment())
                .role(roles.isEmpty() ? "USER" : roles.get(0))
                .build();
    }
}
