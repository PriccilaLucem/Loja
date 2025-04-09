package org.example.loja.services;

import org.example.loja.dto.StoreManagerDTO;
import org.example.loja.entities.StoreEntity;
import org.example.loja.entities.StoreManagerEntity;
import org.example.loja.repository.StoreManagerRepository;
import org.example.loja.repository.StoreRepository;
import org.example.loja.util.CPFValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StoreManagerService {
    @Autowired
    private StoreManagerRepository storeManagerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Transactional
    public boolean dissociateStoreManager(UUID managerId) {
        StoreManagerEntity manager = storeManagerRepository.findById(managerId).orElseThrow();
        StoreEntity store = manager.getStore();

        if (store != null) {
            store.setStoreManager(null);
            manager.setStore(null);
            manager.setStatus(false);
            storeRepository.save(store);
            storeManagerRepository.save(manager);
            return true;
        }
        return false;
    }

    public UUID saveStoreManager(StoreManagerDTO storeManagerDTO){
        StoreManagerEntity storeManager = new StoreManagerEntity();
        storeManager.setName(storeManagerDTO.getName());
        storeManager.setEmail(storeManagerDTO.getEmail());
        storeManager.setCpf(storeManagerDTO.getCpf());
        storeManager.setPassword(storeManagerDTO.getPassword());

        validateStoreManager(storeManager);
        storeManager.setActive(false);
        storeManager.setPassword(org.example.loja.util.Authorization.hashPassword(storeManager.getPassword()));
        storeManager.setStatus(true);
        storeManager.setStore(storeRepository.findById(storeManagerDTO.getStoreId()).orElseThrow(() -> new IllegalArgumentException("Invalid Store")));
        return storeManagerRepository.save(storeManager).getId();
    }

    public StoreManagerEntity getStoreIdByStoreManagerEmail(String email){
        return  storeManagerRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid Store Manager"));
    }

    public boolean activateStoreManager(UUID id, Long storeId){
        int isUpdated = storeManagerRepository.updateStatusToTrue(id, storeId);

        return isUpdated > 0;
    }

    public boolean deleteStoreManager(UUID id){
        int affectedRows = storeManagerRepository.deleteStoreManagerById(id);
        return affectedRows > 0;
    }

    public boolean verifyIfIsAuthorized(UUID uuid, Long storeId){
        StoreManagerEntity storeManager = storeManagerRepository.findById(uuid).orElseThrow(() -> new IllegalArgumentException("Invalid Store Manager"));
        return storeManager.getStore() != null && storeManager.getStore().getId().equals(storeId);
    }
    public boolean updateManagedStore(UUID uuid, Long storeId){
        StoreManagerEntity storeManager = storeManagerRepository.findById(uuid).orElseThrow(() -> new IllegalArgumentException("Invalid Store Manager"));
        storeManager.setStore(storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("Invalid Store")));
        storeManagerRepository.save(storeManager);
        return true;
    }


    private void validateStoreManager(StoreManagerEntity storeManager){
        if(storeManager == null){
            throw new IllegalArgumentException("Invalid Store Manager");
        }
        if(storeManager.getName().isBlank() || storeManager.getEmail().isBlank()){
            throw new IllegalArgumentException("Invalid Store Manager");
        }
        if(storeManager.getPassword().isBlank() || storeManager.getPassword().length() < 6){
            throw new IllegalArgumentException("Invalid Store Manager");
        }
        String REGEX_EMAIL_PATTERN = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(storeManager.getEmail().isBlank() || !storeManager.getEmail().matches(REGEX_EMAIL_PATTERN) ){
            throw new IllegalArgumentException("Store Admin email is null or blank");
        }
        if(!CPFValidator.isValidCPF(storeManager.getCpf())){
            throw new IllegalArgumentException("Invalid Store Manager");
        }

    }
}
