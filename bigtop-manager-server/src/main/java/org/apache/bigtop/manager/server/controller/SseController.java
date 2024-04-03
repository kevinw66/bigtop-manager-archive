/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Tag(name = "Sse Controller")
@RestController
@RequestMapping("/sse/clusters/{clusterId}")
public class SseController {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Operation(summary = "get task log", description = "Get a task log")
    @GetMapping("/tasks/{id}/log")
    public SseEmitter log(@PathVariable Long id, @PathVariable Long clusterId) {
        SseEmitter emitter = new SseEmitter();

        executor.execute(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    emitter.send("[INFO ] " + getFormattedTime() + " - " + "This info message!");
//                    emitter.send("[WARN ] " + getFormattedTime() + " - " + "This warn message!");
//                    emitter.send("[ERROR] " + getFormattedTime() + " - " + "This error message!");
                    Thread.sleep(1000);
                }
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private String getFormattedTime() {
        ZoneId zoneId = ZoneOffset.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String time = zonedDateTime.format(formatter);
        String offset = zonedDateTime.getOffset().getId().replace("Z", "+00:00");

        return time + " " + offset;
    }
}
