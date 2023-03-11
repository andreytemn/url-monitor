CREATE TABLE IF NOT EXISTS user(
  id BINARY(16) PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  access_token VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS monitored_endpoint (
  id BINARY(16) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  url VARCHAR(255) NOT NULL,
  created_at DATETIME NOT NULL,
  last_check_at DATETIME NOT NULL,
  monitored_interval INT NOT NULL,
  user_id BINARY(16) NOT NULL,
  CONSTRAINT `fk_monitored_endpoint_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE IF NOT EXISTS monitoring_result (
  id BINARY(16) PRIMARY KEY,
  check_date DATETIME NOT NULL,
  status_code INT NOT NULL,
  payload VARCHAR(255) NOT NULL,
  monitored_endpoint_id BINARY(16) NOT NULL,
  CONSTRAINT `fk_monitoring_result_monitored_endpoint` FOREIGN KEY (`monitored_endpoint_id`) REFERENCES `monitored_endpoint` (`id`)
);