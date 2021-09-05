package org.localdevelopers.payfinderbatch.reader;

import lombok.RequiredArgsConstructor;
import org.localdevelopers.payfinderbatch.api.StoreItemApiService;
import org.localdevelopers.payfinderbatch.model.StoreItem;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.util.List;

@RequiredArgsConstructor
public class StoreItemReader implements ItemStreamReader<StoreItem> {

    private final StoreItemApiService storeItemApiService;
    private final String siGunCode;
    private List<StoreItem> items = null;
    private int index = 0;

    @Override
    public StoreItem read() {
        if (items == null)
            items = storeItemApiService.fetchBySiGunCode(siGunCode);
        return index < items.size() ? items.get(index++) : null;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }
}
