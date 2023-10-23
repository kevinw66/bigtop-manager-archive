package org.apache.bigtop.manager.server.utils;

import org.apache.bigtop.manager.server.model.query.PageQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Sort;

public class PageUtils {

    private static final String PAGE_NUM = "pageNum";

    private static final String PAGE_SIZE = "pageSize";

    private static final String ORDER_BY = "orderBy";

    private static final String SORT = "sort";

    private static final Integer DEFAULT_PAGE_NUM = 1;

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    private static final String DEFAULT_ORDER_BY = "id";

    private static final String DEFAULT_SORT = "desc";

    public static PageQuery getPageQuery() {
        PageQuery query = new PageQuery();
        query.setPageNum(NumberUtils.toInt(ServletUtils.getParameter(PAGE_NUM), DEFAULT_PAGE_NUM) - 1);
        query.setPageSize(NumberUtils.toInt(ServletUtils.getParameter(PAGE_SIZE), DEFAULT_PAGE_SIZE));

        String orderBy = StringUtils.defaultIfBlank(ServletUtils.getParameter(ORDER_BY), DEFAULT_ORDER_BY);
        String sort = StringUtils.defaultIfBlank(ServletUtils.getParameter(SORT), DEFAULT_SORT);
        if (DEFAULT_SORT.equals(sort)) {
            query.setSort(Sort.by(orderBy).descending());
        } else {
            query.setSort(Sort.by(orderBy).ascending());
        }

        return query;
    }
}
