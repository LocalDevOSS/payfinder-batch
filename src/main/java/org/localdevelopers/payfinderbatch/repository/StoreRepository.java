package org.localdevelopers.payfinderbatch.repository;

import org.bson.types.ObjectId;
import org.localdevelopers.payfinderbatch.domain.Store;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StoreRepository extends MongoRepository<Store, ObjectId> {
    List<Store> findAllBySiGunCode(String siGunCode);
}
