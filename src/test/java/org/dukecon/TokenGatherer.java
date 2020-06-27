package org.dukecon;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TokenGatherer {

	public TokenGatherer() {	}

	protected String gatherToken() {
		Response response = given()
			.config(RestAssured.config()
				.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs("x-www-form-urlencoded",
						ContentType.URLENC)))
			.contentType("application/x-www-form-urlencoded; charset=UTF-8")
			.formParam("grant_type", "client_credentials")
			.formParam("client_id", "dukecon_api")
			.formParam("client_secret", "97d8f2d3-d55c-46bd-aafd-bb4d9e058955")
			.post("https://keycloak.dukecon.org/auth/realms/dukecon-latest/protocol/openid-connect/token");

		response.then()
			.statusCode(200);

		JsonPath jsonPathEvaluator = response.jsonPath();

		return jsonPathEvaluator.get("access_token");
	}

}
