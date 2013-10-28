package org.testfun.jee.real;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data @AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@NamedQueries(@NamedQuery(name = SomeEntity.QUERY_PROVIDER_BY_NAME, query = "SELECT p FROM SomeEntity AS p WHERE p.name = :name"))
@Table(catalog = "tmp", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Entity
public class SomeEntity {
    public static final String QUERY_PROVIDER_BY_NAME = "QUERY_PROVIDER_BY_NAME";

    @Id
    @GeneratedValue
    private int id;

    private String name;

    private String vcdApiAddress;

}
