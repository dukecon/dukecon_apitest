package org.dukecon;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.swing.text.AbstractDocument;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class FeedbackTests extends BaseTests {

	String eventId = UUID.randomUUID().toString(); //TODO not validated, that the event exists in that conference??
	String userToken = gatherToken();

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
			.statusCode(201); //TODO always created???

		//TODO no get?
	}

}
