package com.masantello.bookstoremanager.models;

import com.masantello.bookstoremanager.models.enums.Gender;
import com.masantello.bookstoremanager.models.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "USER_BOOKSTORE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "S_USER", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USER_NAME", nullable = false)
    private String name;

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "USER_BIRTHDATE", columnDefinition = "TIMESTAMP")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER", nullable = false)
    private Gender gender;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "USER_PASSWORD", nullable = false, unique = true)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Book> books;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE_BOOKSTORE", nullable = false, length = 20)
    private Role role;
}
