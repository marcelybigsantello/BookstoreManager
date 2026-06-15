package com.masantello.bookstoremanager.repositories;

import com.masantello.bookstoremanager.models.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    List<Publisher> findByNameContainingIgnoreCase(String name);
}
