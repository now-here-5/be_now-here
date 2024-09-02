package com.now_here5.now_here.domain.interaction.repository;

import jakarta.persistence.PersistenceException;

public class DatabaseOperationException extends Throwable {
    public DatabaseOperationException(String s, PersistenceException e) {
    }
}
