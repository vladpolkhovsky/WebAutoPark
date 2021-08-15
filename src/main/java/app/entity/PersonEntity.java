package app.entity;

import lombok.*;
import summer.orm.annotations.Column;
import summer.orm.annotations.ID;
import summer.orm.annotations.Table;

import java.sql.Date;

@Table(name = "person")
@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PersonEntity {

    @ID
    Long id;

    @Column(name = "first_name", unique = true)
    String fname;

    @Column(name = "second_name", unique = true)
    String sname;

    @Column(name = "age")
    Integer age;

    @Column(name = "DOB")
    Date date;

}
