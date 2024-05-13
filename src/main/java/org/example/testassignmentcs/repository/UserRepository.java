package org.example.testassignmentcs.repository;

import org.example.testassignmentcs.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findAllByBirthDateBetween(
            LocalDate from,
            LocalDate to,
            Pageable pageable);
}
