package com.demo;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;

import com.pojo.LoginRequest;
import com.pojo.LoginResponse;
import com.pojo.OrderDetails;
import com.pojo.Orders;
public class EcommerceApiTest {

	public static void main(String[] args) {
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON)
		.setBaseUri("https://rahulshettyacademy.com/").build();
		
		LoginRequest loginReq = new LoginRequest();
		loginReq.setUserEmail("kajalwasate0897@gmail.com");
		loginReq.setUserPassword("Kajal@123");
		RequestSpecification reqLogin = given().log().all().spec(requestSpecification).body(loginReq);
		
		LoginResponse loginResponse = reqLogin.when().post("api/ecom/auth/login")
		.then().log().all().extract().response().as(LoginResponse.class);
		String token = loginResponse.getToken();
		String userId = loginResponse.getUserId();
		System.out.println(loginResponse.getToken());
		System.out.println(loginResponse.getUserId());
		System.out.println(loginResponse.getMessage());
		
		//Create Product
		RequestSpecification reqSpecBaseProduct = new RequestSpecBuilder()
				.addHeader("authorization", token)
				.setBaseUri("https://rahulshettyacademy.com/").build();
		RequestSpecification reqSpecAddProduct =	given().log().all().spec(reqSpecBaseProduct)
		.param("productName", "ASUS")
		.param("productAddedBy", userId)
		.param("productCategory", "Electronics")
		.param("productSubCategory", "Laptop")
		.param("productPrice", "50000")
		.param("productDescription", "ASUS Laptop")
		.param("productFor", "UNISEX")
		.multiPart("productImage",new File("C://Users//kajal//OneDrive//Desktop//Documents//Laptop.jpg"));
		
		String addProductRes = reqSpecAddProduct.when().post("api/ecom/product/add-product")
		.then().log().all().extract().response().asString();
		JsonPath js = new JsonPath(addProductRes);
		String message = js.getString("message");
		String productId = js.getString("productId");
		System.out.println(message);
		
		//Create Order
		RequestSpecification reqSpecBaseCreateOrder = new RequestSpecBuilder()
				.addHeader("authorization", token)
				.setContentType(ContentType.JSON)
				.setBaseUri("https://rahulshettyacademy.com/").build();
		OrderDetails orderDetail = new OrderDetails();
		orderDetail.setCountry("India");
		orderDetail.setProductOrderedId(productId);
		List<OrderDetails>orderDetailList = new ArrayList<OrderDetails>();
		orderDetailList.add(orderDetail);
		
		Orders order = new Orders();
		order.setOrders(orderDetailList);
		RequestSpecification reqCreateOrder =  given().log().all().spec(reqSpecBaseCreateOrder).body(order);
		String createOrderResponse = reqCreateOrder.when().post("api/ecom/order/create-order")
		.then().log().all().extract().response().asString();
		System.out.println(createOrderResponse);
		
		
		//DELETE Product
		RequestSpecification reqSpecBaseDeleteProduct = new RequestSpecBuilder()
				.addHeader("authorization", token)
				.setContentType(ContentType.JSON)
				.setBaseUri("https://rahulshettyacademy.com/").build();
		RequestSpecification reqDeleteProduct =  given().log().all().spec(reqSpecBaseDeleteProduct).pathParam("productId", productId);
		String deleteResponse = reqDeleteProduct.when().delete("api/ecom/product/delete-product/{productId}")
		.then().log().all().extract().response().asString();
		JsonPath js1 = new JsonPath(deleteResponse);
		String deleteMsg = js1.getString("message");
		Assert.assertEquals(deleteMsg, "Product Deleted Successfully");
	}

}
