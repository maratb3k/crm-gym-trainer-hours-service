package com.example.crm_gym_microservice;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.example.crm_gym_microservice.steps",
        plugin = {"pretty", "json:target/cucumber.json"}
)
@CucumberContextConfiguration
@SpringBootTest
public class CucumberTestRunner {

}

