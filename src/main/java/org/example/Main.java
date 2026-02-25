package org.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class Main {
    public static void main(String[] args) {
        WireMockServer server = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(8080)
                        .httpsPort(8443)
                        // Forwards unmatched requests to the original target host instead of returning 404
                        .enableBrowserProxying(true)
                        // CA keystore for dynamic per-hostname certificate generation during HTTPS proxying
                        .caKeystorePath("ca-keystore.jks")
                        .caKeystorePassword("password")
        );

        server.start();
        System.out.println("WireMock server started on HTTP port " + server.port() + " and HTTPS port " + server.httpsPort());

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
