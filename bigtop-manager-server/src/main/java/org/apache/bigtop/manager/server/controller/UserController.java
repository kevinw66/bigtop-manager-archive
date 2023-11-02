/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.dto.UserDTO;
import org.apache.bigtop.manager.server.model.mapper.UserMapper;
import org.apache.bigtop.manager.server.model.req.UserReq;
import org.apache.bigtop.manager.server.model.vo.UserVO;
import org.apache.bigtop.manager.server.service.UserService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Controller")
@RestController
@RequestMapping("/users")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "current", description = "Get current user")
    @GetMapping("/current")
    public ResponseEntity<UserVO> current() {
        return ResponseEntity.success(userService.current());
    }

    @Operation(summary = "update", description = "Update a user")
    @PutMapping()
    public ResponseEntity<UserVO> update(@RequestBody @Validated UserReq userReq) {
        UserDTO userDTO = UserMapper.INSTANCE.Req2DTO(userReq);
        return ResponseEntity.success(userService.update(userDTO));
    }
}
