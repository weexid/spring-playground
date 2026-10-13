package com.weex.spring_playground.employee;

import java.util.List;

import com.weex.spring_playground.common.exception.ConflictException;
import com.weex.spring_playground.employee.dto.CreateEmployeeRequest;
import com.weex.spring_playground.employee.dto.EmployeeResponse;
import com.weex.spring_playground.employee.dto.UpdateEmployeeRequest;
import com.weex.spring_playground.user.User;
import com.weex.spring_playground.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            UserRepository userRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    public EmployeeResponse create(CreateEmployeeRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validateEmployeeNumberIsAvailable(request.employeeNumber(), null);

        Employee employee = new Employee(
                user,
                request.employeeNumber().trim(),
                request.fullName().trim()
        );
        applyFields(employee, request);

        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return EmployeeResponse.from(findEmployee(id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {
        Employee employee = findEmployee(id);

        validateEmployeeNumberIsAvailable(request.employeeNumber(), id);

        employee.setEmployeeNumber(request.employeeNumber().trim());
        employee.setFullName(request.fullName().trim());
        employee.setPhone(request.phone());
        employee.setAddress(request.address());
        employee.setBirthDate(request.birthDate());
        employee.setJoinDate(request.joinDate());
        employee.setDepartment(request.department());
        employee.setPosition(request.position());
        employee.setEmploymentStatus(request.employmentStatus());

        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    public void delete(Long id) {
        Employee employee = findEmployee(id);
        employeeRepository.delete(employee);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
    }

    private void validateEmployeeNumberIsAvailable(String employeeNumber, Long employeeId) {
        employeeRepository.findByEmployeeNumber(employeeNumber.trim())
                .filter(existing -> !existing.getId().equals(employeeId))
                .ifPresent(existing -> {
                    throw new ConflictException("Employee number already used");
                });
    }

    private void applyFields(Employee employee, CreateEmployeeRequest request) {
        employee.setPhone(request.phone());
        employee.setAddress(request.address());
        employee.setBirthDate(request.birthDate());
        employee.setJoinDate(request.joinDate());
        employee.setDepartment(request.department());
        employee.setPosition(request.position());
        employee.setEmploymentStatus(request.employmentStatus());
    }
}
