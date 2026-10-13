package com.weex.spring_playground.employee;

import java.time.Instant;
import java.util.List;

import com.weex.spring_playground.common.api.ApiSuccessResponse;
import com.weex.spring_playground.employee.dto.CreateEmployeeRequest;
import com.weex.spring_playground.employee.dto.EmployeeResponse;
import com.weex.spring_playground.employee.dto.UpdateEmployeeRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<EmployeeResponse>> create(
            @Valid @RequestBody CreateEmployeeRequest request,
            HttpServletRequest httpRequest
    ) {
        EmployeeResponse employee = employeeService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                response(HttpStatus.CREATED, "Employee created successfully", httpRequest, employee)
        );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<EmployeeResponse>>> getAll(
        HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(
                response(HttpStatus.OK, "Employees retrieved successfully", httpRequest, employeeService.getAll())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<EmployeeResponse>> getById(
        @PathVariable Long id,
        HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(
                response(HttpStatus.OK, "Employee retrieved successfully", httpRequest, employeeService.getById(id))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<EmployeeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(
            response(HttpStatus.OK, "Employee updated successfully", httpRequest, employeeService.update(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> delete(
        @PathVariable Long id,
        HttpServletRequest httpRequest
    ) {
        employeeService.delete(id);

        return ResponseEntity.ok(
            response(HttpStatus.OK, "Employee deleted successfully", httpRequest, null)
        );
    }

    private <T> ApiSuccessResponse<T> response(
        HttpStatus status,
        String message,
        HttpServletRequest request,
        T data
    ) {
        return new ApiSuccessResponse<>(
            Instant.now().toString(),
            status.value(),
            message,
            request.getRequestURI(),
            data
        );
    }
}
