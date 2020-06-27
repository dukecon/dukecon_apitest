package org.dukecon;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class EventsBookingTests extends BaseTests {

	private String eventId = UUID.randomUUID().toString(); //TODO not validated, that the event exists in that conference??
	private String userToken = gatherToken();
	private String badToken = UUID.randomUUID().toString();

	@Test
	public void testEventsBookingGet() {
		whenUrlOkAndContentTypeMatches("/rest/eventsBooking/javaland2019", ContentType.JSON.toString(), document("eventsBookingGet"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
	}

	@Test
	public void testEventsBookingGetAuthenticated() {
		whenAuthenticatedAndContentTypeMatches(userToken,"/rest/eventsBooking/javaland2019", ContentType.JSON.toString(), document("eventsBookingGetWithAuth"))
			.assertThat()
			.statusCode(200)
			.body(notNullValue());
	}

	@Test void testEventBookingsUpdateIsSecured() {
		eventsBookingPost(badToken, eventId, "{\"fullyBooked\":false,\"numberOccupied\":\"10\"}", document("eventsBookingPostWithBadAuthCreate"))
			.assertThat()
			//TODO this should return 401/403
			.statusCode(201)
			.body(equalTo("{\"numberOccupied\":10,\"fullyBooked\":false}"));
	}

	@Test void testEventBookingsUpdate() {

		eventsBookingPost(userToken, eventId, "{\"fullyBooked\":false,\"numberOccupied\":\"10\"}", document("eventsBookingPostWithAuthCreate"))
			.assertThat()
			.statusCode(201)
			.body(equalTo("{\"numberOccupied\":10,\"fullyBooked\":false}"));

		//TODO verify that the created eventbooking exists in the conference?

		//TODO check that updating an existing eventBooking works
		eventsBookingPost(userToken, eventId, "{\"fullyBooked\":true,\"numberOccupied\":\"11\"}", document("eventsBookingPostWithAuthUpdate"))
			.assertThat()
			.statusCode(204)
			.body(emptyString());

	}

	//TODO limit overflow?

	private ValidatableResponse eventsBookingPost(String token, String eventId, String eventsBookingContent, RestDocumentationFilter documentationFilter) {
		return given(this.spec).auth().oauth2(token)
			.body(eventsBookingContent)
			.contentType(ContentType.JSON)
			.filter(documentationFilter)
			.post(String.format("rest/eventsBooking/javaland2019/%s",eventId))
			.then();
	}
}
