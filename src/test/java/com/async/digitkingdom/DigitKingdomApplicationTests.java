package com.async.digitkingdom;

import com.async.digitkingdom.controller.DeviceController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DigitKingdomApplicationTests {
    @Resource
    private DeviceController deviceController;

    @Test
    void contextLoads() {
    }

    @Test
    void testGenerateCode(){
        deviceController.generateQRCode("1213");
    }

}
