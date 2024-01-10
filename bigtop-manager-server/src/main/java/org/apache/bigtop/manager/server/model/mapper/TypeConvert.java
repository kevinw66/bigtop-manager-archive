package org.apache.bigtop.manager.server.model.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.mapstruct.Named;

import java.util.List;

public class TypeConvert {

    @Named("obj2Json")
    public <T> String obj2Json(T obj) {
        return JsonUtils.writeAsString(obj);
    }

    @Named("json2List")
    public List<String> json2List(String json) {
        return JsonUtils.readFromString(json, new TypeReference<>() {
        });
    }
}
