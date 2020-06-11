package org.dukecon;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

public class WebResourcesTests {

	private static String baseUrl
		= System.getProperty("dukecon.apitests.baseurl",
		"https://latest.dukecon.org/javaland/2019");

	@BeforeEach
	public void setup() {
		System.out.println("Testing '" + baseUrl + "'");
		RestAssured.baseURI = baseUrl;
		RestAssured.port = 443;
		RestAssured.registerParser("text/css", Parser.TEXT);
	}

	@Test
	public void testStaticServerInterface() {
		verfyInitJson(
			//TODO local: http://localhost:8080/rest/init/javaland/2016
			whenUrlOkAndContentTypeMatches("/rest/init.json", ContentType.JSON.toString())
		);

		//TODO local http://localhost:8080/rest/image-resources/javaland/2016
		whenUrlOkAndContentTypeMatches("/rest/image-resources.json", ContentType.JSON.toString());

		whenUrlOkAndContentTypeMatches("/rest/conferences/javaland2019", ContentType.JSON.toString());

		//TODO content type does not match?
		whenUrlOk("/rest/speaker/images/c4ab6b88490cb4da5f6ea95dae485095");

		whenUrlOkAndContentTypeMatches("/rest/conferences/javaland2019/styles.css", "text/css");

		whenUrlOkAndContentTypeMatches("/img/favicon.ico", "image/x-icon");
	}

	@Test
	public void testDynamicServerInterface() {
		whenUrlOkAndContentTypeMatches("/rest/keycloak.json", ContentType.JSON.toString());

		whenUrlOkAndContentTypeMatches("/rest/eventsBooking/javaland2019", ContentType.JSON.toString());
	}

	@Test
	public void testDynamicServerInterfacePreferences() throws IOException {
		//TODO there is a cvs variant !

		String token = gatherToken();
		String preferencesContentEmpty = "[]";
		String preferencesContent = generateStringFromResource("samples/preferences.json");

		updatePreferences(token, preferencesContentEmpty);

		whenAuthenticatedUrlOkAndContentTypeMatches(token, "/rest/preferences", ContentType.JSON.toString());

		updatePreferences(token, preferencesContent);

		whenAuthenticatedUrlOkAndContentTypeMatches(token, "/rest/preferences", ContentType.JSON.toString());
		// TODO	Answer is broken?
		//.and()
		//.body(Matchers.equalTo(preferencesContent));
	}

	@Test
	public void testDynamicServerInterfaceFeedback() {
		String token = gatherToken();

		given().auth().oauth2(token)
			.body("{\"comment\":\"test\",\"rating\":3}")
			.contentType(ContentType.JSON)
			.put("rest/feedback/event/javaland2019/569007")
			.then()
			.assertThat()
			.statusCode(201);
	}

	private ValidatableResponse updatePreferences(String token, String preferencesContentEmpty) {
		return given().auth().oauth2(token)
			.body(preferencesContentEmpty)
			.contentType(ContentType.JSON)
			.post("/rest/preferences")
			.then()
			.assertThat()
			.statusCode(201);
	}

	private void verfyInitJson(ValidatableResponse response) {
		response.body(matchesJsonSchemaInClasspath("samples/init.json"));
	}

	private String gatherToken() {
		Response response = given()
			.config(RestAssured.config()
				.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs("x-www-form-urlencoded",
						ContentType.URLENC)))
			.contentType("application/x-www-form-urlencoded; charset=UTF-8")
			.formParam("grant_type", "client_credentials")
			.formParam("client_id", "dukecon_api")
			.formParam("client_secret", "97d8f2d3-d55c-46bd-aafd-bb4d9e058955")
			.post("https://keycloak.dukecon.org/auth/realms/dukecon-latest/protocol/openid-connect/token");

		JsonPath jsonPathEvaluator = response.jsonPath();

		return jsonPathEvaluator.get("access_token");
	}

	private ValidatableResponse whenAuthenticatedUrlOkAndContentTypeMatches(String token, String path, String contentType) {
		return whenUrlOkAndContentTypeMatchesBase(given().auth().oauth2(token), path, contentType);
	}

	private ValidatableResponse whenUrlOk(String path) {
		return whenUrlOk(given(), path);
	}

	private ValidatableResponse whenUrlOk(RequestSpecification spec, String path) {
		return spec.when()
			.get(path)
			.then()
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
	}

	private ValidatableResponse whenUrlOkAndContentTypeMatches(String path, String contentType) {
		return whenUrlOkAndContentTypeMatchesBase(given(), path, contentType);
	}

	private ValidatableResponse whenUrlOkAndContentTypeMatchesBase(RequestSpecification spec, String path, String contentType) {
		return
			whenUrlOk(spec.accept(contentType), path)
				.contentType(contentType);

	}

	public String generateStringFromResource(String path) throws IOException {
		File file = new File(this.getClass().getClassLoader().getResource(path).getFile());
		return FileUtils.readFileToString(file, "UTF-8");
	}
}