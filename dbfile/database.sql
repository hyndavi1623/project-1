CREATE DATABASE employee_db;

USE employee_db;

CREATE TABLE employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    salary DECIMAL(10,2)
);

INSERT INTO employees(name, department, salary)
VALUES
('John Smith', 'IT', 55000),
('Sarah Johnson', 'HR', 48000);
