package com.ensolvers.backend.note.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ensolvers.backend.category.model.Category;
import com.ensolvers.backend.category.repository.CategoryRepository;
import com.ensolvers.backend.note.dto.NoteCategoryRequest;
import com.ensolvers.backend.note.dto.NoteCreateRequest;
import com.ensolvers.backend.note.dto.NoteUpsertRequest;
import com.ensolvers.backend.note.exception.NoteNotFoundException;
import com.ensolvers.backend.note.model.Note;
import com.ensolvers.backend.note.repository.NoteRepository;
import com.ensolvers.backend.user.exception.UserNotFoundException;
import com.ensolvers.backend.user.model.User;
import com.ensolvers.backend.user.repository.UserRepository;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Note create(NoteCreateRequest request) {
        Note note = new Note();
        note.setTitle(request.getTitle().trim());
        note.setContent(request.getContent().trim());
        note.setUser(findUserByIdOrThrow(request.getUserId()));
        return noteRepository.save(note);
    }

    @Override
    @Transactional
    public Note update(Long id, NoteUpsertRequest request) {
        Note note = findByIdOrThrow(id);
        note.setTitle(request.getTitle().trim());
        note.setContent(request.getContent().trim());
        return noteRepository.save(note);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Note note = findByIdOrThrow(id);
        noteRepository.delete(note);
    }

    @Override
    @Transactional
    public Note archive(Long id) {
        Note note = findByIdOrThrow(id);
        note.setArchived(true);
        return noteRepository.save(note);
    }

    @Override
    @Transactional
    public Note unarchive(Long id) {
        Note note = findByIdOrThrow(id);
        note.setArchived(false);
        return noteRepository.save(note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findActive() {
        return noteRepository.findAllByArchivedOrderByUpdatedAtDesc(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findArchived() {
        return noteRepository.findAllByArchivedOrderByUpdatedAtDesc(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findByUser(Long userId) {
        return noteRepository.findAllByUser_IdOrderByUpdatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findActiveByUser(Long userId) {
        return noteRepository.findAllByUser_IdAndArchivedOrderByUpdatedAtDesc(userId, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findArchivedByUser(Long userId) {
        return noteRepository.findAllByUser_IdAndArchivedOrderByUpdatedAtDesc(userId, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findByCategory(String categoryName) {
        return noteRepository.findAllByCategories_NameOrderByUpdatedAtDesc(normalize(categoryName));
    }

    @Override
    @Transactional
    public Note addCategory(Long noteId, NoteCategoryRequest request) {
        Note note = findByIdOrThrow(noteId);
        String categoryName = normalize(request.getCategoryName());
        Category category = categoryRepository.findByNameIgnoreCase(categoryName).orElseGet(() -> {
            Category newCategory = new Category();
            newCategory.setName(categoryName);
            return categoryRepository.save(newCategory);
        });
        note.getCategories().add(category);
        category.getNotes().add(note);
        return noteRepository.save(note);
    }

    @Override
    @Transactional
    public Note removeCategory(Long noteId, String categoryName) {
        Note note = findByIdOrThrow(noteId);
        Category category = categoryRepository.findByNameIgnoreCase(normalize(categoryName))
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryName));
        if (!note.getCategories().contains(category)) {
            throw new IllegalArgumentException("Category not assigned to note: " + categoryName);
        }
        note.getCategories().remove(category);
        category.getNotes().remove(note);
        return noteRepository.save(note);
    }

    private Note findByIdOrThrow(Long id) {
        return noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}