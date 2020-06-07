package org.dukecon;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.notNullValue;

public class WebResourcesTests {

	@BeforeEach
	public void setup() {
		RestAssured.baseURI = "https://latest.dukecon.org/javaland/2019";
		RestAssured.port = 443;
		RestAssured.registerParser("text/css", Parser.TEXT);
	}

	@Test
	public void testStaticServerInterface() {
		verfyInitJson(
			whenUrlOkAndContentTypeMatches("/rest/init.json", ContentType.JSON.toString())
		);
		whenUrlOkAndContentTypeMatches("/rest/image-resources.json", ContentType.JSON.toString());

		whenUrlOkAndContentTypeMatches("/rest/speaker/images/c4ab6b88490cb4da5f6ea95dae485095","image/png");

		whenUrlOkAndContentTypeMatches("/rest/conferences/javaland2019", ContentType.JSON.toString());
		whenUrlOkAndContentTypeMatches("/rest/conferences/javaland2019/styles.css", "text/css");

		whenUrlOkAndContentTypeMatches("/img/favicon.ico", "image/x-icon");
	}

	@Test
	public void testDynamicServerInterface() {
		whenUrlOkAndContentTypeMatches("/rest/keycloak.json", ContentType.JSON.toString());

		whenUrlOkAndContentTypeMatches("/rest/eventsBooking/javaland2019", ContentType.JSON.toString());
	}

	private void verfyInitJson(ValidatableResponse response) {
			response.body("id", Matchers.notNullValue());
	}

	private ValidatableResponse whenUrlOkAndContentTypeMatches(String path, String contentType) {
		return when()
			.get(path).
			then()
			.assertThat()
			.statusCode(200)
			.contentType(contentType)
			.body(notNullValue());
	}

}