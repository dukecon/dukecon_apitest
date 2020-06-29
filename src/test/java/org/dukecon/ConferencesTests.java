package org.dukecon;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class ConferencesTests extends BaseTests {

	private static String pathToConferences = System.getProperty("dukecon.apitests.pathToConferences");
	private static String pathToConferenceById = System.getProperty("dukecon.apitests.pathToConferenceById");
	private static String pathToConferenceUpdateById = System.getProperty("dukecon.apitests.pathToConferenceUpdateById");

	@Test
	public void testConferences() {
		whenUrlOkAndContentTypeMatches(pathToConferences, ContentType.JSON.toString(),document("conferencesGet"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
		//TODO check body?
	}

	@Test
	public void testConferencesGetById() {
		whenUrlOkAndContentTypeMatches(pathToConferenceById, ContentType.JSON.toString(),document("conferencesGetById"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
		//TODO check body?
	}

	@Test
	public void testConferencesUpdateByIdIsSecured() {
		whenAuthenticatedAndContentTypeMatches(new TokenGatherer().gatherUserToken(),pathToConferenceUpdateById, ContentType.JSON.toString(),document("conferencesUpdateById"))
			.assertThat()
			.statusCode(403)
			.body(notNullValue());
		//TODO check body?
	}

}
