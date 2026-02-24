package com.masantello.bookstoremanager.models;

import com.masantello.bookstoremanager.models.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "birthDate", columnDefinition = "TIMESTAMP")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Book> books;
}
