package com.trekker.global.auth.dto.res;

public record GoogleRes(
        AuthResDto authResDto,
        boolean isCompleted
) {

}
