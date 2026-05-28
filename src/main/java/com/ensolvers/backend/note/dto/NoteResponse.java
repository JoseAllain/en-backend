package com.ensolvers.backend.note.dto;

import java.time.Instant;
import java.util.List;

import com.ensolvers.backend.category.model.Category;
import com.ensolvers.backend.note.model.Note;

import lombok.Data;

@Data
public class NoteResponse {

    private Long id;
    private String title;
    private String content;
    private boolean archived;
    private Long userId;
    private List<String> categories;
    private Instant createdAt;
    private Instant updatedAt;

    public static NoteResponse from(Note note) {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setContent(note.getContent());
        response.setArchived(note.isArchived());
        response.setUserId(note.getUser() != null ? note.getUser().getId() : null);
        response.setCategories(note.getCategories() == null ? List.of()
                : note.getCategories().stream().map(Category::getName).sorted().toList());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());
        return response;
    }

}