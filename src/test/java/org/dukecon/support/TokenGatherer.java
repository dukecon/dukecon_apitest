package org.dukecon.support;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TokenGatherer {

	private final boolean authEnabled = Boolean.valueOf(System.getProperty("dukecon.apitests.authEnabled"));
	private final String authTokenEndpoint = "https://keycloak.dukecon.org/auth/realms/dukecon-latest/protocol/openid-connect/token";
	private final String authClientSecretUserRole = "97d8f2d3-d55c-46bd-aafd-bb4d9e058955";
	private final String authClientSecretAdminRole = "61ba244a-70a3-47c2-8c11-bdb0e7a1c5c4";
	private final String authClientIdUserRole = "dukecon_api_user";
	private final String authClientIdAdminRole = "dukecon_api_admin";

	public TokenGatherer() {	}

	public String gatherUserToken() {
		return gatherToken(this.authClientIdUserRole, this.authClientSecretUserRole);
	}

	public String gatherAdminToken() {
		return gatherToken(this.authClientIdAdminRole, this.authClientSecretAdminRole);
	}

	private String gatherToken(String authClientIdUserRole, String authClientSecretUserRole) {
		if (!authEnabled) {
			return "securityIsDisabled";
		}

		Response response = given()
			.config(RestAssured.config()
				.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs("x-www-form-urlencoded",
						ContentType.URLENC)))
			.contentType("application/x-www-form-urlencoded; charset=UTF-8")
			.formParam("grant_type", "client_credentials")
			.formParam("client_id", authClientIdUserRole)
			.formParam("client_secret", authClientSecretUserRole)
			.post(authTokenEndpoint);

		response.then()
			.statusCode(200);

		JsonPath jsonPathEvaluator = response.jsonPath();

		return jsonPathEvaluator.get("access_token");
	}

}
