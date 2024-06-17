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
    private int page = 1;
    private int pageSize = 10;

    public Page getPageObj() {
        return new Page(this.page, this.pageSize);
    }

    public int getPageSize() {
        return this.pageSize == 0 ? 10 : this.pageSize;
    }

    public int getPage() {
        return this.page == 0 ? 1 : this.page;
    }
}
