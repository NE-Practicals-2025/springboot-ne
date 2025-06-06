package com.proj.springsecrest.security;

import com.proj.springsecrest.enums.EGender;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.models.Employee;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private UUID code;

    private String email;

    private String firstName;

    private String lastName;

    private String telephone;

    private LocalDate dateOfBirth;

    @JsonIgnore
    private String password;

    private EUserStatus status;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(Employee user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString())).collect(Collectors.toList());

        return new UserPrincipal(
                user.getCode(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getTelephone(),
                user.getDateOfBirth(),
                user.getPassword(),
                user.getStatus(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return true;
    }
}
