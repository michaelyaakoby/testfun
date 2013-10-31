package org.testfun.jee;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

@Data @NoArgsConstructor
@Table(uniqueConstraints=
    @UniqueConstraint(columnNames={"NAME"})
)
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
