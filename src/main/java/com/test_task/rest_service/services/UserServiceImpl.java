package com.test_task.rest_service.services;

import com.test_task.rest_service.exceptions.ErrorMessage;
import com.test_task.rest_service.exceptions.UserException;
import com.test_task.rest_service.models.dto.PartialUpdateUserDTO;
import com.test_task.rest_service.models.dto.UserDTO;
import com.test_task.rest_service.models.entity.UserEntity;
import com.test_task.rest_service.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationService validationService;

    @Override
    public UserEntity createUser(UserDTO userDTO) {
        validationService.validateUser(userDTO);
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserException(ErrorMessage.EMAIL_ALREADY_USED);
        }
        UserEntity userEntity = new UserEntity();
        dtoToEntity(userDTO, userEntity);
        return userRepository.save(userEntity);
    }

    @Override
    @CachePut(value = "user", key = "#userId")
    public UserEntity updateUser(UserDTO userDTO, Long userId) {
        validationService.validateUser(userDTO);
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() ->
                new UserException(ErrorMessage.USER_NOT_EXIST));
        if (!userEntity.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserException(ErrorMessage.EMAIL_ALREADY_USED);
        }
        dtoToEntity(userDTO, userEntity);
        return userRepository.save(userEntity);
    }

    private void dtoToEntity(UserDTO userDTO, UserEntity userEntity) {
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setFirstName(userDTO.getFirstName());
        userEntity.setLastName(userDTO.getLastName());
        userEntity.setBirthDate(userDTO.getBirthDate());
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setPhoneNumber(userDTO.getPhoneNumber());
    }

    @Override
    @CachePut(value = "user", key = "#userId")
    public UserEntity partialUserUpdate(PartialUpdateUserDTO userDTO, Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() ->
                new UserException(ErrorMessage.USER_NOT_EXIST));
        update(userDTO, userEntity);
        return userRepository.save(userEntity);
    }

    private void update(PartialUpdateUserDTO userDTO, UserEntity userEntity) {
        if (userDTO.getEmail() != null) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new UserException(ErrorMessage.EMAIL_ALREADY_USED);
            }
            userEntity.setEmail(userDTO.getEmail());
        }
        if (userDTO.getBirthDate() != null) {
            validationService.validateUserAge(userDTO.getBirthDate());
            userEntity.setBirthDate(userDTO.getBirthDate());
        }

        userDTO.getFirstName().ifPresent(userEntity::setFirstName);
        userDTO.getLastName().ifPresent(userEntity::setLastName);
        userDTO.getAddress().ifPresent(userEntity::setAddress);
        userDTO.getPhoneNumber().ifPresent(userEntity::setPhoneNumber);
    }

    @Override
    public List<UserEntity> findAllUsersByRange(LocalDate from, LocalDate to) {
        validationService.validateDates(from, to);
        return userRepository.findAllByBirthDateAfterAndBirthDateBefore(from, to);
    }

    @Override
    @Cacheable(value = "user", key = "#userId")
    public UserEntity findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserException(ErrorMessage.USER_NOT_EXIST));
    }

    @Override
    @CacheEvict(value = "user", key = "#userId")
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(
                userRepository::delete,
                () -> {
                    throw new UserException(ErrorMessage.USER_NOT_EXIST);
                });
    }
}
