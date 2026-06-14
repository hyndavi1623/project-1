package com.example.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class EmployeeApplication {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeApplication(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }

    @GetMapping("/")
    public String home() {

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Employee Management</title>

            <style>
                body{
                    font-family:Arial;
                    background:#f4f4f4;
                    margin:20px;
                }

                .container{
                    max-width:800px;
                    margin:auto;
                }

                .card{
                    background:white;
                    padding:15px;
                    margin:10px 0;
                    border-radius:8px;
                    box-shadow:0 2px 5px rgba(0,0,0,.1);
                }

                input{
                    width:100%;
                    padding:10px;
                    margin:5px 0;
                }

                button{
                    width:100%;
                    padding:10px;
                    background:#007bff;
                    color:white;
                    border:none;
                    cursor:pointer;
                }

                h1{
                    text-align:center;
                }
            </style>
        </head>

        <body>

            <div class="container">

                <h1>Employee Management System</h1>

                <div class="card">

                    <input id="name" placeholder="Employee Name">

                    <input id="department" placeholder="Department">

                    <input id="salary" placeholder="Salary">

                    <button onclick="addEmployee()">
                        Add Employee
                    </button>

                </div>

                <div id="employees"></div>

            </div>

            <script>

                async function loadEmployees(){

                    let response =
                        await fetch('/api/employees');

                    let employees =
                        await response.json();

                    let html='';

                    employees.forEach(emp => {

                        html += `
                        <div class="card">

                            <h3>${emp.name}</h3>

                            <p>
                                Department:
                                ${emp.department}
                            </p>

                            <p>
                                Salary:
                                $${emp.salary}
                            </p>

                        </div>`;
                    });

                    document.getElementById('employees')
                        .innerHTML = html;
                }

                async function addEmployee(){

                    let employee = {

                        name:
                            document.getElementById('name').value,

                        department:
                            document.getElementById('department').value,

                        salary:
                            document.getElementById('salary').value
                    };

                    await fetch('/api/employees',{

                        method:'POST',

                        headers:{
                            'Content-Type':
                            'application/json'
                        },

                        body:
                            JSON.stringify(employee)
                    });

                    loadEmployees();
                }

                loadEmployees();

            </script>

        </body>
        </html>
        """;
    }

    @GetMapping("/api/employees")
    public List<Map<String,Object>> getEmployees() {

        return jdbcTemplate.queryForList(
            "SELECT * FROM employees ORDER BY id DESC"
        );
    }

    @PostMapping("/api/employees")
    public String addEmployee(
            @RequestBody EmployeeRequest request) {

        jdbcTemplate.update(
            """
            INSERT INTO employees
            (name, department, salary)
            VALUES (?, ?, ?)
            """,
            request.name,
            request.department,
            request.salary
        );

        return "Employee Added";
    }

    static class EmployeeRequest {
        public String name;
        public String department;
        public Double salary;
    }
}
