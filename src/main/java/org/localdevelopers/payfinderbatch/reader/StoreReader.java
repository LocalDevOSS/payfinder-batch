package org.localdevelopers.payfinderbatch.reader;

import org.localdevelopers.payfinderbatch.StepOperator;
import org.localdevelopers.payfinderbatch.domain.Store;
import org.localdevelopers.payfinderbatch.service.StoresFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.util.List;

public class StoreReader implements ItemStreamReader<Store> {

    private static final Logger logger = LoggerFactory.getLogger(StoreReader.class);

    private final StoresFactory storesFactory;
    private final StepOperator stepOperator;
    private List<Store> stores = null;
    private int index = 0;

    public StoreReader(final StoresFactory storesFactory, final StepOperator stepOperator) {
        if (isInvalidStepOperator(stepOperator))
            throw new IllegalArgumentException("StepOperator must be one of SAVE, DELETE");

        this.storesFactory = storesFactory;
        this.stepOperator = stepOperator;
    }

    private boolean isInvalidStepOperator(final StepOperator stepOperator) {
        return (stepOperator != StepOperator.SAVE
                && stepOperator != StepOperator.DELETE);
    }

    @Override
    public Store read() {
        if (stores == null) {
            switch (stepOperator) {
                case SAVE:
                    stores = storesFactory.getSaveStores();
                    break;
                case DELETE:
                    stores = storesFactory.getDeleteStores();
                    break;
            }
            logger.info("Operator: {}, size of stores: {}", stepOperator.name(), stores.size());
        }
        return index < stores.size() ? stores.get(index++) : null;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
        initialize();
    }

    private void initialize() {
        stores = null;
        index = 0;
        logger.info("item reader was initialized");
    }
}
