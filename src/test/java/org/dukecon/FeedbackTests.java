package org.dukecon;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.dukecon.support.BaseTests;
import org.dukecon.support.TokenGatherer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class FeedbackTests extends BaseTests {

	private final String eventId = UUID.randomUUID().toString(); //TODO not validated, that the event exists in that conference??

	private final String badToken = UUID.randomUUID().toString();
	private final String userToken = new TokenGatherer().gatherUserToken();

	private final String pathToFeedback = "/rest/feedback/event/javaland2019/%s";
	private final String pathToEventFeedback = String.format(pathToFeedback, eventId);

	//TODO future plans: read feedback, verify?

	@Test
	@EnabledIfSystemProperty(named = "dukecon.apitests.authEnabled", matches = "true")
	public void testFeedbackGiveIsSecured() {
		putFeedback(badToken, document("feedbackPutNotAuthorized"))
			.assertThat()
			.statusCode(401);
	}

	@Test
	public void testFeedbackGive() {
		putFeedback(userToken, document("feedbackPut"))
			.assertThat()
			.statusCode(201) //TODO always created???
			.body(Matchers.emptyString());
	}

	private ValidatableResponse putFeedback(String token, RestDocumentationFilter documentationFilter) {
		return given(this.spec)
			.filter(documentationFilter)
			.body("{\"comment\":\"test\",\"rating\":3}")
			.contentType(ContentType.JSON)
			.auth().oauth2(token)
			.put(pathToEventFeedback)
			.then();
	}

}
