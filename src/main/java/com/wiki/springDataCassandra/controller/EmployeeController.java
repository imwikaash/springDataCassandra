package com.wiki.springDataCassandra.controller;

import com.wiki.springDataCassandra.entity.Employee;
import com.wiki.springDataCassandra.handler.EmployeeHandler;

import com.wiki.springDataCassandra.respository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.AccessType;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RestController
public class EmployeeController {

    @Autowired
    EmployeeHandler employeeService;

    @Autowired
    EmployeeRepository employeeRepository;

//    @PostConstruct
//    public void saveEmployees() {
//        List<Employee> employees = new ArrayList<>();
//        employees.add(new Employee( 123, "John Doe", "Delaware", "jdoe@xyz.com", 31));
//        employees.add(new Employee(324, "Adam Smith", "North Carolina", "asmith@xyz.com", 43));
//        employees.add(new Employee(355, "Kevin Dunner", "Virginia", "kdunner@xyz.com", 24));
//        employees.add(new Employee(643, "Mike Lauren", "New York", "mlauren@xyz.com", 41));
//        employeeService.insertEmployees(employees);
//    }

  //  @GetMapping("/list")
 //   public Flux getAllEmployees() {

//        Flux employees = employeeService.getAllEmployees();
//        employees.onErrorResume(e -> {
//            System.out.println("err"+e);
//        });
//        employees.doOnError(e -> {
//            System.out.println("err"+e);
//        }).log();
//
//        employees.subscribe(System.out::println,  // (9)
//
//                Throwable::printStackTrace);
//
//        //System.out.println(employees);
//
//
//        return ServerResponse.ok()
//                .contentType(APPLICATION_JSON)
//                .body(employees, Employee.class);
 //       return employees;
  //  }

//    @GetMapping("/test")
//    public String  getAllEmp() {
//
//
//        return "employees";
//    }
//
//    @GetMapping("/{id}")
//    public Mono<Employee> getEmployeeById(@PathVariable int id) {
//        return employeeService.getEmployeeById(id);
//    }
//
//    @GetMapping("/filterByAge/{age}")
//    public Flux<Employee> getEmployeesFilterByAge(@PathVariable int age) {
//        return employeeService.getEmployeesFilterByAge(age);
//    }

    @Bean
    RouterFunction<ServerResponse> routes(EmployeeHandler handler) {
        return nest(path("/emp"),
                nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)).or(accept(TEXT_EVENT_STREAM)),
                        route(GET("/"), handler::getAllEmployees)
                      //  .andRoute(POST("/create").and(contentType(APPLICATION_JSON)), handler::saveEmployee)
                                .andRoute(method(HttpMethod.POST), handler::saveEmployee)
                                .andRoute(DELETE("/"), handler::deleteAllEmployee)
                                  .andNest(path("/{id}"),
                                          route(method(HttpMethod.GET), handler::getEmployeeById)
                                                  .andRoute(method(HttpMethod.PUT),handler::updateEmployee)
                                                  .andRoute(method(HttpMethod.DELETE), handler::deleteEmployee)

                                )
                )
        );

    }

}
