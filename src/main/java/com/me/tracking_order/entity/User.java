package com.me.tracking_order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_users_username",
                columnNames = "username"
        ),
        @UniqueConstraint(
                name = "uk_users_phone",
                columnNames = "phone"
        ),
        @UniqueConstraint(
                name = "uk_users_email",
                columnNames = "email"
        )
})
public class User extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "username", nullable = false, length = 36)
    private String username;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserDiscount> userDiscounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<NotificationLog> notificationLogs = new ArrayList<>();
}
