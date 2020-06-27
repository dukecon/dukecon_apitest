package org.dukecon;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class BaseTests {

	private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
	private static String baseUrl = System.getProperty("dukecon.apitests.baseurl");

	protected RequestSpecification spec;

	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = baseUrl;
		RestAssured.port = 443;
		RestAssured.registerParser("text/css", Parser.TEXT);
	}

	@BeforeEach
	public void setupTest() {
		this.spec = new RequestSpecBuilder()
			.addFilter(
				RestAssuredRestDocumentation.documentationConfiguration(this.restDocumentation))
			.build();
		this.restDocumentation.beforeTest(getClass(), "method.getName()");
	}

	@AfterEach
	public void tearDown() {
		this.restDocumentation.afterTest();
	}

	protected ValidatableResponse whenAuthenticatedAndContentTypeMatches(String token, String path, String contentType, RestDocumentationFilter documentationFilter) {
		return whenUrlOkAndContentTypeMatchesBase(given(this.spec).filter(documentationFilter).auth().oauth2(token), path, contentType);
	}

	protected ValidatableResponse whenUrlOkAndContentTypeMatches(String path, String contentType, RestDocumentationFilter documentationFilter) {
		return whenUrlOkAndContentTypeMatchesBase(given(this.spec).filter(documentationFilter), path, contentType);
	}

	protected ValidatableResponse whenUrlOk(String path, RestDocumentationFilter documentationFilter) {
		return whenUrlOk(given(this.spec).filter(documentationFilter),
			path);
	}

	private RestDocumentationFilter getDefaultRestDocumentationFilter(RestDocumentationFilter documentationFilter) {
		if (documentationFilter == null) {
			documentationFilter = document("default");
		}
		return documentationFilter;
	}

	private ValidatableResponse whenUrlOkAndContentTypeMatchesBase(RequestSpecification spec, String path, String contentType) {
		return
			whenUrlOk(spec.accept(contentType), path)
				.contentType(contentType);
	}

	private ValidatableResponse whenUrlOk(RequestSpecification spec, String path) {
		return spec.when()
			.get(path)
			.then();
	}

	protected String generateStringFromResource(String path) throws IOException {
		File file = new File(this.getClass().getClassLoader().getResource(path).getFile());
		return FileUtils.readFileToString(file, "UTF-8");
	}
}
