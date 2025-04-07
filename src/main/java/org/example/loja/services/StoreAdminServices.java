package org.example.loja.services;

import org.apache.catalina.Store;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.repository.StoreAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StoreAdminServices {

    @Autowired
    private  StoreAdminRepository storeAdminRepository;

    @Autowired
    AdminLogsService adminLogsService;

    // Renamed method for clarity and purpose
    public  List<StoreAdminEntity> getAllStoreAdmins() {
        return storeAdminRepository.findAll();
    }

    // Simplified and cleaned save logic by extracting validation
    public UUID saveStoreAdmin(StoreAdminEntity storeAdminEntity, double lat, double lon) throws IllegalArgumentException{
        validateStoreAdmin(storeAdminEntity);
        StoreAdminEntity newAdmin = storeAdminRepository.save(storeAdminEntity);
        adminLogsService.saveLogAction(newAdmin, "Created", "New Store Admin created", lat, lon);
        return newAdmin.getId();
    }

    public StoreAdminEntity getStoreAdminById(UUID uuid) throws IllegalArgumentException{
        return storeAdminRepository.findById(uuid).orElseThrow(() -> new IllegalArgumentException("Store Admin not found"));
    }
    public boolean deleteStoreAdmin(UUID uuid) throws IllegalArgumentException{
        int updated = storeAdminRepository.updateStatus(uuid, false);
        return updated > 0;
    }

    /**
     * Validates the given StoreAdmin object to ensure that all required fields are populated
     * and properly formatted.
     *
     * @param storeAdminEntity the StoreAdmin object to validate
     * @throws IllegalArgumentException if the StoreAdmin is null, the name is null or blank,
     *         the email is null, blank, or improperly formatted, or the password is null or blank
     */
    private void validateStoreAdmin(StoreAdminEntity storeAdminEntity) throws IllegalArgumentException{
        if(storeAdminEntity == null){
            throw new IllegalArgumentException("Store Admin is null");
        }
        if(storeAdminEntity.getName() == null || storeAdminEntity.getName().isBlank()){
            throw new IllegalArgumentException("Store Admin name is null or blank");
        }
        String REGEX_EMAIL_PATTERN = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(storeAdminEntity.getEmail() == null || storeAdminEntity.getEmail().isBlank() || !storeAdminEntity.getEmail().matches(REGEX_EMAIL_PATTERN) ){
            throw new IllegalArgumentException("Store Admin email is null or blank");
        }
        if(storeAdminEntity.getPassword() == null || storeAdminEntity.getPassword().isBlank()){
            throw new IllegalArgumentException("Store Admin password is null or blank");
        }

    }
}