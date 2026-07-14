package com.masantello.bookstoremanager.repositories;

import com.masantello.bookstoremanager.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.masantello.bookstoremanager.utils.NativeQueries.FIND_ALL_BOOKS_OF_AN_AUTHOR;
import static com.masantello.bookstoremanager.utils.NativeQueries.FIND_ALL_BOOKS_OF_A_PUBLISHER;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitle(String title);

    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query(value = FIND_ALL_BOOKS_OF_AN_AUTHOR, nativeQuery = true)
    List<Book> findAllBooksByAuthor(@Param("authorName") String authorName);

    @Query(value = FIND_ALL_BOOKS_OF_A_PUBLISHER, nativeQuery = true)
    List<Book> findAllBooksByPublisher(@Param("publisherName") String publisherName);

}
