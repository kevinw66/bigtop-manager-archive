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
package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.holder.SessionUserHolder;
import org.apache.bigtop.manager.server.model.dto.UserDTO;
import org.apache.bigtop.manager.server.model.mapper.UserMapper;
import org.apache.bigtop.manager.server.model.vo.UserVO;
import org.apache.bigtop.manager.server.orm.entity.User;
import org.apache.bigtop.manager.server.orm.repository.UserRepository;
import org.apache.bigtop.manager.server.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Override
    public UserVO current() {
        Long id = SessionUserHolder.getUserId();
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ApiExceptionEnum.NEED_LOGIN));
        return UserMapper.INSTANCE.Entity2VO(user);
    }

    @Override
    public UserVO update(Long id, UserDTO userDTO) {
        User user = UserMapper.INSTANCE.DTO2Entity(userDTO);
        user.setId(id);

        User storedUser = userRepository.getReferenceById(id);

        if (user.getPassword() == null) {
            user.setPassword(storedUser.getPassword());
        }
        if (user.getStatus() == null) {
            user.setStatus(storedUser.getStatus());
        }
        if (user.getNickname() == null) {
            user.setNickname(storedUser.getNickname());
        }
        user.setUsername(storedUser.getUsername());

        userRepository.save(user);

        return UserMapper.INSTANCE.Entity2VO(user);
    }
}
