<%--Copyright (c) 2015. Healthcare Services Platform Consortium. All Rights Reserved.--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <style>
        table, th, td {
            border: 1px solid black;
            border-collapse: collapse;
        }

        th, td {
            padding: 5px;
            text-align: left;
        }
    </style>
    <title>Java Client Example - Standalone Clinician Confidential Webapp</title>
</head>
<body>
<h2>${patientFullName} Height Chart</h2>

<c:if test="${not empty heights}">
<div>
    <table id="observation_list">
        <tr><th>Date</th><th>Height</th> </tr>

        <c:forEach var="height" items="${heights}">
        <tr>
            <td>${height.date}</td>
            <td>${height.height}</td>
        </tr>
        </c:forEach>
    </table>
</div>
</c:if>
</body>
</html>