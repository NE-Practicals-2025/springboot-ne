package com.proj.springsecrest.models;

import com.proj.springsecrest.audits.TimestampAudit;
import com.proj.springsecrest.enums.EUserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empoyees", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"}), @UniqueConstraint(columnNames = {"telephone"})})
public class Employee extends TimestampAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "code")
    private UUID code;

    @NotBlank
    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "telephone")
    private String telephone;

    @JsonIgnore
    @NotBlank
    @Column(name = "password")
    private String password;

    @NotBlank
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "activation_code")
    private String activationCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EUserStatus status = EUserStatus.ACTIVE;

    @JsonIgnore
    @Column(name = "activation_code_expires_at")
    private LocalDateTime activationCodeExpiresAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "employee_roles", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public Employee(String email, String firstName, String lastName, String telephone, String password, LocalDate dateOfBirth, String activationCode, EUserStatus status, LocalDateTime activationCodeExpiresAt) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telephone = telephone;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.activationCode = activationCode;
        this.status = status;
    }
}
