package com.userservice.user.entity;

import com.userservice.common.entity.SoftDeletableBaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "user", indexes = @Index(
    name = "idx_email_deletedAt", columnList = "email, deletedAt", unique = true
))
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends SoftDeletableBaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String phone;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }

    @Override
    public void restore() {
        super.restore();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateUserDetails(Optional<String> name, Optional<String> phone,
        Optional<String> email, Optional<String> address) {
        name.ifPresent(n -> this.name = n);
        phone.ifPresent(p -> this.phone = p);
        email.ifPresent(e -> this.email = e);
        address.ifPresent(a -> this.address = a);
    }
}
