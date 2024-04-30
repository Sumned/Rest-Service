package com.test_task.rest_service.services;

import com.test_task.rest_service.models.dto.PartialUpdateUserDTO;
import com.test_task.rest_service.models.dto.UserDTO;
import com.test_task.rest_service.models.entity.UserEntity;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    UserEntity createUser(UserDTO userDTO);

    UserEntity updateUser(UserDTO userDTO, Long userId);

    UserEntity partialUserUpdate(PartialUpdateUserDTO userDTO, Long userId);

    List<UserEntity> findAllUsersByRange(LocalDate from, LocalDate to);

    UserEntity findUser(Long userId);

    void deleteUser(Long userId);
}
