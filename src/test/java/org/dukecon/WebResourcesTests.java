package org.dukecon;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class WebResourcesTests extends BaseTests {

	private static String pathToInitJson = System.getProperty("dukecon.apitests.pathToInitJson");
	private static String pathToImageResourcesJson = System.getProperty("dukecon.apitests.pathToImageResourcesJson");
	private static String pathToFavicon = System.getProperty("dukecon.apitests.pathToFavicon");
	private static String pathToKeycloakJson = System.getProperty("dukecon.apitests.pathToKeycloakJson");

	private static String pathToSpeakerImage = System.getProperty("dukecon.apitests.pathToSpeakerImage");

	private static String pathToStylesCss = System.getProperty("dukecon.apitests.pathToStylesCss");

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
	public void testSpeakerImage() {
		//@ ignore when run without apache?
		//TODO content type in accept header is blocked by apache?
		//TODO get image from conferences?
		whenUrlOk(pathToSpeakerImage, document("speaker-image"))
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
	//@ ignore when run without apache?
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