package org.localdevelopers.payfinderbatch.processor;

import org.localdevelopers.payfinderbatch.domain.Store;
import org.localdevelopers.payfinderbatch.model.StoreItem;
import org.springframework.batch.item.ItemProcessor;

public class StoreItemProcessor implements ItemProcessor<StoreItem, Store> {

    @Override
    public Store process(StoreItem item) {
        return transform(item);
    }

    private Store transform(StoreItem item) {
        return Store.builder()
                .name(item.getName())
                .type(item.getType())
                .lotNumberAddress(item.getLotNumberAddress())
                .roadNameAddress(item.getRoadNameAddress())
                .createAt(item.getCreateAt())
                .zipCode(item.getZipCode())
                .latitude(item.getLatitude())
                .longitude(item.getLongitude())
                .siGunCode(item.getSiGunCode())
                .siGunName(item.getSiGunName())
                .build();
    }
}
