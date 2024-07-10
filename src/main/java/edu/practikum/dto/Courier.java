package edu.practikum.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Решил воспользоваться ломбоком. там намного читаемее и удобнее,
 * чем портянка геттеров и сеттеров
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Courier {

    private Integer id;
    private String login;
    private String password;
    private String firstName;
}
