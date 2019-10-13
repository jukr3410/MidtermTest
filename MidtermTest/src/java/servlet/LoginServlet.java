/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;
import jpacontroller.StudentsJpaController;
import model.Students;

/**
 *
 * @author Jn
 */
public class LoginServlet extends HttpServlet {
    @PersistenceUnit(unitName = "MidtermTestPU")
    EntityManagerFactory emf;
    
    @Resource
    UserTransaction utx;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // รับค่ามาจาก Login.jsp
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        //เช็คว่ามีค่าไม่เป็น Null แล้วค่อยไปหาใน DB
        if (username!=null&&password!=null) {
            StudentsJpaController sjc = new StudentsJpaController(utx, emf); //สร้าง Obj เป็นใช้ method เกี่ยวกับจัดการ DB
            List<Students> students = sjc.findStudentsEntities(); //สร้าง List รับค่า User ที่มีทั้งหมด เพื่อมา Loop หา User เอา
            for (Students student : students) {
                //เช็คว่า User & Pass ของ student แต่ล่ะตัวใน List เท่ากับค่าที่รับมาหรือไม่ 
                if (student.getUsername().equals(username)&&student.getPassword().equals(password)) {
                    HttpSession session = request.getSession(); //ถ้าหาเจอก็ให้สร้าง Session ขึ้นมา
                    
                    //ต่อด้วยกำหนดค่าใน Session เพื่อที่หน้า Page จะได้ดึงค่าจาก ${SessionScope.(Atr)}
                    //Session เมื่อกำหนดค่าก็สามารถเรียกได้ทุก Page
                    session.setAttribute("student", student); 
                    response.sendRedirect("/MidtermTest/Equipment.jsp");  // ก็ตอบกลับด้วยหน้าเว็บนี้
                    return; //อย่าลืมมันจะได้จบการทำงาน
                }
            }
            request.setAttribute("errorlogin", "Wrong user or pass !!"); //Loop หาไม่เจอก็ส่งข้อความไป .jsp
        }
        
        getServletContext().getRequestDispatcher("/Login.jsp").forward(request, response); //Servlet ใช้กับ jsp ใหน
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
