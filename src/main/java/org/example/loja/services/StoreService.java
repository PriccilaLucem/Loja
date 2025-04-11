package org.example.loja.services;

import org.example.loja.entities.ProductEntity;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.entities.StoreEntity;
import org.example.loja.repository.StoreAdminRepository;
import org.example.loja.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.NumberParseException;

import java.util.UUID;

@Service
public class StoreService {

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreAdminRepository storeAdminRepository;

    public Long saveStore(StoreEntity store, UUID storeAdmin) throws IllegalArgumentException {
        if (storeAdmin == null) {
            throw new IllegalArgumentException("StoreAdmin UUID cannot be null");
        }

        store.setStoreAdmin(storeAdminRepository.findById(storeAdmin)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Credentials")));

        validateStore(store);

        try {
            return storeRepository.save(store).getId();
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while saving the store: " + e.getMessage());
        }
    }

    public boolean deleteStore(Long id) throws IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Store ID must be a valid positive number");
        }

        int affectedRows = storeRepository.deactivateStoreById(id);
        return affectedRows > 0;
    }

    public boolean updateStore(StoreEntity store) throws IllegalArgumentException {
        validateStore(store);

        try {
            int affectedRows = storeRepository.saveStoreAndReturnAffectedRows(store);
            return affectedRows > 0;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while updating the store: " + e.getMessage());
        }
    }

    public boolean addProductsToStore(Long storeId, ProductEntity product) throws IllegalArgumentException {
        StoreEntity store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("Store not found"));
        store.addProduct(product);
        storeRepository.save(store);
        return true;
    }

    private void validateStore(StoreEntity store) throws IllegalArgumentException {

            if (store.getDescription() == null || store.getDescription().length() > 250) {
                throw new IllegalArgumentException("Store Description is null or too long");
            }

            if (store.getStoreAdmin() == null || store.getStoreAdmin().getId() == null) {
                throw new IllegalArgumentException("Store Admin or Admin ID is null");
            }

            if (store.getLocations() == null || store.getLocations().isEmpty()) {
                throw new IllegalArgumentException("Store Locations are null or empty");
            }

            if (store.getName() == null) {
                throw new IllegalArgumentException("Store Name is null");
            }

            if (store.getEmail() == null ||
                    !store.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                throw new IllegalArgumentException("Store Email is either null or invalid");
            }
            if (store.getPhone() == null) {
                throw new IllegalArgumentException("Store Phone is null");
            }
            try {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(store.getPhone(), "US"); // Define região padrão como "US"
                if (!phoneUtil.isValidNumber(phoneNumber)) {
                    throw new IllegalArgumentException(store.getPhone() + " is not a valid phone number");
                }
            } catch (NumberParseException e) {
                throw new IllegalArgumentException("Error parsing phone number: " + e.getMessage());
            }
        }


    public boolean verifyIfIsAuthorized(UUID uuid, Long id) throws IllegalArgumentException {
        if (uuid == null || id == null) {
            throw new IllegalArgumentException("UUID or Store ID cannot be null");
        }

        StoreAdminEntity storeAdmin = storeAdminRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Credentials"));

        return storeAdmin.getManagedStore() != null && storeAdmin.getManagedStore().stream()
                .anyMatch(store -> store != null && store.getId() != null && store.getId().equals(id));
    }
}