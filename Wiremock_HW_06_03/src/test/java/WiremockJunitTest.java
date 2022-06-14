import com.github.tomakehurst.wiremock.junit.WireMockRule;
import okhttp3.*;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class WiremockJunitTest {


    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void wiremock_rule_test() throws IOException{
        configureStabs();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/xml");
        RequestBody body = RequestBody.create(mediaType, "<users>\r\n    <user>\r\n        <name>user1</name>\r\n        <age>23</age>\r\n    </user>\r\n    <user>\r\n        <name>user2</name>\r\n        <age>30</age>\r\n    </user>\r\n    <user>\r\n        <name>user3</name>\r\n        <age>25</age>\r\n    </user>\r\n</users>");
        Request request = new Request.Builder()
                .url("http://localhost:8080/3users")
                .method("POST", body)
                .addHeader("Content-Type", "application/xml")
                .build();
        Pattern pattern = Pattern.compile("<name>user[0-9]*</name>");
        try {
            String file_content = "<users><user><name>user1</name><age>23</age></user><user><name>user1</name><age>30</age></user><user><name>user3</name><age>25</age></user></users>" ;
            Matcher matcher = pattern.matcher(file_content);
            int numberOfMatches = 3;
            assertEquals(matcher.results().count(), numberOfMatches);
        } catch (Exception e){}
    }
private void configureStabs(){
    configureFor("localhost", 8080);
    stubFor(
            post(urlEqualTo("/3users"))
                    .withRequestBody(matchingXPath("users[count(user[age > 18]) = 3]")).willReturn(aResponse().withStatus(200).withUniformRandomDelay(440, 460)));
    }
}