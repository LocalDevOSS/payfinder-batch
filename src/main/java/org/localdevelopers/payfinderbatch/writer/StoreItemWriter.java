package org.localdevelopers.payfinderbatch.writer;

import com.mongodb.client.result.UpdateResult;
import org.localdevelopers.payfinderbatch.domain.Store;
import org.localdevelopers.payfinderbatch.domain.Store.StoreFields;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class StoreItemWriter extends MongoItemWriter<Store> {

    public StoreItemWriter(MongoTemplate mongoTemplate) {
        setTemplate(mongoTemplate);
    }

    @Override
    protected void doWrite(List<? extends Store> items) {
        MongoOperations mongoOperations = getTemplate();
        for (Store item : items) {
            UpdateResult updateResult = mongoOperations.updateFirst(
                    makeQuery(item),
                    makeUpdate(item),
                    Store.class);
            if (updateResult.getMatchedCount() == 0)
                mongoOperations.save(item);
        }
    }

    private Query makeQuery(Store store) {
        return new Query()
                .addCriteria(Criteria.where(StoreFields.NAME.getName())
                        .is(store.getName()))
                .addCriteria(Criteria.where(StoreFields.LATITUDE.getName())
                        .is(store.getLatitude()))
                .addCriteria(Criteria.where(StoreFields.LONGITUDE.getName())
                        .is(store.getLongitude()));
    }

    private Update makeUpdate(Store store) {
        return new Update()
                .set(StoreFields.TYPE.getName(),
                        store.getType())
                .set(StoreFields.LOT_NUMBER_ADDRESS.getName(),
                        store.getLotNumberAddress())
                .set(StoreFields.ROAD_NAME_ADDRESS.getName(),
                        store.getRoadNameAddress())
                .set(StoreFields.CREATE_AT.getName(),
                        store.getCreateAt())
                .set(StoreFields.ZIP_CODE.getName(),
                        store.getZipCode())
                .set(StoreFields.SI_GUN_CODE.getName(),
                        store.getSiGunCode())
                .set(StoreFields.SI_GUN_NAME.getName(),
                        store.getSiGunName());
    }
}
