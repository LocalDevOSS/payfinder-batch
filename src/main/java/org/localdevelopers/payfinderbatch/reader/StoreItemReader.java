package org.localdevelopers.payfinderbatch.reader;

import lombok.RequiredArgsConstructor;
import org.localdevelopers.payfinderbatch.api.StoreItemApiService;
import org.localdevelopers.payfinderbatch.model.StoreItem;
import org.springframework.batch.item.ItemReader;

import java.util.List;

@RequiredArgsConstructor
public class StoreItemReader implements ItemReader<StoreItem> {
    private final StoreItemApiService storeItemApiService;
    private List<StoreItem> items = null;
    private int index = 0;

    @Override
    public StoreItem read() {
        if (items == null)
            items = storeItemApiService.fetchAll();
        return index < items.size() ? items.get(index++) : null;
    }
}
