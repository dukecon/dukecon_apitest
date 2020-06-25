package org.dukecon;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class EventsBookingTests extends BaseTests {

	String eventId = UUID.randomUUID().toString(); //TODO not validated, that the event exists in that conference??
	String userToken = gatherToken();

	@Test
	public void testEventsBookingGet() {
		whenUrlOkAndContentTypeMatches("/rest/eventsBooking/javaland2019", ContentType.JSON.toString(), document("eventsBookingGet"));
	}

	@Test
	public void testEventsBookingGetAuthenticated() {
		whenAuthenticatedUrlOkAndContentTypeMatches(userToken,"/rest/eventsBooking/javaland2019", ContentType.JSON.toString(), document("eventsBookingGetWithAuth"));
	}

	@Test void testEventBookingsUpdate() {

		eventsBookingPost(userToken, eventId, "{\"fullyBooked\":false,\"numberOccupied\":\"10\"}", document("eventsBookingPostWithAuthCreate"))
			.statusCode(201);

		//TODO verify that the created eventbooking exists in the conference?

		//TODO check that updating an existing eventBooking works
		eventsBookingPost(userToken, eventId, "{\"fullyBooked\":true,\"numberOccupied\":\"11\"}", document("eventsBookingPostWithAuthUpdate"))
			.statusCode(204);

		//TODO limit increase?

	}

	private ValidatableResponse eventsBookingPost(String token, String eventId, String eventsBookingContent, RestDocumentationFilter documentationFilter) {
		return given(this.spec).auth().oauth2(token)
			.body(eventsBookingContent)
			.contentType(ContentType.JSON)
			.filter(documentationFilter)
			.post(String.format("rest/eventsBooking/javaland2019/%s",eventId))
			.then()
			.assertThat();
	}
}
