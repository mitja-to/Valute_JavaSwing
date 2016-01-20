/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.valuta;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Mitja
 */
@Entity
@Table(name = "VALUTA")
@NamedQueries({
    @NamedQuery(name = "Valuta.findAll", query = "SELECT v FROM Valuta v"),
    @NamedQuery(name = "Valuta.findById", query = "SELECT v FROM Valuta v WHERE v.id = :id"),
    @NamedQuery(name = "Valuta.findByOznaka", query = "SELECT v FROM Valuta v WHERE v.oznaka = :oznaka"),
    @NamedQuery(name = "Valuta.findBySifra", query = "SELECT v FROM Valuta v WHERE v.sifra = :sifra"),
    @NamedQuery(name = "Valuta.findByTecaj", query = "SELECT v FROM Valuta v WHERE v.tecaj = :tecaj"),
    @NamedQuery(name = "Valuta.findByDatum", query = "SELECT v FROM Valuta v WHERE v.datum = :datum")})
public class Valuta implements Serializable {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "OZNAKA")
    private String oznaka;
    @Column(name = "SIFRA")
    private Short sifra;
    @Column(name = "TECAJ")
    private String tecaj;
    @Column(name = "DATUM")
    @Temporal(TemporalType.DATE)
    private Date datum;

    public Valuta() {
    }

    public Valuta(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        Integer oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public String getOznaka() {
        return oznaka;
    }

    public void setOznaka(String oznaka) {
        String oldOznaka = this.oznaka;
        this.oznaka = oznaka;
        changeSupport.firePropertyChange("oznaka", oldOznaka, oznaka);
    }

    public Short getSifra() {
        return sifra;
    }

    public void setSifra(Short sifra) {
        Short oldSifra = this.sifra;
        this.sifra = sifra;
        changeSupport.firePropertyChange("sifra", oldSifra, sifra);
    }

    public String getTecaj() {
        return tecaj;
    }

    public void setTecaj(String tecaj) {
        String oldTecaj = this.tecaj;
        this.tecaj = tecaj;
        changeSupport.firePropertyChange("tecaj", oldTecaj, tecaj);
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        Date oldDatum = this.datum;
        this.datum = datum;
        changeSupport.firePropertyChange("datum", oldDatum, datum);
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
        if (!(object instanceof Valuta)) {
            return false;
        }
        Valuta other = (Valuta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.valuta.Valuta[ id=" + id + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
