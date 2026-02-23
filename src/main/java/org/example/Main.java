package org.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class Main {
    public static void main(String[] args) {
        WireMockServer server = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(8080)
                        // Forwards unmatched requests to the original target host instead of returning 404
                        .enableBrowserProxying(true)
        );

        server.start();
        System.out.println("WireMock server started on port " + server.port());

        server.stubFor(get(urlEqualTo("/hello"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Hello, World!\"}")
                        .withStatus(200)));

        server.stubFor(post(urlEqualTo("/echo"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withTransformers("response-template")
                        .withBody("{{request.body}}")
                        .withStatus(200)));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            System.out.println("WireMock server stopped.");
        }));
    }
}
