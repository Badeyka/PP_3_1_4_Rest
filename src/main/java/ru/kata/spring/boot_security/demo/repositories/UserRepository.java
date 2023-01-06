package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kata.spring.boot_security.demo.models.User;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u join fetch u.roles where u.id = :id")
    User findByID(Long id);

    @Query("select u from User u join fetch u.roles where u.email = :email")
    User findByEmail(String email);
}
