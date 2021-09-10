package org.localdevelopers.payfinderbatch.writer;

import org.localdevelopers.payfinderbatch.StepOperator;
import org.localdevelopers.payfinderbatch.domain.Store;
import org.localdevelopers.payfinderbatch.service.StoreService;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import java.util.List;
import java.util.stream.Collectors;

public class StoreWriter implements ItemStreamWriter<Store> {

    private final StoreService storeService;
    private final StepOperator stepOperator;

    public StoreWriter(final StoreService storeService, final StepOperator stepOperator) {
        if (isInvalidStepOperator(stepOperator))
            throw new IllegalArgumentException("StepOperator must be one of SAVE, DELETE");

        this.storeService = storeService;
        this.stepOperator = stepOperator;
    }

    private boolean isInvalidStepOperator(final StepOperator stepOperator) {
        return (stepOperator != StepOperator.SAVE
                && stepOperator != StepOperator.DELETE);
    }


    @Override
    public void write(List<? extends Store> items) {
        final List<Store> stores = items.stream()
                .map(it -> (Store) it)
                .collect(Collectors.toList());

        switch (stepOperator) {
            case SAVE:
                storeService.saveAll(stores);
                break;
            case DELETE:
                storeService.deleteAllById(
                        stores.stream()
                                .map(Store::getId)
                                .collect(Collectors.toList())
                );
                break;
        }
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
