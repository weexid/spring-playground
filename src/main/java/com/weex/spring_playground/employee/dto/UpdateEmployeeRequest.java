package com.weex.spring_playground.employee.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeRequest(
        @NotBlank @Size(max = 50) String employeeNumber,
        @NotBlank @Size(max = 150) String fullName,
        @Size(max = 30) String phone,
        String address,
        LocalDate birthDate,
        LocalDate joinDate,
        @Size(max = 100) String department,
        @Size(max = 100) String position,
        @Size(max = 30) String employmentStatus
) {
}
