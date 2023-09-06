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
package org.apache.bigtop.manager.server.enums;

import lombok.Getter;

@Getter
public enum LocaleKeys {
    REQUEST_SUCCESS("request.success"),
    REQUEST_FAILED("request.failed"),
    PARAMETER_ERROR("request.parameter.incorrect"),
    NOTEMPTY("NotEmpty"),
    NOTNULL("NotNull"),

    LOGIN_REQUIRED("login.required"),
    LOGIN_ACCOUNT_REQUIRED("login.account.required"),
    LOGIN_ACCOUNT_INCORRECT("login.account.incorrect"),

    CLUSTER_NOT_FOUND("cluster.not.found"),

    HOST_NOT_FOUND("host.not.found"),

    STACK_NOT_FOUND("stack.not.found"),
    STACK_CHECK_INVALID("stack.check.invalid"),

    SERVICE_NOT_FOUND("service.not.found"),

    COMPONENT_NOT_FOUND("component.not.found"),
    ;

    private final String key;

    LocaleKeys(String key) {
        this.key = key;
    }
}
