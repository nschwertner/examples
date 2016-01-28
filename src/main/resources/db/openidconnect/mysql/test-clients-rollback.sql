SET AUTOCOMMIT = 0;

START TRANSACTION;

-- Test Client
DELETE FROM client_grant_type WHERE owner_id = (SELECT id from client_details where client_id = 'test_client');
DELETE FROM client_scope WHERE owner_id = (SELECT id from client_details where client_id = 'test_client');
DELETE FROM client_redirect_uri WHERE owner_id = (SELECT id from client_details where client_id = 'test_client');
DELETE FROM client_details WHERE client_id = 'test_client';

-- Test Client using JWT
DELETE FROM client_grant_type WHERE owner_id = (SELECT id from client_details where client_id = 'test_client_jwt');
DELETE FROM client_scope WHERE owner_id = (SELECT id from client_details where client_id = 'test_client_jwt');
DELETE FROM client_redirect_uri WHERE owner_id = (SELECT id from client_details where client_id = 'test_client_jwt');
DELETE FROM client_details WHERE client_id = 'test_client_jwt';

-- Standalone Patient Test Client
DELETE FROM client_grant_type WHERE owner_id = (SELECT id from client_details where client_id = 'standalone_patient_test_client');
DELETE FROM client_scope WHERE owner_id = (SELECT id from client_details where client_id = 'standalone_patient_test_client');
DELETE FROM client_redirect_uri WHERE owner_id = (SELECT id from client_details where client_id = 'standalone_patient_test_client');
DELETE FROM client_details WHERE client_id = 'standalone_patient_test_client';

-- Standalone Clinician Test Client
DELETE FROM client_grant_type WHERE owner_id = (SELECT id from client_details where client_id = 'standalone_clinical_test_client');
DELETE FROM client_scope WHERE owner_id = (SELECT id from client_details where client_id = 'standalone_clinical_test_client');
DELETE FROM client_redirect_uri WHERE owner_id = (SELECT id from client_details where client_id = 'standalone_clinical_test_client');
DELETE FROM client_details WHERE client_id = 'standalone_clinical_test_client';

COMMIT;

SET AUTOCOMMIT = 1;
