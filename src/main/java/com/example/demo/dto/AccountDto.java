package com.example.demo.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    @NotEmpty(message = "Bank name must not be empty")
    private String name;
    @NotNull (message = "IBAN must not be null")
    @Min(value = 1, message = "IBAN must be a positive integer")
    private Integer iban;
    private Double balance;
    private String description;
}
