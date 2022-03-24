package cn.wolfcode.car.business.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Customer {
    /** */
    private Long id;

    /** */
    private String name;

    /** */
    private String phone;

    /** */
    private Integer age;
}