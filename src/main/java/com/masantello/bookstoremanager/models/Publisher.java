package com.masantello.bookstoremanager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "PUBLISHER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "publisher_sequence")
    @SequenceGenerator(name = "publisher_sequence", sequenceName = "S_PUBLISHER", allocationSize = 1)
    @Column(name = "PUBLISHER_ID")
    private Long id;

    @Column(name = "PUBLISHER_NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "PUBLISHER_CODE", nullable = false, unique = true)
    private String code;

    @Column(name = "PUBLISHER_DESCRIPTION")
    private String description;

    @Column(name = "FOUNDATION_DATE", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate foundationDate;

    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
    private List<Book> books;

}
