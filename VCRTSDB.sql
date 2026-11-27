CREATE DATABASE IF NOT EXISTS vcrts;
USE vcrts;

CREATE USER IF NOT EXISTS 'vcrts'@'localhost' IDENTIFIED BY 'vcrts123';
GRANT ALL PRIVILEGES ON vcrts.* TO 'vcrts'@'localhost';

CREATE TABLE IF NOT EXISTS client (
    timeStamp  DATETIME DEFAULT CURRENT_TIMESTAMP,
    clientID    VARCHAR(50),
    first_name  VARCHAR(50),
    last_name   VARCHAR(50),
    PRIMARY KEY (clientID)
);

CREATE TABLE IF NOT EXISTS owner (
    timeStamp  DATETIME DEFAULT CURRENT_TIMESTAMP,
    ownerID     VARCHAR(50),
    first_name  VARCHAR(50),
    last_name   VARCHAR(50),
    PRIMARY KEY (ownerID)
);

CREATE TABLE IF NOT EXISTS job (
    timeStamp  DATETIME DEFAULT CURRENT_TIMESTAMP,
    jobID       VARCHAR(50),
    clientID    VARCHAR(50),
    job_name    VARCHAR(100),
    duration    INT,
    deadline    VARCHAR(30),
    importance  INT,
    job_status  VARCHAR(20),
    PRIMARY KEY (jobID),
    INDEX idx_job_client (clientID)
);

CREATE TABLE IF NOT EXISTS vehicle (
    timeStamp      DATETIME DEFAULT CURRENT_TIMESTAMP,
    vehicleID       VARCHAR(50),
    ownerID         VARCHAR(50),
    make            VARCHAR(50),
    model           VARCHAR(50),
    year            INT,
    license_plate   VARCHAR(20),
    ram_gb          INT,
    cpu_cores       INT,
    storage_gb      INT,
    residency_time  INT,
    vehicle_status  VARCHAR(20),
    PRIMARY KEY (vehicleID),
    INDEX idx_vehicle_owner (ownerID)
);