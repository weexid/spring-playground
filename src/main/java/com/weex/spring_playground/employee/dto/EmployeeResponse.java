package com.weex.spring_playground.employee.dto;

import java.time.LocalDate;

import com.weex.spring_playground.employee.Employee;

public record EmployeeResponse(
        Long id,
        Long userId,
        String employeeNumber,
        String fullName,
        String phone,
        String address,
        LocalDate birthDate,
        LocalDate joinDate,
        String department,
        String position,
        String employmentStatus
) {

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getUser().getId(),
                employee.getEmployeeNumber(),
                employee.getFullName(),
                employee.getPhone(),
                employee.getAddress(),
                employee.getBirthDate(),
                employee.getJoinDate(),
                employee.getDepartment(),
                employee.getPosition(),
                employee.getEmploymentStatus()
        );
    }
}
