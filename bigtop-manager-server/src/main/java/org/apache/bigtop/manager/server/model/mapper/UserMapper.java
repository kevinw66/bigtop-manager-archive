package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.UserDTO;
import org.apache.bigtop.manager.server.model.req.UserReq;
import org.apache.bigtop.manager.server.model.vo.UserVO;
import org.apache.bigtop.manager.server.orm.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    UserVO fromEntity2VO(User user);

    UserDTO fromReq2DTO(UserReq userReq);
}
