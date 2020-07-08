package org.dukecon;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.dukecon.support.BaseTests;
import org.dukecon.support.TokenGatherer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;

import java.io.IOException;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class PreferencesTests extends BaseTests {

	private final String userToken = new TokenGatherer().gatherUserToken();
	private final String badToken = UUID.randomUUID().toString();

	private final String preferencesContentEmpty = "[]";
	private final String preferencesContent = generateStringFromResource("samples/preferences.json");

	private final String pathToPreferences = "/javaland/2019/rest/preferences";
	private final String pathToFavorites = "/javaland/2019/rest/favorites/javaland2019";

	public PreferencesTests() throws IOException {
	}
	
	//TODO there is a cvs variant !

	@Test
	@EnabledIfSystemProperty(named = "dukecon.apitests.authEnabled", matches = "true")
	public void testPreferencesIsSecured() {
		whenAuthenticatedAndContentTypeMatches(userToken, pathToPreferences, ContentType.JSON.toString(), document("preferencesIsSecured"))
			.assertThat()
			//TODO this should return 401/403?
			.statusCode(200);
	}

	@Test
	@EnabledIfSystemProperty(named = "dukecon.apitests.authEnabled", matches = "true")
	public void testPreferencesUpdateIsSecured() {
		updatePreferences(badToken, preferencesContentEmpty, document("preferencesUpdateIsSecured"))
			.assertThat()
			.statusCode(401);
	}

	@Test
	public void testPreferencesSetEmpty() {
		updatePreferences(userToken, preferencesContentEmpty, document("preferencesInit"))
			.assertThat()
			.statusCode(201);

		whenAuthenticatedAndContentTypeMatches(userToken, pathToPreferences, ContentType.JSON.toString(), document("preferencesInitCheck"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue())
			.and()
			.body(Matchers.equalTo(preferencesContentEmpty));
	}

	@Test
	public void testPreferencesUpdateAndRead() {
		updatePreferences(userToken, preferencesContent, document("preferencesUpdate"))
			.assertThat()
			.statusCode(201);

		whenAuthenticatedAndContentTypeMatches(userToken, pathToPreferences, ContentType.JSON.toString(), document("preferencesUpdateCheck"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
		// TODO	Answer is broken?
		//.and()
		//.body(Matchers.equalTo(preferencesContent));
	}

	@Test
	public void testFavoritesAllSet() {
		whenAuthenticatedAndContentTypeMatches(userToken, pathToFavorites, ContentType.JSON.toString(), document("favoritesGetAll"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
	}

	private ValidatableResponse updatePreferences(String userToken, String preferencesContent, RestDocumentationFilter documentationFilter) {
		return given(this.spec)
			.auth().oauth2(userToken)
			.body(preferencesContent)
			.contentType(ContentType.JSON)
			.filter(documentationFilter)
			.post(pathToPreferences)
			.then();
	}

}
