package com.skill.platform.auth.security;

import com.skill.platform.auth.model.RolePermission;
import com.skill.platform.auth.model.UserRole;
import com.skill.platform.auth.repository.PermissionRepository;
import com.skill.platform.auth.repository.RolePermissionRepository;
import com.skill.platform.auth.repository.RoleRepository;
import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.auth.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PermissionService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PERMISSION_CACHE_KEY = "user:%s:permissions";
    private static final long CACHE_TTL = 300; // 5 minutes

    public PermissionService(UserRepository userRepository,
                             UserRoleRepository userRoleRepository,
                             RolePermissionRepository rolePermissionRepository,
                             PermissionRepository permissionRepository,
                             RoleRepository roleRepository,
                             RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.redisTemplate = redisTemplate;
    }

    @SuppressWarnings("unchecked")
    public List<String> getUserPermissions(String userId) {
        String cacheKey = String.format(PERMISSION_CACHE_KEY, userId);
        List<String> cached = (List<String>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<UserRole> userRoles = userRoleRepository.findByUserId(UUID.fromString(userId));
        List<String> permissions = new ArrayList<>();

        for (UserRole ur : userRoles) {
            List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(ur.getRole().getId());
            for (RolePermission rp : rolePermissions) {
                permissions.add(rp.getPermission().getCode());
            }
        }

        redisTemplate.opsForValue().set(cacheKey, permissions, Duration.ofSeconds(CACHE_TTL));
        return permissions;
    }

    public boolean checkPermission(String userId, String resource, String action) {
        List<String> permissions = getUserPermissions(userId);
        String requiredPermission = resource + ":" + action;
        return permissions.contains(requiredPermission);
    }

    public boolean hasRole(String userId, String roleName) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(UUID.fromString(userId));
        return userRoles.stream()
            .anyMatch(ur -> ur.getRole().getName().equals(roleName));
    }

    public void clearPermissionCache(String userId) {
        String cacheKey = String.format(PERMISSION_CACHE_KEY, userId);
        redisTemplate.delete(cacheKey);
    }
}
