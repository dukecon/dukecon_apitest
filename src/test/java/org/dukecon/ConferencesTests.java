package org.dukecon;

import io.restassured.http.ContentType;
import org.dukecon.support.BaseTests;
import org.dukecon.support.TokenGatherer;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class ConferencesTests extends BaseTests {

	private static String pathToConferences = System.getProperty("dukecon.apitests.pathToConferences");
	private static String pathToConferenceById = System.getProperty("dukecon.apitests.pathToConferenceById");
	private static String pathToConferenceByIdSpeakers = System.getProperty("dukecon.apitests.pathToConferenceByIdSpeakers");
	private static String pathToConferenceByIdEvents = System.getProperty("dukecon.apitests.pathToConferenceByIdEvents");
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
	public void testConferencesGetByIdEvents() {
		whenUrlOkAndContentTypeMatches(pathToConferenceByIdEvents, ContentType.JSON.toString(),document("conferencesGetByIdEvents"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
		//TODO check body?
	}

	@Test
	public void testConferencesGetByIdSpeakers() {
		whenUrlOkAndContentTypeMatches(pathToConferenceByIdSpeakers, ContentType.JSON.toString(),document("conferencesGetByIdSpeakers"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
		//TODO check body?
	}

	@Test
	// TODO might not be necessary?
	public void testConferencesUpdateByIdIsSecured() {
		whenAuthenticatedAndContentTypeMatches(new TokenGatherer().gatherUserToken(),pathToConferenceUpdateById, ContentType.JSON.toString(),document("conferencesUpdateByIdIsSecured"))
			.assertThat()
			.statusCode(403)
			.body(notNullValue());
		//TODO check body?
	}

	@Test
	// TODO might not be necessary?
	public void testConferencesUpdateById() {
		whenAuthenticatedAndContentTypeMatches(new TokenGatherer().gatherAdminToken(),pathToConferenceUpdateById, ContentType.JSON.toString(),document("conferencesUpdateById"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
		//TODO check body?
	}

}
