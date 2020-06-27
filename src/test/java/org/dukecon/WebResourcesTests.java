package org.dukecon;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class WebResourcesTests extends BaseTests {

	private static String pathToInitJson = System.getProperty("dukecon.apitests.pathToInitJson");
	private static String pathToImageResourcesJson = System.getProperty("dukecon.apitests.pathToImageResourcesJson");
	private String pathToConferences = "/rest/conferences/javaland2019";
	private String pathToStylesCss = "/rest/conferences/javaland2019/styles.css";
	private String pathToFavicon = "/img/favicon.ico";
	private String pathToKeycloakJson = "/rest/keycloak.json";

	@Test
	public void testInitJson() {
		whenUrlOkAndContentTypeMatches(pathToInitJson, ContentType.JSON.toString(),document("init"))
			.assertThat()
			.statusCode(200)
			.body(matchesJsonSchemaInClasspath("samples/init.json"));
	}

	@Test
	public void testImageResourceJson() {
		whenUrlOkAndContentTypeMatches(pathToImageResourcesJson, ContentType.JSON.toString(),document("image-resources"))
			.assertThat()
			.statusCode(200)
			.body(matchesJsonSchemaInClasspath("samples/image-resources.json").using(settings().with().jsonSchemaFactory()));
	}

		@Test
	public void testConferences() {
			whenUrlOkAndContentTypeMatches(pathToConferences, ContentType.JSON.toString(),document("conferences"))
				.assertThat()
				.statusCode(200)
				.body(notNullValue());
	}

	@Test
	public void testSpeakerImage() {
		//TODO content type in accept header is blocked by apache?
		//TODO get image from conferences?
		whenUrlOk("/rest/speaker/images/c4ab6b88490cb4da5f6ea95dae485095", document("speaker-image"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());;
	}

	@Test
	public void testStyles() {
		whenUrlOkAndContentTypeMatches(pathToStylesCss, "text/css", document("styles"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
	}

	@Test
	public void testFavicon() {
		whenUrlOkAndContentTypeMatches(pathToFavicon, "image/x-icon", document("favicon"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
	}

	@Test
	public void testKeycloakJson() {
		whenUrlOkAndContentTypeMatches(pathToKeycloakJson, ContentType.JSON.toString(), document("keycloak"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());;
	}

}