package com.github.pavlidise.acmebooking.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingInquiryDTO (

        @NotBlank
        String roomName,

        @NotNull
        @FutureOrPresent
        LocalDate date
){
}
