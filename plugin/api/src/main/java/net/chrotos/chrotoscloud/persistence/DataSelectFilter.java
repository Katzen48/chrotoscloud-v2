package net.chrotos.chrotoscloud.persistence;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class DataSelectFilter {
    @Builder.Default
    Map<String, Object> columnFilters = new HashMap<>();
    Object primaryKeyValue;
    String orderKey;
    @Builder.Default
    Ordering ordering = Ordering.ASCENDING;
    @Builder.Default
    int pageSize = 0;
    @Builder.Default
    int first = 0;
}
