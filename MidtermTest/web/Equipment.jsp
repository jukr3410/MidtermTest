<%-- 
    Document   : Equipment
    Created on : Oct 11, 2019, 10:34:48 PM
    Author     : Jn
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Equipment Page</title>
    </head>
    <body>
        <h1>Equipment</h1>
        <h3>User Full Name : ${sessionScope.student.fullname}</h3>
        <hr>
        <%-- ลิงค์กดไปหน้าต่าง (url) --%>
        <a href="Borrow">Borrow</a><br>
        <a href="Return">Return</a><br><br>
        <a href="Logout">Logout</a>
    </body>
</html>
