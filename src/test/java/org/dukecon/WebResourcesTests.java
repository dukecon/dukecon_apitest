package org.dukecon;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class WebResourcesTests extends BaseTests {

	private static String pathToInitJson = System.getProperty("dukecon.apitests.pathToInitJson");
	private static String pathToImageResourcesJson = System.getProperty("dukecon.apitests.pathToImageResourcesJson");
	private static String pathToConferences = "/rest/conferences/javaland2019";
	private static String pathToStylesCss = "/rest/conferences/javaland2019/styles.css";
	private static String pathToFavicon = "/img/favicon.ico";
	private static String pathToKeycloakJson = "/rest/keycloak.json";

	@Test
	public void testInitJson() {
		whenUrlOkAndContentTypeMatches(pathToInitJson, ContentType.JSON.toString(),document("initJson"))
			.assertThat()
			.statusCode(200)
			.body(matchesJsonSchemaInClasspath("schemas/init.json"));
	}

	@Test
	public void testImageResourceJson() {
		whenUrlOkAndContentTypeMatches(pathToImageResourcesJson, ContentType.JSON.toString(),document("image-resources"))
			.assertThat()
			.statusCode(200)
			.body(matchesJsonSchemaInClasspath("schemas/image-resources.json"));
	}

		@Test
	public void testConferences() {
			whenUrlOkAndContentTypeMatches(pathToConferences, ContentType.JSON.toString(),document("conferencesJson"))
				.assertThat()
				.statusCode(200)
				.body(notNullValue());
			//TODO check body?
	}

	@Test
	public void testSpeakerImage() {
		//TODO content type in accept header is blocked by apache?
		//TODO get image from conferences?
		whenUrlOk("/rest/speaker/images/c4ab6b88490cb4da5f6ea95dae485095", document("speaker-image"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());;
		//TODO check body?
	}

	@Test
	public void testStyles() {
		whenUrlOkAndContentTypeMatches(pathToStylesCss, "text/css", document("stylesCss"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
		//TODO check body?
	}

	@Test
	public void testFavicon() {
		whenUrlOkAndContentTypeMatches(pathToFavicon, "image/x-icon", document("favicon"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
		  //TODO check body?
	}

	@Test
	public void testKeycloakJson() {
		whenUrlOkAndContentTypeMatches(pathToKeycloakJson, ContentType.JSON.toString(), document("keycloakJson"))
			.assertThat()
			.statusCode(200)
			.body(matchesJsonSchemaInClasspath("schemas/keycloak.json"));;
	}

}