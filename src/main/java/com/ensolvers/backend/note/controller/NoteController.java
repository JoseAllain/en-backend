package com.ensolvers.backend.note.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ensolvers.backend.note.dto.NoteCategoryRequest;
import com.ensolvers.backend.note.dto.NoteCreateRequest;
import com.ensolvers.backend.note.dto.NoteResponse;
import com.ensolvers.backend.note.dto.NoteUpsertRequest;
import com.ensolvers.backend.note.exception.NoteNotFoundException;
import com.ensolvers.backend.note.service.NoteService;
import com.ensolvers.backend.user.exception.UserNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@Valid @RequestBody NoteCreateRequest request) {
        return NoteResponse.from(noteService.create(request));
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable("id") Long id, @Valid @RequestBody NoteUpsertRequest request) {
        return NoteResponse.from(noteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        noteService.delete(id);
    }

    @PatchMapping("/{id}/archive")
    public NoteResponse archive(@PathVariable("id") Long id) {
        return NoteResponse.from(noteService.archive(id));
    }

    @PatchMapping("/{id}/unarchive")
    public NoteResponse unarchive(@PathVariable("id") Long id) {
        return NoteResponse.from(noteService.unarchive(id));
    }

    @PostMapping("/{id}/categories")
    public NoteResponse addCategory(@PathVariable("id") Long id, @Valid @RequestBody NoteCategoryRequest request) {
        return NoteResponse.from(noteService.addCategory(id, request));
    }

    @DeleteMapping("/{id}/categories/{categoryName}")
    public NoteResponse removeCategory(@PathVariable("id") Long id, @PathVariable("categoryName") String categoryName) {
        return NoteResponse.from(noteService.removeCategory(id, categoryName));
    }

    @GetMapping("/category/{categoryName}")
    public List<NoteResponse> findByCategory(@PathVariable("categoryName") String categoryName) {
        return noteService.findByCategory(categoryName).stream().map(NoteResponse::from).toList();
    }

    @GetMapping("/active")
    public List<NoteResponse> getActiveNotes() {
        return noteService.findActive().stream().map(NoteResponse::from).toList();
    }

    @GetMapping("/archived")
    public List<NoteResponse> getArchivedNotes() {
        return noteService.findArchived().stream().map(NoteResponse::from).toList();
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Invalid note payload"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    public record ErrorResponse(String message) {
    }
}