package org.testfun.jee;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

@Data
@Table(catalog = "runner_test")
@EqualsAndHashCode(callSuper = false)
@Entity
public class Duplicates {

    @Id
    @GeneratedValue
    @XmlTransient
    private int id;

    private String name;

    @Transient
    private String duplicateName;

    public Duplicates(String name) {
        this.name = name;
    }

    @PreUpdate
    public void callback() {
        duplicateName = name;
    }

}
