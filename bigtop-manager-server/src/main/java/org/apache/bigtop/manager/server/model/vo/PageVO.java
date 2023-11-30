package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
public class PageVO<T> {

    private Long total;

    private List<T> content;

    public static <T> PageVO<T> of(List<T> content, Long total) {
        PageVO<T> res = new PageVO<>();
        res.setContent(content);
        res.setTotal(total);
        return res;
    }

    @SuppressWarnings("unchecked")
    public static <T, S> PageVO<T> of(Page<S> page) {
        List<T> content = new ArrayList<>();
        if (page.hasContent()) {
            try {
                Class<S> clz = (Class<S>) page.getContent().get(0).getClass();
                String className = "org.apache.bigtop.manager.server.model.mapper." + clz.getSimpleName() + "Mapper";
                Class<?> mapper = Class.forName(className);
                Object o = Mappers.getMapper(mapper);
                Method method = o.getClass().getDeclaredMethod("fromEntity2VO", List.class);
                content = (List<T>) method.invoke(o, page.getContent());
            } catch (Exception e) {
                throw new ServerException(e);
            }
        }

        PageVO<T> res = new PageVO<>();
        res.setContent(content);
        res.setTotal(page.getTotalElements());
        return res;
    }
}
