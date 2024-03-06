package org.apache.bigtop.manager.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

@Tag(name = "Monitoring Controller")
@RestController
@RequestMapping("monitoring")
public class MonitoringController {

    private final WebClient webClient;

    public MonitoringController(WebClient.Builder webClientBuilder, @Value("${monitoring.prometheus-host}") String prometheusHost) {
        this.webClient = webClientBuilder.baseUrl(prometheusHost).build();
    }

    @Operation(summary = "agent healthy", description = "agent healthy check")
    @GetMapping("agenthealthy")
    public ResponseEntity<String> agentHostsHealthyStatus() {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("query", "absent(up{job=bm-agent-host}==1)");
        Mono<ObjectNode> body = webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/query").build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("query", "absent(up{job=\"bm-agent-host\"}==1)"))
                .retrieve().bodyToMono(ObjectNode.class);

        ObjectNode result = body.block();
        ArrayNode arr = (ArrayNode) (result != null && "success".equals(result.get("status").asText()) ?
                result.get("data").get("result") : new ObjectMapper().createArrayNode());
        AtomicBoolean isRunning = new AtomicBoolean(true);
        arr.forEach(item -> {
            JsonNode jsonNode = item.get("metric");
            if (jsonNode.isEmpty()) {
                isRunning.set(false);
            }
        });
        return isRunning.get() ? ResponseEntity.success("running") : ResponseEntity.success("down");
    }

}
