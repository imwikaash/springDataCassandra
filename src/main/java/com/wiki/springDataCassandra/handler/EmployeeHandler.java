package com.wiki.springDataCassandra.handler;

import com.wiki.springDataCassandra.entity.Employee;
import com.wiki.springDataCassandra.respository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;


@Service
public class EmployeeHandler {

    @Autowired
    private EmployeeRepository  employeeRepository;

    public EmployeeHandler(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    public Mono<ServerResponse> getAllEmployees(ServerRequest request) {

        Flux<Employee> employees = employeeRepository.findAll();

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(employees, Employee.class);

    }

    public Mono<ServerResponse> getEmployeesFilterByAge(int age) {

        Flux<Employee> employees = employeeRepository.findByAgeGreaterThan(age);

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(employees, Employee.class);
    }

    public Mono<ServerResponse> getEmployeeById(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Employee> employee = employeeRepository.findById(Integer.parseInt(id));

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(employee, Employee.class);
    }

    public Mono<ServerResponse> saveEmployee(ServerRequest request) {
        Mono<Employee> productMono = request.bodyToMono(Employee.class);

        return productMono.flatMap(product ->
                ServerResponse.status(HttpStatus.CREATED)
                        .contentType(APPLICATION_JSON)
                        .body(employeeRepository.save(product), Employee.class));


    }

    public Mono<ServerResponse> updateEmployee(ServerRequest request) {
        int id = Integer.parseInt(request.pathVariable("id"));
        Mono<Employee> existingEmpMono = this.employeeRepository.findById(id);
        Mono<Employee> employeeMonoMono = request.bodyToMono(Employee.class);

        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return employeeMonoMono.zipWith(existingEmpMono,
                (employee, existingEmployee) ->
                        new Employee(existingEmployee.getId(), employee.getName(),employee.getAddress(),employee.getEmail(), employee.getAge())
        )
                .flatMap(employee ->
                        ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(employeeRepository.save(employee), Employee.class)
                ).switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> deleteEmployee(ServerRequest request) {
        int id = Integer.parseInt(request.pathVariable("id"));

        Mono<Employee> employeeMono = this.employeeRepository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return employeeMono
                .flatMap(existingProduct ->
                        ServerResponse.ok()
                                .build(employeeRepository.delete(existingProduct))
                )
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> deleteAllEmployee(ServerRequest request) {
        return ServerResponse.ok()
                .build(employeeRepository.deleteAll());
    }


}
