The Standalone Patient Confidential webapp example shows how to create a web application that is not launched from an EHR (is
standalone) and uses the confidential client architecture.  This style differs from being launched from an EHR in that
the application must handle the launch initiation (for example, no clinician in the EHR clicked the application).

As a "patient" application, the user is a patient who wants to find information about themself.  Therefore, the scope
requested is "user/*.read".