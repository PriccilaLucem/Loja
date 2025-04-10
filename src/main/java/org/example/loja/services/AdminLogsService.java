package org.example.loja.services;

import org.example.loja.entities.AdminLogsEntity;
import org.example.loja.inteface.LoggableUser;
import org.example.loja.repository.StoreLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminLogsService {

    @Autowired
    StoreLogsRepository logsRepository;

    public void saveLogAction(LoggableUser user, String action, String description, double lat, double lon) {
        AdminLogsEntity log = new AdminLogsEntity();
        log.setUserType(user.getRoles().get(0).getName());
        log.setAction(action);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());
        log.setLat(lat);
        log.setLon(lon);
        logsRepository.save(log);
    }
}
