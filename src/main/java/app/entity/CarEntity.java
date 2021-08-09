package app.entity;

import lombok.*;
import summer.orm.annotations.Column;
import summer.orm.annotations.ID;
import summer.orm.annotations.Table;

@Table(name = "car")
@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CarEntity {

    @ID
    Long idCar;

    @Column(name = "color")
    String color;

}
