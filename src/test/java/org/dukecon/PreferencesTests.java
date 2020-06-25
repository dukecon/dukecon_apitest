package org.dukecon;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class PreferencesTests extends BaseTests {

	private String userToken = gatherToken();
	private String preferencesContentEmpty = "[]";
	private String preferencesContent = generateStringFromResource("samples/preferences.json");

	public PreferencesTests() throws IOException {
	}
	
	//TODO there is a cvs variant !

	@Test
	public void testPreferencesSetEmpty() {

		updatePreferences(userToken, preferencesContentEmpty, document("preferencesInit"));

		whenAuthenticatedUrlOkAndContentTypeMatches(userToken, "/rest/preferences", ContentType.JSON.toString(), document("preferencesInitCheck"))
			.and()
			.body(Matchers.equalTo(preferencesContentEmpty));
	}

	@Test
	public void testPreferencesUpdateAndRead() {
		updatePreferences(userToken, preferencesContent, document("preferencesUpdate"));

		whenAuthenticatedUrlOkAndContentTypeMatches(userToken, "/rest/preferences", ContentType.JSON.toString(), document("preferencesUpdateCheck"));
		// TODO	Answer is broken?
		//.and()
		//.body(Matchers.equalTo(preferencesContent));
	}

	@Test
	public void testFavoritesAllSet() {
		whenAuthenticatedUrlOkAndContentTypeMatches(userToken, "/rest/favorites/javaland2019", ContentType.JSON.toString(), document("favoritesGetAll"));
	}

	private ValidatableResponse updatePreferences(String userToken, String preferencesContent, RestDocumentationFilter documentationFilter) {
		return given(this.spec).auth().oauth2(userToken)
			.body(preferencesContent)
			.contentType(ContentType.JSON)
			.filter(documentationFilter)
			.post("/rest/preferences")
			.then()
			.assertThat()
			.statusCode(201); // TODO initial create 201, updates 204?
	}

}
