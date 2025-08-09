package com.intr.debt.it;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Shared test base:
 *  - Starts WireMock (Jetty11) once, before Spring resolves properties
 *  - Injects intrum.payout.api to point to WireMock
 *  - Provides a temp folder for wakanda file path
 *  - Disables scheduling in tests
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT {

    // Start WireMock in a static block so it's ready before @DynamicPropertySource runs
    private static final WireMockServer WIREMOCK;
    private static final Path WAKANDA_DIR;

    static {
        WIREMOCK = new WireMockServer(options().dynamicPort());
        WIREMOCK.start();
        System.out.println("[BaseIT] WireMock started on port " + WIREMOCK.port());
        try {
            WAKANDA_DIR = Files.createTempDirectory("wakanda-it");
            System.out.println("[BaseIT] Wakanda test dir: " + WAKANDA_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp dir for wakanda files", e);
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry reg) {
        String url = "http://localhost:" + WIREMOCK.port() + "/payout"; // your client posts exactly here
        reg.add("intrum.payout.api", () -> url);
        reg.add("wakanda.file.path", () -> WAKANDA_DIR.toString());
        reg.add("spring.task.scheduling.enabled", () -> "false");
        System.out.println("[BaseIT] intrum.payout.api => " + url);
    }


    protected WireMockServer wm() { return WIREMOCK; }
    protected Path wakandaDir() { return WAKANDA_DIR; }
}
