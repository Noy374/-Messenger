package com.example.messenger.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AddFriendRequest {

    @Schema(description = "Имя другка " +
            "(Friend's name)"
            , example = "Oleg")
    String friendUsername;
}
