package com.masantello.bookstoremanager.repositories;

import com.masantello.bookstoremanager.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

}
