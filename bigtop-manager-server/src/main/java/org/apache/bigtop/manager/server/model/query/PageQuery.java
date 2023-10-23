package org.apache.bigtop.manager.server.model.query;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class PageQuery {

    private Integer pageNum;

    private Integer pageSize;

    private Sort sort;
}
