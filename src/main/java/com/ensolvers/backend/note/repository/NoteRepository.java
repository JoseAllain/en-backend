package com.ensolvers.backend.note.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ensolvers.backend.note.model.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByArchivedOrderByUpdatedAtDesc(boolean archived);

    List<Note> findAllByUser_IdOrderByUpdatedAtDesc(Long userId);

    List<Note> findAllByUser_IdAndArchivedOrderByUpdatedAtDesc(Long userId, boolean archived);

    List<Note> findAllByCategories_NameOrderByUpdatedAtDesc(String categoryName);
}