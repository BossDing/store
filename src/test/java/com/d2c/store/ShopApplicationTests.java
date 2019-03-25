package com.d2c.store;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/*
 * VM options: -Dspring.profiles.active=dev -Des.set.netty.runtime.available.processors=false
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {StoreApplication.class})
public class ShopApplicationTests {

    @Test
    public void contextLoads() {
    }

}

