<%--Copyright (c) 2015. Healthcare Services Platform Consortium. All Rights Reserved.--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>Java Client Example - Standalone Patient Confidential Webapp</title>
</head>
<body>
<h2>Java Client Example - Standalone Patient Confidential Webapp</h2>

<form action="launch">
    <input type="hidden" name="launch" value="${launch}" />
    <input type="hidden" name="iss" value="${iss}" />
    <input type="submit" name="Submit" value="Show My Height Chart"/>
</form>

</body>
</html>