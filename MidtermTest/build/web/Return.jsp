<%-- 
    Document   : Borrow
    Created on : Oct 11, 2019, 10:35:10 PM
    Author     : Jn
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Equipment</h1>
        <h3>User Full Name : ${sessionScope.student.fullname}</h3>
        <a href="Equipment.jsp">Back</a>
        <hr>
        <p style="color: red">
            ${errorreturn}
        </p>       
            <table border="1">
                <thead>
                    <tr>
                        <th>id</th>
                        <th>name</th>
                        <th>status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${equipments}" var="e">
                        <tr>
                            <td>${e.id}</td>
                            <td>${e.name}</td>
                            <td>
                                <a href="Return?returnid=${e.id}" >
                                    <%-- ไม่ต้องมี choose ก็ได้ เพราะ List ที่ส่งมาจะมี่แค่อันที่ User ยืมไป ขึ้นแค่ Return ก็พอ--%>
                                    <c:choose>
                                        <c:when test="${e.borrower==null}">
                                            Returned
                                        </c:when>
                                        <c:otherwise>
                                            Return
                                        </c:otherwise>
                                    </c:choose>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        
    </body>
</html>
