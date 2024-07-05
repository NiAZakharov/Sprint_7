package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Integer id;
    private String track;


    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private Integer rentTime;
    private String deliveryDate;
    private String comment;
    private String color;

    private Integer courierId; //не обязательный
    private Integer nearestStation; //не обязательный
    private Integer limit; //не обязательный
    private Integer page; //не обязательный





}
