package org.example.loja.inteface;

import java.util.List;

public interface LoggableUser {
    Long getId();
    String getName();
    String getEmail();
    List<String> getRole();
}
