package net.chrotos.chrotoscloud.rest.response;

import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class PagedResponse<T> extends Response<List<T>> {
    private final Page pagination;

    public PagedResponse(@NonNull List<T> data, int from, int pageSize) {
        super(data);
        this.pagination = new Page(from, pageSize, from + data.size() - 1);
    }
}
