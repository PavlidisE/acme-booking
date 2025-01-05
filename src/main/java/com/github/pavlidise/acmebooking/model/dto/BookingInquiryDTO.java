package com.github.pavlidise.acmebooking.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingInquiryDTO (

        @NotBlank
        String roomName,

        @NotNull
        LocalDate date
){
}
