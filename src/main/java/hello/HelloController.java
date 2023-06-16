package hello;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String index() {
        StringBuilder thirdPartyContent = new StringBuilder();

        String serviceURL = "http://wiremock-1686939037";

        if(System.getenv("SERVICE_URL") != null) {
            serviceURL = System.getenv("SERVICE_URL");
        }

        try {
            URL url = new URL(serviceURL + "/v1/hello");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                thirdPartyContent.append(inputLine);
            }

            in.close();
            con.disconnect();

        } catch (IOException e) {
            System.out.println("Failed to connect to " + serviceURL + ": " + e.toString());
        }

        return "Greetings from Spring Boot and Jib! Content from 3rd party: " + thirdPartyContent;
    }
}