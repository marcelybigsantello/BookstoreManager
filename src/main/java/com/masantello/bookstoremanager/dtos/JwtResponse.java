package com.masantello.bookstoremanager.dtos;

import lombok.Builder;

@Builder
public record JwtResponse(String jwtToken) {

}

