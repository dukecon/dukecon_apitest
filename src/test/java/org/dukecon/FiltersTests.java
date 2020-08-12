package org.dukecon;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.dukecon.support.BaseTests;
import org.dukecon.support.TokenGatherer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class FiltersTests extends BaseTests {

	private final String userToken = new TokenGatherer().gatherUserToken();
	private final String badToken = UUID.randomUUID().toString();

	private final String pathToFilters = System.getProperty("dukecon.apitests.pathToFilters");

	@Test
	@EnabledIfSystemProperty(named = "dukecon.apitests.auth.enabled", matches = "true")
	public void testFiltersIsSecured() {
		whenAuthenticatedAndContentTypeMatches(badToken, pathToFilters, ContentType.JSON.toString(), document("filtersGetSecured"))
			.assertThat()
			.statusCode(401)
			.body(Matchers.notNullValue());
	}

	@Test
	@EnabledIfSystemProperty(named = "dukecon.apitests.auth.enabled", matches = "true")
	public void testFilters() {
		whenAuthenticatedAndContentTypeMatches(userToken, pathToFilters, ContentType.JSON.toString(), document("filtersGet"))
			.assertThat()
			.statusCode(200)
			.body(Matchers.notNullValue());
	}

	@Test
	@EnabledIfSystemProperty(named = "dukecon.apitests.auth.enabled", matches = "true")
	public void testSetFilters() {

		String filtersData = "{\"favourites\":true,\"levels\":[],\"languages\":[],\"tracks\":[],\"locations\":[]}";

		//If there would be a way to remove the filters of a user / reset
		//Alternative would be to create a new user ...
		putFilters(userToken, filtersData, document("filtersSet"))
			.assertThat()
			.statusCode(anyOf(is(200),is(201))); // ... for a new user it must be 201 (will fail initially)

		whenAuthenticatedAndContentTypeMatches(userToken, pathToFilters, ContentType.JSON.toString(), document("filtersGetAfterSet"))
			.assertThat()
			.statusCode(200)
			.body(Matchers.equalToObject(filtersData));
	}

	private ValidatableResponse putFilters(String token, String filtersData, RestDocumentationFilter documentationFilter) {
		return given(this.spec)
			.filter(documentationFilter)
			.body(filtersData)
			.contentType(ContentType.JSON)
			.auth().oauth2(token)
			.put(pathToFilters)
			.then();
	}

}
