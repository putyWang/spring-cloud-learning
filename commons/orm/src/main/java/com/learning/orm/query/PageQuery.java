package com.learning.orm.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Setter;

/**
 * @ClassName: PageQuery
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Setter
public class PageQuery {
    private long page = 1L;
    private long pageSize = 10L;

    public Page getPageObj() {
        return new Page(this.page, this.pageSize);
    }

    public long getPageSize() {
        return this.pageSize == 0L ? 10L : this.pageSize;
    }

    public long getPage() {
        return this.page == 0L ? 1L : this.page;
    }
}
