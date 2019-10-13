/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jn
 */
@Entity
@Table(name = "EQUIPMENTS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Equipments.findAll", query = "SELECT e FROM Equipments e")
    , @NamedQuery(name = "Equipments.findById", query = "SELECT e FROM Equipments e WHERE e.id = :id")
    , @NamedQuery(name = "Equipments.findByName", query = "SELECT e FROM Equipments e WHERE e.name = :name")})
public class Equipments implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 255)
    @Column(name = "NAME")
    private String name;
    @JoinColumn(name = "BORROWER", referencedColumnName = "ID")
    @ManyToOne
    private Students borrower;

    public Equipments() {
    }

    public Equipments(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Students getBorrower() {
        return borrower;
    }

    public void setBorrower(Students borrower) {
        this.borrower = borrower;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Equipments)) {
            return false;
        }
        Equipments other = (Equipments) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Equipments[ id=" + id + " ]";
    }
    
}
