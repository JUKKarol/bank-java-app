CREATE DATABASE "auth-service";
CREATE DATABASE "transaction-service";
CREATE DATABASE "atm-service";
CREATE DATABASE "credit-service";

GRANT ALL PRIVILEGES ON DATABASE "auth-service" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "transaction-service" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "atm-service" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "credit-service" TO postgres;
