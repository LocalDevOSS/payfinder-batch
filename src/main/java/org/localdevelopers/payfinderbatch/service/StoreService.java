package org.localdevelopers.payfinderbatch.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.localdevelopers.payfinderbatch.domain.Store;
import org.localdevelopers.payfinderbatch.repository.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public List<Store> findAllBySiGunCode(final String siGunCode) {
        return storeRepository.findAllBySiGunCode(siGunCode);
    }

    public void saveAll(final List<Store> stores) {
        storeRepository.saveAll(stores);
    }

    public void deleteAllById(final List<ObjectId> ids) {
        storeRepository.deleteAllById(ids);
    }
}
