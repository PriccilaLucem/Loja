package org.example.loja.inteface;

import org.example.loja.entities.RoleEntity;

import java.util.List;
import java.util.UUID;

public interface LoggableUser {
    UUID getId();
    String getName();
    String getEmail();
    List<RoleEntity> getRoles();
}
