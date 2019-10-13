/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
public class ReturnServlet extends HttpServlet {

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
        String returnid = request.getParameter("returnid");
        
        //คล้ายๆกับ Borrow เลย ที่มีเพิ่มมาคือ Loop เพื่อ ส่ง List แค่ที่ User ยืม
        
        EquipmentsJpaController ejc = new EquipmentsJpaController(utx, emf);
        List<Equipments> equipments = ejc.findEquipmentsEntities(); //เอามาทั้งหมดก่อน
        List<Equipments> borrowed = new ArrayList(100);//สร้าง List เพื่อรับ obj ที่ Loop
        // Loop เปรียบเทียบจาก student ใน session ตรงกับ equipment อันใน List equipment
        if (equipments!=null) {
            Students s = (Students) session.getAttribute("student");
            for (Equipments equipment : equipments) {
                if (s.equals(equipment.getBorrower())) { // borrower ตรงกับก็ ใช้ method add() ในการเพิ่ม obj
                    borrowed.add(equipment); 
                }
            }
        }
        
        
        // เช็คว่ากด return 
        if (returnid != null) {
            Equipments e = ejc.findEquipments(Integer.valueOf(returnid)); // หาก่อนว่า obj ตัวใหนใน DB 
            if (e.getBorrower() != null) { // เช็คว่ามีคนยืมหรือไม่ ถ้ามีมันก็ไม่ว่าง(Not Null)
                Students s = null; 
                e.setBorrower(s); //ถ้าก็ return ได้ก็ตั้งในมันเป็น null ไม่ยืมแล้ว ช่อง borrower ก็จะว่าง
                try {
                    ejc.edit(e); 
                } catch (RollbackFailureException ex) {
                    Logger.getLogger(BorrowServlet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(BorrowServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                response.sendRedirect("/MidtermTest/Return");
                return;

            } else {
                request.setAttribute("errorreturn", "have not in your borrow !!");
            }

        }
        
        request.setAttribute("equipments", borrowed);
        getServletContext().getRequestDispatcher("/Return.jsp").forward(request, response);
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
