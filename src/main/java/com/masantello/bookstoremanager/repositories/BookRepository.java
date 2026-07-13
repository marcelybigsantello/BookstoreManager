package com.masantello.bookstoremanager.repositories;

import com.masantello.bookstoremanager.models.Book;
import com.masantello.bookstoremanager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.masantello.bookstoremanager.utils.NativeQueries.FIND_ALL_BOOKS_OF_AN_AUTHOR;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = FIND_ALL_BOOKS_OF_AN_AUTHOR, nativeQuery = true)
    List<Book> findAllBooksByAuthor(@Param("authorName") String authorName);


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
    List<Book> findAllBooksByPublisher(@Param("publisherName") String publisherName);

    Optional<Book> findByTitle(String title);

    List<Book> findByTitleContainingIgnoreCase(String title);

}
