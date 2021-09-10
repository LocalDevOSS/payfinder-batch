package org.localdevelopers.payfinderbatch.service;

import lombok.RequiredArgsConstructor;
import org.localdevelopers.payfinderbatch.api.StoreItemApiService;
import org.localdevelopers.payfinderbatch.domain.Store;
import org.localdevelopers.payfinderbatch.model.StoreItem;
import org.localdevelopers.payfinderbatch.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StoresFactory {

    private static final Logger logger = LoggerFactory.getLogger(StoresFactory.class);

    private final StoreItemApiService storeItemApiService;
    private final StoreService storeService;

    private List<Store> saveStores;
    private List<Store> deleteStores;

    public void loadStores(String siGunCode) {
        saveStores = Collections.emptyList();
        deleteStores = Collections.emptyList();

        logger.info("start loading the store items where siGunCode is {}", siGunCode);
        final List<Store> newStores = storeItemApiService.fetchBySiGunCode(siGunCode)
                .stream()
                .filter(StoreItem::isValid)
                .map(this::transform)
                .distinct()
                .collect(Collectors.toList());
        final List<Store> oldStores = storeService.findAllBySiGunCode(siGunCode);

        newStores.forEach(newStore ->   // 디비에 동일한 데이터가 있으면 후처리 필드를 복사
                oldStores.stream()
                        .filter(newStore::equals)
                        .findAny()
                        .ifPresent(newStore::update));

        saveStores = newStores.stream() // 새 데이터 또는 업데이트가 필요한 데이터만 필터링
                .filter(newStore -> {
                    if (newStore.getId() == null)    // 새 데이터
                        return true;
                    Optional<Store> store = oldStores.stream()
                            .filter(oldStore ->
                                    oldStore.getId() == newStore.getId())
                            .findAny();
                    return store.isPresent() && newStore.requireUpdate(store.get());    // 업데이트가 필요한 데이터
                }).collect(Collectors.toList());

        deleteStores = ListUtils.getDifference(oldStores, newStores);

        long insertCount = saveStores.stream()
                .filter(it -> it.getId() == null)
                .count();
        logger.info("finished loading the store items where siGunCode is {}", siGunCode);
        logger.info("loaded: {}, insert: {}, update: {}, delete: {}", newStores.size(), insertCount, saveStores.size() - insertCount, deleteStores.size());
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

    public List<Store> getSaveStores() {
        return saveStores;
    }

    public List<Store> getDeleteStores() {
        return deleteStores;
    }
}
