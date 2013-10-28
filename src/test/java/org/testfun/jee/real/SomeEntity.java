package org.testfun.jee.real;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NamedQueries(@NamedQuery(name = SomeEntity.QUERY_PROVIDER_BY_NAME, query = "SELECT p FROM SomeEntity AS p WHERE p.name = :name"))
@Table(catalog = "noc", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Entity
public class SomeEntity {
    public static final String QUERY_PROVIDER_BY_NAME = "QUERY_PROVIDER_BY_NAME";

    @Id
    @GeneratedValue
    private int id;

    private String name;

    private String vcdApiAddress;

    public SomeEntity(){}

    public SomeEntity(String name, String vcdApiAddress) {
        this.name = name;
        this.vcdApiAddress = vcdApiAddress;
    }

}
