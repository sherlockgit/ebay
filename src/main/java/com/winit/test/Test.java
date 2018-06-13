package com.winit.test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * Created by yf on 2018-05-29.
 */


public class Test {

 public static void  main(String[] args){
     Test  test = new Test();
     test.test();
 }
    public void  test(){
        BigDecimal carriageFee = new BigDecimal("50");
        BigDecimal carriageFee2 = new BigDecimal("50");
        carriageFee =  carriageFee.add(new BigDecimal("50"));

    }
}
