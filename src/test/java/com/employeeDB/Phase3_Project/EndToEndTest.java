package com.employeeDB.Phase3_Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndToEndTest {
	
	Response response;
	JsonPath jpath;
	List<String> names;
	
	public Response GetAllEmployees() {
		
		RestAssured.baseURI = "http://localhost:3000";
		RequestSpecification request = RestAssured.given();

//		Get All Employees
		response = request.get("employees");
		
//		Print all employee body
		System.out.println("All Employees are "+response.getBody().asString());
		
		return response;
	}
	
	public Response GetSingleEmployees(int empID) {
		
		RestAssured.baseURI = "http://localhost:3000";
		RequestSpecification request = RestAssured.given();

//		Get response for single employee
		Response response = request.get("employees/"+empID);

//		Print single employee body
		System.out.println("Details from Get Single Employee is "+response.getBody().asString());
		
		return response;
	}
	
	public Response CreateEmployee(String name, String salary) {

//		Put name and salary in HashMap
		Map<String,Object> MapObj = new HashMap<String,Object>();
		MapObj.put("name", name);
		MapObj.put("salary", salary);
		
		RestAssured.baseURI = "http://localhost:3000";
		RequestSpecification request = RestAssured.given();

//		Creating employee
		Response response = request
							.contentType(ContentType.JSON)
							.accept(ContentType.JSON)
							.body(MapObj)
							.post("employees/create");
		
		System.out.println("The Following employee is created "+response.getBody().asString());
		
		return response;
		
	}
	
	public Response UpdateEmployee(int empID, String name, String salary) {

//		Put name and salary in HashMap
		Map<String,Object> MapObj = new HashMap<String,Object>();
		MapObj.put("name", name);
		MapObj.put("salary", salary);

		RestAssured.baseURI = "http://localhost:3000";
		RequestSpecification request = RestAssured.given();

//		Updating employee
		Response response = request
							.contentType(ContentType.JSON)
							.accept(ContentType.JSON)
							.body(MapObj)
							.put("employees/"+empID);
		
		System.out.println("Following Employee has been updated "+response.getBody().asString());
		
		return response;
	}
	
	public Response DeleteEmployee(int empID) {
		RestAssured.baseURI = "http://localhost:3000";
		RequestSpecification request = RestAssured.given();

//		Deleting Employee
		Response response = request
							.contentType(ContentType.JSON)
							.accept(ContentType.JSON)
							.delete("employees/"+empID);
		
		System.out.println("Employee deleted from id: "+ empID +response.getBody().asString());
		
		return response;
	}

	@Test
	public void GetEmpTest1() {
		EndToEndTest et = new EndToEndTest();
				
//		Get All Employees
		response = et.GetAllEmployees();
		Assert.assertEquals(200, response.getStatusCode());
		
//		Create an employee with name John and salary as 8000
		response = et.CreateEmployee("John", "8000");
//		Extract employee id from newly created employee
		jpath = response.jsonPath();
		int RetEmpID = jpath.getInt("id");
		System.out.println("New Employee is created with id "+ RetEmpID);
		Assert.assertEquals(201, response.getStatusCode());
		
//		Call get single employee and validate the name is John and status code is 200
		response = et.GetSingleEmployees(RetEmpID);
		jpath=response.jsonPath();
		Assert.assertTrue(jpath.get("name").equals("John"));
		Assert.assertEquals(200, response.getStatusCode());
	
//	Update the employee created in step b and change the name to Smith and validate the status code is 200
		et.UpdateEmployee(RetEmpID, "Smith", "8000");
		Assert.assertEquals(200, response.getStatusCode());
	
//	Call get single employee created in step b and validate the name is Smith and status code is 200
		response = et.GetSingleEmployees(RetEmpID);
		jpath=response.jsonPath();
		Assert.assertTrue(jpath.get("name").equals("Smith"));
		Assert.assertEquals(200, response.getStatusCode());
		
//		Delete the employee created in step b and validate the status code is 200
		response = et.DeleteEmployee(RetEmpID);
		Assert.assertEquals(200, response.getStatusCode());
		
//		Call get single employee created in step b and validate the status code is 404
		response = 	et.GetSingleEmployees(RetEmpID);
		Assert.assertEquals(404, response.getStatusCode());
	
//	Call get All Employee and validate that deleted employee from step-f is not present in the response
		response = et.GetAllEmployees();
		jpath = response.jsonPath();
		names = jpath.get("name");
		Assert.assertFalse(names.contains("Smith"));
	
	}
	
	
}
