package net.chrotos.chrotoscloud.rest.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Page {
    int from;
    @SerializedName("per_page")
    int perPage;
    int to;
}
