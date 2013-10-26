package org.testfun.real;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NamedQueries(@NamedQuery(name = Provider.QUERY_PROVIDER_BY_NAME, query = "SELECT p FROM Provider AS p WHERE p.name = :name"))
@Table(catalog = "noc", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Entity
public class Provider {
    public static final String QUERY_PROVIDER_BY_NAME = "QUERY_PROVIDER_BY_NAME";

    @Id
    @GeneratedValue
    private int id;

    private String name;

    private String vcdApiAddress;

    public Provider(){}

    public Provider(String name, String vcdApiAddress) {
        this.name = name;
        this.vcdApiAddress = vcdApiAddress;
    }

}
