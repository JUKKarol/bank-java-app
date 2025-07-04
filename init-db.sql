CREATE DATABASE "auth-service";
CREATE DATABASE "transaction-service";
CREATE DATABASE "atm-service";

GRANT ALL PRIVILEGES ON DATABASE "auth-service" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "transaction-service" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "atm-service" TO postgres;
