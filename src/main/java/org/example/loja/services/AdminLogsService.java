package org.example.loja.services;

import org.example.loja.entities.AdminLogsEntity;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.repository.StoreAdminLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminLogsService {

    @Autowired
    StoreAdminLogsRepository logsRepository;

    public void saveLogAction(StoreAdminEntity admin, String action, String description, double lat, double lon) {
        AdminLogsEntity log = new AdminLogsEntity();
        log.setStoreAdmin(admin);
        log.setAction(action);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());
        log.setLat(lat);
        log.setLon(lon);
        logsRepository.save(log);
    }
}
