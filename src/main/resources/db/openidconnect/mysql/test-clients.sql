SET AUTOCOMMIT = 0;

START TRANSACTION;

-- Test Client
INSERT INTO client_details (client_id, client_name, client_secret, access_token_validity_seconds, token_endpoint_auth_method) VALUES
  ('test_client', 'Test', 'secret', 86400, 'SECRET_BASIC');

INSERT INTO client_redirect_uri (owner_id, redirect_uri) VALUES
  ((SELECT id from client_details where client_id = 'test_client'), 'http://localhost:8080/hspc-java-client-example-spring-mvc/launch/redirect'),
  ((SELECT id from client_details where client_id = 'test_client'), 'http://54.213.219.198:9080/uat/hspc-java-client-example-spring-mvc/launch/redirect'),
  ((SELECT id from client_details where client_id = 'test_client'), 'https://sandbox.hspconsortium.org/hspc-java-client-example-spring-mvc/launch/redirect');

INSERT INTO client_scope (owner_id, scope) VALUES
  ((SELECT id from client_details where client_id = 'test_client'), 'launch'),
  ((SELECT id from client_details where client_id = 'test_client'), 'system/*.read'),
  ((SELECT id from client_details where client_id = 'test_client'), 'patient/*.read'),
  ((SELECT id from client_details where client_id = 'test_client'), 'offline_access');

INSERT INTO client_grant_type (owner_id, grant_type) VALUES
  ((SELECT id from client_details where client_id = 'test_client'), 'authorization_code'),
  ((SELECT id from client_details where client_id = 'test_client'), 'client_credentials'),
  ((SELECT id from client_details where client_id = 'test_client'), 'refresh_token');

-- Test Client using JWT
INSERT INTO client_details (client_id, client_name, jwks_uri, token_endpoint_auth_method, access_token_validity_seconds) VALUES
	('test_client_jwt', 'Test JWT', 'https://sandbox.hspconsortium.org/dstu2/hspc-reference-authorization/jwk', 'PRIVATE_KEY', 86400);

-- INSERT INTO client_redirect_uri (owner_id, redirect_uri) VALUES
--	((SELECT id from client_details where client_id = 'test_client_jwt'), 'http://localhost:8080/hspc-java-client-example-spring-mvc/launch/redirect'),
--	((SELECT id from client_details where client_id = 'test_client_jwt'), 'http://localhost:8080/hspc-java-client-example-spring-mvc/example');

INSERT INTO client_scope (owner_id, scope) VALUES
	--	((SELECT id from client_details where client_id = 'test_client_jwt'), 'launch'),
	((SELECT id from client_details where client_id = 'test_client_jwt'), 'system/*.read'),
	--	((SELECT id from client_details where client_id = 'test_client_jwt'), 'patient/*.read'),
	((SELECT id from client_details where client_id = 'test_client_jwt'), 'offline_access');

INSERT INTO client_grant_type (owner_id, grant_type) VALUES
	--	((SELECT id from client_details where client_id = 'test_client_jwt'), 'authorization_code'),
	((SELECT id from client_details where client_id = 'test_client_jwt'), 'client_credentials'),
	((SELECT id from client_details where client_id = 'test_client_jwt'), 'refresh_token');

-- Standalone Patient Test Client
INSERT INTO client_details (client_id, client_name, client_secret, access_token_validity_seconds, token_endpoint_auth_method) VALUES
	('standalone_patient_test_client', 'Standalone Patient Test', 'secret', 86400, 'SECRET_BASIC');

INSERT INTO client_redirect_uri (owner_id, redirect_uri) VALUES
	((SELECT id from client_details where client_id = 'standalone_patient_test_client'), 'http://localhost:8080/hspc-java-client-example-standalone-patient-confidential-webapp/launch/redirect'),
	((SELECT id from client_details where client_id = 'standalone_patient_test_client'), 'http://localhost:8080/hspc-java-client-example-standalone-patient-confidential-webapp/myHeightChart');

INSERT INTO client_scope (owner_id, scope) VALUES
	((SELECT id from client_details where client_id = 'standalone_patient_test_client'), 'launch'),
	((SELECT id from client_details where client_id = 'standalone_patient_test_client'), 'launch/patient'),
	((SELECT id from client_details where client_id = 'standalone_patient_test_client'), 'user/*.read'),
	((SELECT id from client_details where client_id = 'standalone_patient_test_client'), 'offline_access');

INSERT INTO client_grant_type (owner_id, grant_type) VALUES
	((SELECT id from client_details where client_id = 'standalone_patient_test_client'), 'authorization_code'),
	((SELECT id from client_details where client_id = 'standalone_patient_test_client'), 'client_credentials'),
	((SELECT id from client_details where client_id = 'standalone_patient_test_client'), 'refresh_token');

-- Standalone Clinician Test Client
INSERT INTO client_details (client_id, client_name, client_secret, access_token_validity_seconds, token_endpoint_auth_method) VALUES
	('standalone_clinical_test_client', 'Standalone Clinician Test', 'secret', 86400, 'SECRET_BASIC');

INSERT INTO client_redirect_uri (owner_id, redirect_uri) VALUES
	((SELECT id from client_details where client_id = 'standalone_clinical_test_client'), 'http://localhost:8080/hspc-java-client-example-standalone-clinician-confidential-webapp/launch/redirect'),
	((SELECT id from client_details where client_id = 'standalone_clinical_test_client'), 'http://localhost:8080/hspc-java-client-example-standalone-clinician-confidential-webapp/patientHeightChart');

INSERT INTO client_scope (owner_id, scope) VALUES
	((SELECT id from client_details where client_id = 'standalone_clinical_test_client'), 'launch'),
	((SELECT id from client_details where client_id = 'standalone_clinical_test_client'), 'launch/patient'),
	((SELECT id from client_details where client_id = 'standalone_clinical_test_client'), 'patient/*.read'),
	((SELECT id from client_details where client_id = 'standalone_clinical_test_client'), 'offline_access');

INSERT INTO client_grant_type (owner_id, grant_type) VALUES
	((SELECT id from client_details where client_id = 'standalone_clinical_test_client'), 'authorization_code'),
	((SELECT id from client_details where client_id = 'standalone_clinical_test_client'), 'client_credentials'),
	((SELECT id from client_details where client_id = 'standalone_clinical_test_client'), 'refresh_token');

COMMIT;

SET AUTOCOMMIT = 1;
