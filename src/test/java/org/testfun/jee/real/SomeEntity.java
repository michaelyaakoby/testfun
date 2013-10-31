package org.testfun.jee.real;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Data @AllArgsConstructor @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Entity
public class SomeEntity {

    @Id
    @GeneratedValue
    private int id;

    @Length.List({
            @Length(min = 4, message = "The name must be at least 4 character"),
            @Length(max = 20, message = "The name must be less than 20 characters")
    })
    private String name;

    private String vcdApiAddress;

}
