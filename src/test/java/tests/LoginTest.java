package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ExtentReportManager;

import static io.restassured.RestAssured.*;

public class LoginTest {

    String baseURI = "https://reqres.in/api";
    RequestSpecification requestSpec;
    ExtentReports extent;
    ExtentTest test;


    @BeforeClass
    public void setup() {
        extent = ExtentReportManager.getReportObject();
        RestAssured.baseURI = baseURI;

        requestSpec = given()
                .header("Content-Type", "application/json")
                .header("x-api-key", "reqres-free-v1");
    }
    @Test
    public void testPositiveLogin() {
        test = extent.createTest("Positive Login Test");
        RestAssured.baseURI = baseURI;
try {
    JSONObject loginData = new JSONObject();
    loginData.put("email", "eve.holt@reqres.in");
    loginData.put("password", "cityslicka");


    Response response = requestSpec
            .body(loginData.toString())
            .when()
            .post("/login")
            .then()
            .extract()
            .response();

    System.out.println("Response: " + response.asString());

    Assert.assertEquals(response.statusCode(), 200, "Expected 200 OK");

    Assert.assertTrue(response.asString().contains("token"));
}catch (AssertionError a) {
        test.fail("Login failed");
}
    }

    @Test
    public void testNegativeLogin_MissingPassword() {
        test = extent.createTest("Negative Login Test");
        RestAssured.baseURI = baseURI;
        JSONObject loginData = new JSONObject();
        loginData.put("email", "eve.holt@reqres.in");


        Response response = requestSpec
                .body(loginData.toString())
                .when()
                .post("/login")
                .then()
                .extract()
                .response();

        System.out.println("Response: " + response.asString());

        Assert.assertEquals(response.statusCode(), 400, "Expected 400 Bad Request");
        Assert.assertTrue(response.asString().contains("Missing password"));
        test.pass("Error message received as expected");
    }
    @AfterClass
    public void tearDownReport() {
        extent.flush(); // Write results to report file
    }
}
