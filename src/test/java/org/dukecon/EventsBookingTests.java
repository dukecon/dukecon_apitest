package org.dukecon;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class EventsBookingTests extends BaseTests {

	private final String badToken = UUID.randomUUID().toString();

	private final String userToken = new TokenGatherer().gatherUserToken();
	private static String pathToEventsBooking = "/javaland/2019/rest/eventsBooking/javaland2019";

	@Test
	public void testEventsBookingGet() {
		whenUrlOkAndContentTypeMatches(pathToEventsBooking, ContentType.JSON.toString(), document("eventsBookingGet"))
			.assertThat()
			.statusCode(200)
			.body(matchesJsonSchemaInClasspath("schemas/eventsBooking.json"));
	}

	@Test
	public void testEventsBookingGetAuthenticated() {
		whenAuthenticatedAndContentTypeMatches(userToken,pathToEventsBooking, ContentType.JSON.toString(), document("eventsBookingGetWithAuth"))
			.assertThat()
			.statusCode(200)
			.and()
			.body(matchesJsonSchemaInClasspath("schemas/eventsBooking.json"));
	}

	@Test
	@EnabledIfSystemProperty(named = "dukecon.apitests.authEnabled", matches = "true")
	public void testEventBookingsUpdateIsSecured() {
		String eventId = UUID.randomUUID().toString(); //TODO get date from conference?

		eventsBookingPost(badToken, eventId, "{\"fullyBooked\":false,\"numberOccupied\":\"10\"}", document("eventsBookingPostWithBadAuthCreate"))
			.assertThat()
			//TODO this should return 401/403
			.statusCode(201)
			.body(equalTo("{\"numberOccupied\":10,\"fullyBooked\":false}"));
	}

	@Test
	public void testEventBookingsUpdate() {
		String eventId = UUID.randomUUID().toString(); //TODO get date from conference?

		eventsBookingPost(userToken, eventId, "{\"fullyBooked\":true,\"numberOccupied\":\"11\"}", document("eventsBookingPostWithAuthCreate"))
			.assertThat()
			.statusCode(201)
			.body(equalTo("{\"numberOccupied\":11,\"fullyBooked\":true}"));

		//TODO verify that the created eventbooking exists in the conference?

		//TODO check that updating an existing eventBooking works
		eventsBookingPost(userToken, eventId, "{\"fullyBooked\":false,\"numberOccupied\":\"10\"}", document("eventsBookingPostWithAuthUpdate"))
			.assertThat()
			.statusCode(204)
			.body(emptyString());

	}

	//TODO limit overflow?
	//TODO crosscheck with feedback?

	private ValidatableResponse eventsBookingPost(String token, String eventId, String eventsBookingContent, RestDocumentationFilter documentationFilter) {
		return given(this.spec).auth().oauth2(token)
			.body(eventsBookingContent)
			.contentType(ContentType.JSON)
			.filter(documentationFilter)
			.post(String.format("%s/%s",pathToEventsBooking,eventId))
			.then();
	}
}
