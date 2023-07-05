package com.dzl.usercenter.config;

import com.dzl.usercenter.model.domain.User;
import com.dzl.usercenter.test.Car;
import com.dzl.usercenter.test.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dzl
 * @date 2023/7/4 20:11
 */
@Configuration
public class TestComponent {


    @Bean
    public Car car(){
        Car car = new Car();
        car.setType("好车");
        return car;
    }

    @Bean
    public Driver driver(){
        Driver driver = new Driver();
        driver.setCar(car());
        return driver;
    }
}
