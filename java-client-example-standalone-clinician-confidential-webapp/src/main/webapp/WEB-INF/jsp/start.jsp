<%--Copyright (c) 2015. Healthcare Services Platform Consortium. All Rights Reserved.--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>Java Client Example - Standalone Confidential Webapp</title>
</head>
<body>
<h2>Java Client Example - Standalone Confidential Webapp</h2>

<blink>This app is not build correctly with the launch/patient context.  Waiting on consolidation of the Ref Impl with SMART</blink>

<form action="launch">
    <input type="hidden" name="launch" value="${launch}" />
    <input type="hidden" name="iss" value="${iss}" />
    Patient Id: <input type="textbox" name="patientId" value="ID9995679"/>
    <input type="submit" name="Show Patient Height Chart"/>
</form>

</body>
</html>