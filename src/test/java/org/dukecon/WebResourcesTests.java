package org.dukecon;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class WebResourcesTests extends BaseTests {

	private static String pathToInitJson = System.getProperty("dukecon.apitests.pathToInitJson");
	private static String pathToImageResourcesJson = System.getProperty("dukecon.apitests.pathToImageResourcesJson");

	@Test
	public void testInitJson() {
		whenUrlOkAndContentTypeMatches(pathToInitJson, ContentType.JSON.toString(),document("init"))
			.body(matchesJsonSchemaInClasspath("samples/init.json"));
	}

	@Test
	public void testImageResourceJson() {
		JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
			.setValidationConfiguration(
				ValidationConfiguration.newBuilder()
					.setDefaultVersion(SchemaVersion.DRAFTV4)
					.freeze()).freeze();
		JsonSchemaValidator.settings = settings()
			.with().jsonSchemaFactory(factory)
			.and().with().checkedValidation(true);

		whenUrlOkAndContentTypeMatches(pathToImageResourcesJson, ContentType.JSON.toString(),document("image-resources"))
			.body(matchesJsonSchemaInClasspath("samples/image-resources.json").using(settings().with().jsonSchemaFactory()));
	}

		@Test
	public void testConferences() {
		whenUrlOkAndContentTypeMatches("/rest/conferences/javaland2019", ContentType.JSON.toString(),document("conferences"));
	}

	@Test
	public void testSpeakerImage() {
		//TODO content type in accept header is blocked by apache?
		whenUrlOk("/rest/speaker/images/c4ab6b88490cb4da5f6ea95dae485095", document("speaker-image"));
	}

	@Test
	public void testStyles() {
		whenUrlOkAndContentTypeMatches("/rest/conferences/javaland2019/styles.css", "text/css", document("styles"));
	}

	@Test
	public void testFavicon() {
		whenUrlOkAndContentTypeMatches("/img/favicon.ico", "image/x-icon", document("favicon"));
	}

	@Test
	public void testKeycloakJson() {
		whenUrlOkAndContentTypeMatches("/rest/keycloak.json", ContentType.JSON.toString(), document("keycloak"));
	}

}