package com.autohome.app.cars.common.utils;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author chengjincheng
 * @date 2024/4/28
 */
public class EsPageUtil {

    /**
     * Search for hit.
     *
     * @param searchAfterKey the search after key
     * @param size           the page size
     * @param searchRequest  the search request
     * @param consumer       the consumer of hits
     */
    public static void searchForHit(RestHighLevelClient client,
                                    String searchAfterKey,
                                    int size,
                                    SearchRequest searchRequest,
                                    Consumer<SearchHit[]> consumer) throws IOException {
        searchForResponse(client, searchAfterKey, size, searchRequest,
                searchResponse -> forEachHits(searchResponse, consumer));
    }

    /**
     * Search for response.
     *
     * @param searchRequest the search request
     * @param consumer      the consumer of response
     */
    public static void searchForResponse(RestHighLevelClient client,
                                         String searchAfterKey,
                                         int size,
                                         SearchRequest searchRequest,
                                         Consumer<SearchResponse> consumer) throws IOException {
        if (searchRequest == null || consumer == null) {
            return;
        }
        SearchSourceBuilder sourceBuilder = searchRequest.source();
        // 在构建查询条件时，即可设置大小
        sourceBuilder.size(size);
        // 设置排序字段
        sourceBuilder.sort(searchAfterKey, SortOrder.ASC);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        while (searchHits.length > 0) {
            consumer.accept(searchResponse);
            SearchHit last = searchHits[searchHits.length - 1];
            sourceBuilder.searchAfter(last.getSortValues());
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            searchHits = searchResponse.getHits().getHits();
        }
    }

    public static void forEachHits(SearchResponse searchResponse, Consumer<SearchHit[]> consumer) {
        if (searchResponse == null) {
            return;
        }
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        consumer.accept(searchHits);
    }
}
