package org.apache.bigtop.manager.server.model.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.dto.PropertyDTO;
import org.apache.bigtop.manager.server.model.vo.PropertyVO;
import org.mapstruct.Named;

import java.util.List;

public class TypeConvert {

    @Named("obj2Json")
    public <T> String obj2Json(T obj) {
        return JsonUtils.writeAsString(obj);
    }

    @Named("json2List")
    public List<String> json2List(String json) {
        return JsonUtils.readFromString(json, new TypeReference<>() {});
    }

    @Named("json2PropertyDTOList")
    public List<PropertyDTO> json2PropertyDTOList(String json) {
        return JsonUtils.readFromString(json, new TypeReference<>() {});
    }

    @Named("json2PropertyVOList")
    public List<PropertyVO> json2PropertyVOList(String json) {
        return JsonUtils.readFromString(json, new TypeReference<>() {});
    }
}
