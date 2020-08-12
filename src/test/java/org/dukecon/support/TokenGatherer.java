package org.dukecon.support;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TokenGatherer {

	private final boolean authEnabled = Boolean.valueOf(System.getProperty("dukecon.apitests.auth.enabled"));

	private final String authTokenEndpoint = System.getProperty("dukecon.apitests.auth.tokenEndpoint");
	private final String authClientIdUserRole = System.getProperty("dukecon.apitests.auth.userToken.clientId");
	private final String authClientSecretUserRole = System.getProperty("dukecon.apitests.auth.userToken.secret");
	private final String authClientIdAdminRole = System.getProperty("dukecon.apitests.auth.adminToken.clientId");
	private final String authClientSecretAdminRole = System.getProperty("dukecon.apitests.auth.adminToken.secret");

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
