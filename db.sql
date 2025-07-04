-- 0) Crear y seleccionar la base de datos
CREATE DATABASE
IF NOT EXISTS bank;
USE bank;

-- 1) Deshabilitar comprobaciones de FK para poder borrar en cualquier orden
SET FOREIGN_KEY_CHECKS
= 0;

-- 2) Borrar tablas si existen
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS atm_cash;

-- 3) Reactivar las comprobaciones de FK
SET FOREIGN_KEY_CHECKS
= 1;

-- 4) Crear tabla de usuarios (clientes y empleados)
CREATE TABLE users
(
    id INT
    AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR
    (50) UNIQUE NOT NULL,
    password_hash  VARCHAR
    (64) NOT NULL,
    user_type      ENUM
    ('EMPLEADO','CLIENTE') NOT NULL
);

    -- 5) Crear tabla de cuentas
    CREATE TABLE accounts
    (
        id INT
        AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    account_number VARCHAR
        (20) UNIQUE NOT NULL,
    balance        DECIMAL
        (15,2) NOT NULL DEFAULT 0,
    FOREIGN KEY
        (user_id) REFERENCES users
        (id)
);

        -- 6) Crear tabla de transacciones
        CREATE TABLE transactions
        (
            id INT
            AUTO_INCREMENT PRIMARY KEY,
    account_id        INT NOT NULL,
    type              ENUM
            ('DEPOSITO','RETIRO','TRANSFERENCIA') NOT NULL,
    amount            DECIMAL
            (15,2) NOT NULL,
    timestamp         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    target_account_id INT NULL,
    FOREIGN KEY
            (account_id)        REFERENCES accounts
            (id),
    FOREIGN KEY
            (target_account_id) REFERENCES accounts
            (id)
);

            -- 7) Crear tabla de efectivo del cajero
            CREATE TABLE atm_cash
            (
                id INT PRIMARY KEY CHECK (id = 1),
                total_cash DECIMAL(15,2) NOT NULL
            );

            -- 8) Poblar datos de prueba

            -- 8.1) Usuarios: id, username, SHA-256(password), tipo
            INSERT INTO users
                (id, username, password_hash, user_type)
            VALUES
                (1, 'guest', SHA2('1234',256), 'CLIENTE'),
                (2, 'guest1', SHA2('abc',256), 'CLIENTE'),
                (3, 'guest2', SHA2('pwd',256), 'CLIENTE'),
                (4, 'admin', SHA2('1234',256), 'EMPLEADO');

            -- 8.2) Cuentas: id, user_id, número de cuenta, balance inicial
            INSERT INTO accounts
                (id, user_id, account_number, balance)
            VALUES
                (1, 1, 'ACC1001', 1000.00),
                (2, 2, 'ACC1002', 500.00),
                (3, 3, 'ACC1003', 250.50);

            -- 8.3) Transacciones de ejemplo
            INSERT INTO transactions
                (account_id, type, amount, timestamp, target_account_id)
            VALUES
                (1, 'DEPOSITO', 200.00, '2025-06-27 10:15:00', NULL),
                (1, 'RETIRO', 150.00, '2025-06-27 12:30:00', NULL),
                (2, 'TRANSFERENCIA', 50.00, '2025-06-27 14:45:00', 1),
                (3, 'DEPOSITO', 75.00, '2025-06-27 16:00:00', NULL);

            -- 8.4) Inventario inicial de efectivo del cajero
            INSERT INTO atm_cash
                (id, total_cash)
            VALUES
                (1, 10000.00)
            ON DUPLICATE KEY
            UPDATE total_cash = VALUES
            (total_cash);
