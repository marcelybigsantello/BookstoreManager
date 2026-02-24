package com.masantello.bookstoremanager.repositories;

import com.masantello.bookstoremanager.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = """
        SELECT 
                B.ID,
                B.TITLE,
                B.ISBN
        FROM BOOK B
        LEFT JOIN AUTHOR A
        ON B.AUTHOR_ID = A.ID
        WHERE A.NAME = :authorName
    """, nativeQuery = true)
    Optional<List<Book>> findAllBooksByAuthor(@Param("authorName") String authorName);


    @Query(value = """
        SELECT 
                B.ID,
                B.TITLE,
                B.ISBN
        FROM BOOK B
        LEFT JOIN PUBLISHER P
        ON B.PUBLISHER_ID = P.ID
        WHERE P.NAME = :publisherName
    """, nativeQuery = true)
    Optional<List<Book>> findAllBooksByPublisher(@Param("publisherName") String publisherName);
}
