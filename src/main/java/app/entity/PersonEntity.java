package app.entity;

import lombok.Builder;
import lombok.ToString;
import summer.orm.annotations.Column;
import summer.orm.annotations.ID;
import summer.orm.annotations.Table;

@Table(name = "person")
@Builder
@ToString
public class PersonEntity {

    @ID
    Long id;

    @Column(name = "first_name", unique = true)
    String fname;

    @Column(name = "second_name", unique = true)
    String sname;

    @Column(name = "age")
    Integer age;

}
