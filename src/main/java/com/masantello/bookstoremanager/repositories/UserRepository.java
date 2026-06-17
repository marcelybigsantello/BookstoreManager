package com.masantello.bookstoremanager.repositories;

import com.masantello.bookstoremanager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUsernameContainingIgnoreCase(String name);

    Optional<User> findByPassword(String password);

    Optional<User> findByEmail(String email);
}
