package com.github.tripolkaandrey.mintnoteapi.dto;

import com.github.tripolkaandrey.mintnoteapi.entity.Directory;
import com.github.tripolkaandrey.mintnoteapi.entity.Note;

import java.util.List;

public class DirectoryContents {
    private final List<Directory> directories;
    private final List<Note> notes;

    public DirectoryContents(List<Directory> directories, List<Note> notes) {
        this.directories = directories;
        this.notes = notes;
    }

    public List<Directory> getDirectories() {
        return directories;
    }

    public List<Note> getNotes() {
        return notes;
    }
}