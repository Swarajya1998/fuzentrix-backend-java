package com.fuzentrix.backend.security;

import com.fuzentrix.backend.entity.User;
import com.fuzentrix.backend.entity.UserPermission;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CustomUserDetails implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public UUID getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add permissions from roles
        user.getRoles().forEach(role -> {
            role.getPermissions().forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            });
        });

        // Add specific user permissions (overrides)
        for (UserPermission userPerm : user.getUserPermissions()) {
            if (userPerm.getIsGranted()) {
                authorities.add(new SimpleGrantedAuthority(userPerm.getPermission().getName()));
            } else {
                // Remove if explicitly denied
                authorities.remove(new SimpleGrantedAuthority(userPerm.getPermission().getName()));
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive() != null && user.getIsActive();
    }
}
