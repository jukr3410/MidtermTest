<%-- 
    Document   : Login
    Created on : Oct 11, 2019, 10:34:31 PM
    Author     : Jn
--%>
<%--  --%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Page</title>
    </head>
    <body>
        <h1>Equipment Store</h1>
        <h2>Login</h2>
        <hr>
        
        <%-- รับข้อความเตือนรหัสผิดจาก servlet--%>      
        <p style="color: red">
            ${errorlogin}
        </p> 
        
        <%-- กรอกข้อความส่งไปยัง servlet 
        มีค่าที่ส่งคือ username และ password 
        ${param.(atr)} param คือรับค่า ใน page เดียวกัน
        --%>
        <form action="Login" method="post"> 
            <input name="username" type="text" required value="${param.username}"/><br><br>
            <input name="password" type="text" required value="${param.password}"/><br><br>
            <input type="submit" value="Submit"/>
        </form>
    </body>
</html>
