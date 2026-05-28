package com.ensolvers.backend.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ensolvers.backend.note.dto.NoteResponse;
import com.ensolvers.backend.note.service.NoteService;
import com.ensolvers.backend.user.dto.UserResponse;
import com.ensolvers.backend.user.dto.UserUpsertRequest;
import com.ensolvers.backend.user.exception.UserNotFoundException;
import com.ensolvers.backend.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private NoteService noteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserUpsertRequest request) {
        return UserResponse.from(userService.create(request));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable("id") Long id, @Valid @RequestBody UserUpsertRequest request) {
        return UserResponse.from(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable("id") Long id) {
        return UserResponse.from(userService.findById(id));
    }

    @GetMapping("/{id}/notes")
    public List<NoteResponse> findAllNotes(@PathVariable("id") Long id) {
        userService.findById(id);
        return noteService.findByUser(id).stream().map(NoteResponse::from).toList();
    }

    @GetMapping("/{id}/notes/active")
    public List<NoteResponse> findActiveNotes(@PathVariable("id") Long id) {
        userService.findById(id);
        return noteService.findActiveByUser(id).stream().map(NoteResponse::from).toList();
    }

    @GetMapping("/{id}/notes/archived")
    public List<NoteResponse> findArchivedNotes(@PathVariable("id") Long id) {
        userService.findById(id);
        return noteService.findArchivedByUser(id).stream().map(NoteResponse::from).toList();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Invalid user payload"));
    }

    public record ErrorResponse(String message) {
    }
}