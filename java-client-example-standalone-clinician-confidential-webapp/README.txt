The Standalone Clinician Confidential webapp example shows how to create a web application that is not launched from an EHR (is
standalone) and uses the confidential client architecture.  This style differs from being launched from an EHR in that
the application must handle the launch initiation (for example, no clinician in the EHR clicked the application).

As a "clinician" application, the user is a clinician who wants to find information about a patient, therefore the scope
requested is "patient/*.read" and "launch/patient".