package org.apache.bigtop.manager.server.model.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ScriptInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.vo.TaskVO;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskVO Entity2VO(Task task);

    @Mapping(target = "customCommands", expression = "java(str2CustomCommands(task.getCustomCommands()))")
    @Mapping(target = "commandScript", expression = "java(str2ScriptInfo(task.getCommandScript()))")
    @Mapping(target = "osSpecifics", expression = "java(str2OsSpecifics(task.getOsSpecifics()))")
    @Mapping(target = "jobId", source = "task.job.id")
    @Mapping(target = "stageId", source = "task.stage.id")
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "messageId", ignore = true)
    CommandMessage DTO2CommandMessage(Task task);

    default Map<String, ScriptInfo> str2CustomCommands(String customCommands) {
        try {
            return JsonUtils.readFromString(customCommands, new TypeReference<>() {
            });
        } catch (Exception ignored) {
        }
        return null;
    }

    default List<OSSpecificInfo> str2OsSpecifics(String osSpecifics) {
        try {
            return JsonUtils.readFromString(osSpecifics, new TypeReference<>() {
            });
        } catch (Exception ignored) {
        }
        return null;
    }

    default ScriptInfo str2ScriptInfo(String commandScript) {
        try {
            return JsonUtils.readFromString(commandScript, new TypeReference<>() {
            });
        } catch (Exception ignored) {
        }
        return null;
    }
}
