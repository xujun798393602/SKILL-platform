package com.skill.platform.auth.security;

import com.skill.platform.auth.model.User;
import com.skill.platform.auth.model.UserRole;
import com.skill.platform.auth.repository.RoleRepository;
import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.auth.repository.UserRoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                     UserRoleRepository userRoleRepository,
                                     RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        User user = userRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + employeeId));

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<GrantedAuthority> authorities = userRoles.stream()
            .map(ur -> roleRepository.findById(ur.getRole().getId()))
            .filter(Optional::isPresent)
            .map(opt -> new SimpleGrantedAuthority("ROLE_" + opt.get().getName()))
            .toList();

        return new org.springframework.security.core.userdetails.User(
            user.getId().toString(),
            user.getPasswordHash(),
            user.getStatus().equals("active"),
            true, true, true,
            authorities
        );
    }
}
