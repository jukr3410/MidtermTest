/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;
import jpacontroller.EquipmentsJpaController;
import jpacontroller.exceptions.RollbackFailureException;
import model.Equipments;
import model.Students;

/**
 *
 * @author Jn
 */
public class BorrowServlet extends HttpServlet {

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
        
        
        HttpSession session = request.getSession();
        String borrowid = request.getParameter("borrowid"); //รับค่าที่ใส่ใน href tag <a>

        EquipmentsJpaController ejc = new EquipmentsJpaController(utx, emf);
        List<Equipments> equipments = ejc.findEquipmentsEntities(); //รับ obj ใน DB มาทั้งหมด
        
        if (borrowid != null) {
            Equipments e = ejc.findEquipments(Integer.valueOf(borrowid)); //ไปดูใน DB ว่ากด borrow อันไหน 
            if (e.getBorrower() == null) { // ก็มาเช็คดูว่ามีคนยืมอยู่หรือไม่ ถ้าไม่มี่ก็ต้องว่าง(Null)
                Students s = (Students) session.getAttribute("student"); //รับค่าคนที่ login อยู่ ก็คือค่า student ที่เราตั้งใน session LoginServlet
                e.setBorrower(s); //ก็ตั้งคนยืมให้มัน ค่าที่มันรับค่า obj ของ Students
                try {
                    ejc.edit(e); //ใช้ Method edit() ในการ update หรือแก้ไขค่าใน DB
                } catch (RollbackFailureException ex) {
                    Logger.getLogger(BorrowServlet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(BorrowServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                response.sendRedirect("/MidtermTest/Borrow");
                return;

            } else {
                request.setAttribute("errorborrow", "can not borrow !!"); // ถ้าไม่ว่างก็คือมี่คนยืมอยู่ ส่งข้อความไป
            }

        }

        request.setAttribute("equipments", equipments);
        getServletContext().getRequestDispatcher("/Borrow.jsp").forward(request, response); //ใช้กับ jsp ไหน

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
