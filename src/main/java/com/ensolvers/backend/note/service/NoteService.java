package com.ensolvers.backend.note.service;

import java.util.List;

import com.ensolvers.backend.note.dto.NoteCategoryRequest;
import com.ensolvers.backend.note.dto.NoteCreateRequest;
import com.ensolvers.backend.note.dto.NoteUpsertRequest;
import com.ensolvers.backend.note.model.Note;

public interface NoteService {

    Note create(NoteCreateRequest request);

    Note update(Long id, NoteUpsertRequest request);

    void delete(Long id);

    Note archive(Long id);

    Note unarchive(Long id);

    List<Note> findActive();

    List<Note> findArchived();

    List<Note> findByUser(Long userId);

    List<Note> findActiveByUser(Long userId);

    List<Note> findArchivedByUser(Long userId);

    List<Note> findByCategory(String categoryName);

    Note addCategory(Long noteId, NoteCategoryRequest request);

    Note removeCategory(Long noteId, String categoryName);
}