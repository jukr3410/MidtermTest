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
        
        <%-- sessionScope คือรับค่าที่เราตั้งใน servlet แบบ session ส่วน .(จุด) หลัง student ก็อ้าง column DB ดูง่าย --%>
        <h3>User Full Name : ${sessionScope.student.fullname}</h3>
        <a href="Equipment.jsp">Back</a>
        <hr>
        <p style="color: red">
            ${errorborrow}
        </p>
        <table border="1">
            <thead>
                <tr>
                    <th>id</th>
                    <th>name</th>
                    <th>status</th>
                </tr>
            </thead>
            
            
            <%-- ใช้ tag c:forEach เพื่อ Loop 
                items : List ที่รับมาจาก servlet
                var : ตัวย่อของ items
                หลัง .(จุด) เหมือนกันอ้างจาก DB
            --%>
            <tbody>
                <c:forEach items="${equipments}" var="e">
                    <tr>
                        <td>${e.id}</td>
                        <td>${e.name}</td>
                        <td>
                            <%-- href : สามารถส่ง Param ไปพร้อมกับกดลิงค์ได้เลย ..url..?..param..=..value.. --%>
                            <a href="Borrow?borrowid=${e.id}" >
                                <%-- choose อารมณ์แบบ swithcase 
                                    when ใช้กำหนดเงื่อนไข เขียนใน test=""
                                    otherwise กรณีอื่น
                                --%>
                                <c:choose>
                                    <c:when test="${e.borrower==null}">
                                        Borrow
                                    </c:when>
                                    <c:otherwise>
                                        Borrowed
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
