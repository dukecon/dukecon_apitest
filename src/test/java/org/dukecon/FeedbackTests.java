package org.dukecon;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class FeedbackTests extends BaseTests {

	private final String eventId = UUID.randomUUID().toString(); //TODO not validated, that the event exists in that conference??
	private final String userToken = new TokenGatherer().gatherUserToken();

	private final String badToken = UUID.randomUUID().toString();

	private final String pathToFeedback = "/rest/feedback/event/javaland2019/%s";
	private final String pathToEventFeedback = String.format(pathToFeedback, eventId);

	@Test
	@EnabledIfSystemProperty(named = "dukecon.apitests.authEnabled", matches = "true")
	public void testFeedbackGiveIsSecured() {

		given(this.spec)
			.filter(document("feedbackPut"))
			.body("{\"comment\":\"test\",\"rating\":3}")
			.contentType(ContentType.JSON)
			.auth().oauth2(badToken)
			.put(pathToEventFeedback)
			.then()
			.assertThat()
			.statusCode(401);

	}

	@Test
	public void testFeedbackGive() {

		given(this.spec)
			.filter(document("feedbackPut"))
			.body("{\"comment\":\"test\",\"rating\":3}")
			.contentType(ContentType.JSON)
			.auth().oauth2(userToken)
			.put(String.format("/rest/feedback/event/javaland2019/%s",eventId))
			.then()
			.assertThat()
			.statusCode(201) //TODO always created???
			.body(Matchers.emptyString());
	}

	//TODO no get?
}
