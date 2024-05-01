package com.test_task.rest_service.controller.api;

import com.test_task.rest_service.models.dto.PartialUpdateUserDTO;
import com.test_task.rest_service.models.dto.ResponseDTO;
import com.test_task.rest_service.models.dto.UserDTO;
import com.test_task.rest_service.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.test_task.rest_service.Constants.DATE_PATTERN;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDTO(userService.createUser(userDTO)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> totalUserUpdate(@Valid @RequestBody UserDTO userDTO, @PathVariable(name = "id") Long userId) {
        return ResponseEntity.ok(new ResponseDTO(userService.updateUser(userDTO, userId)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO> partialUserUpdate(@Valid @RequestBody PartialUpdateUserDTO partialUpdateUserDTO,
                                                         @PathVariable(name = "id") Long userId) {
        return ResponseEntity.ok(new ResponseDTO(userService.partialUserUpdate(partialUpdateUserDTO, userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getUser(@PathVariable(name = "id") Long userId) {
        return ResponseEntity.ok(new ResponseDTO(userService.findUser(userId)));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getUsers(@RequestParam(name = "from") @DateTimeFormat(pattern = DATE_PATTERN) LocalDate from,
                                                @RequestParam(name = "to") @DateTimeFormat(pattern = DATE_PATTERN) LocalDate to) {
        return ResponseEntity.ok(new ResponseDTO(userService.findAllUsersByRange(from, to)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable(name = "id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
