package org.localdevelopers.payfinderbatch.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.localdevelopers.payfinderbatch.api.error.ResponseCode;
import org.localdevelopers.payfinderbatch.model.StoreItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StoreItemApiService {
    private static final Logger logger = LoggerFactory.getLogger(StoreItemApiService.class);
    private static final String ERROR_STATUS = "ERROR";
    private static final String SUCCESS_CODE = "000";
    private static final String INVALID_PATH_ERROR_MESSAGE = "유효하지 않은 경로입니다.";

    @Value("${open.api.key}")
    private String key;

    @Value("${open.api.store.url}")
    private String url;

    @Value("${open.api.store.type}")
    private String type;

    @Value("${open.api.store.pSize}")
    private int pageSize;

    private int totalPageCount;

    public List<StoreItem> fetchAll() {
        List<StoreItem> items = new ArrayList<>();
        totalPageCount = 1;

        logger.info("===== start loading the store items from openapi =====");
        for (int pIndex = 1; pIndex <= totalPageCount; ++pIndex) {
            items.addAll(
                    fetch(key, url, type, pIndex, pageSize)
            );
            logger.info("fetch progress (page): {} / {}", pIndex, totalPageCount);
        }
        logger.info("===== finished loading the store items from openapi =====");
        return items;
    }

    private List<StoreItem> fetch(final String key, final String url, final String type, final int pageIndex, final int pageSize) {
        List<StoreItem> items = Collections.emptyList();
        try {
            URI uri = makeURI(url, key, type, pageIndex, pageSize);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            JsonNode tree = getTree(response.getBody());
//            totalPageCount = parseTotalPageCount(tree);
            totalPageCount = 2;
            items = parseStoreItems(tree);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            return items;
        }
    }

    private URI makeURI(final String url, final String key, final String type, final int pageIndex, final int pageSize) throws URISyntaxException {
        final StringBuilder stringBuilder = new StringBuilder(url)
                .append("?key=")
                .append(key)
                .append("&type=")
                .append(type)
                .append("&pIndex=")
                .append(pageIndex)
                .append("&pSize=")
                .append(pageSize);
        return new URI(stringBuilder.toString());
    }

    private JsonNode getTree(final String body) throws JsonProcessingException {
        JsonNode tree = new ObjectMapper().readTree(body);
        throwExceptionIfTreeHasError(tree);
        return tree;
    }

    private void throwExceptionIfTreeHasError(final JsonNode tree) {
        final ResponseCode responseCode = parseResponseCode(tree);
        if (responseCode.getStatus().equals(ERROR_STATUS) || !responseCode.getCode().equals(SUCCESS_CODE))
            throw new IllegalStateException(responseCode.getDescription());
    }

    private ResponseCode parseResponseCode(final JsonNode tree) {
        final JsonNode code = tree.path("RegionMnyFacltStus")
                .path(0)
                .path("head")
                .path(1)
                .path("RESULT")
                .path("CODE");
        throwParseExceptionIfIsInvalidPath(code);

        return ResponseCode.valueOf(
                code.asText()
                        .replace('-', '_'));
    }

    private void throwParseExceptionIfIsInvalidPath(final JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull())
            throw new ParseException(INVALID_PATH_ERROR_MESSAGE);
    }

    private int parseTotalPageCount(final JsonNode tree) {
        final JsonNode totalItemCount = tree.path("RegionMnyFacltStus")
                .path(0)
                .path("head")
                .path(0)
                .path("list_total_count");
        throwParseExceptionIfIsInvalidPath(totalItemCount);

        int totalPageCount = (int) (totalItemCount.asLong() / pageSize);
        return (totalItemCount.asLong() % pageSize != 0) ? totalPageCount + 1 : totalPageCount;
    }

    private List<StoreItem> parseStoreItems(final JsonNode tree) throws IOException {
        JsonNode items = tree.path("RegionMnyFacltStus")
                .path(1)
                .path("row");
        throwParseExceptionIfIsInvalidPath(items);

        return new ObjectMapper().readerFor(
                new TypeReference<List<StoreItem>>() {
                }).readValue(items);
    }
}
