package com.test_task.rest_service.repositories;

import com.test_task.rest_service.models.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    List<UserEntity> findAllByBirthDateAfterAndBirthDateBefore(LocalDate from, LocalDate to);
}
