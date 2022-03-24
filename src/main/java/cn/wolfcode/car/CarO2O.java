package cn.wolfcode.car;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"cn.wolfcode.car.*.mapper"})
public class CarO2O {
    public static void main(String[] args) {
        SpringApplication.run(CarO2O.class, args);
    }
}
