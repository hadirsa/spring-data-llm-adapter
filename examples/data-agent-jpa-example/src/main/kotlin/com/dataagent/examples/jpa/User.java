package com.dataagent.examples.jpa;

import com.dataagent.jpa.annotation.DataAgent;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;


@DataAgent(
        description = "Example JPA entity User for demonstration purposes",
        discoverable = true
)
@Entity
@Table(name = "um_user",
        uniqueConstraints = @UniqueConstraint(name = "UM_USERNAME_UK", columnNames = {"username"}))
public class User {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private String id;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @Size(min = 8, max = 100)
    @Column(length = 100)
    private String password;

    @Email
    @Size(min = 5, max = 254)
    @Column(length = 254)
    private String email;

    @Column(name = "is_online", nullable = false)
    private boolean online;

    @NotNull
    @Column(name = "is_activated", nullable = false)
    private boolean activated = false;

    private LocalDateTime lastLogin;

    private Integer wrongTries;

    private boolean forceToChangePassword = false;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

}
