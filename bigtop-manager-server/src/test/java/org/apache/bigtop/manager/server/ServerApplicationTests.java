package org.apache.bigtop.manager.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void queryMonitoring() {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:9090").build();
        Mono<String> body = webClient.get().uri("/api/v1/query?query=absent(up{job=bm-agent-host}==1)", "").retrieve().bodyToMono(String.class);
        System.out.println(body.block());
    }

}
