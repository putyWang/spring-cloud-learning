package com.learning.orm.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName: PageUtils
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public class PageUtils<T> {
    private List<T> data;
    private int pageSize;

    public PageUtils(List<T> data, int pageSize) {
        this.data = data;
        this.pageSize = pageSize;
    }

    public List<T> page(int pageNum) {
        if (pageNum < 1) {
            pageNum = 1;
        }

        int from = (pageNum - 1) * this.pageSize;
        int to = Math.min(pageNum * this.pageSize, this.data.size());
        if (from > to) {
            from = to;
        }

        return this.data.subList(from, to);
    }

    public int getPageCount() {
        if (this.pageSize == 0) {
            return 0;
        } else {
            return this.data.size() % this.pageSize == 0 ? this.data.size() / this.pageSize : this.data.size() / this.pageSize + 1;
        }
    }

    public Iterator<List<T>> iterator() {
        return new PageUtils.Itr();
    }

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        System.out.println("原始数据是：" + list);
        int pageSize = 2;
        System.out.println("每页大小是：" + pageSize);
        PageUtils<Integer> pager = new PageUtils(list, pageSize);
        System.out.println("总页数是: " + pager.getPageCount());
        System.out.println("<- - - - - - - - - - - - - ->");
        Iterator iterator = pager.iterator();

        while(iterator.hasNext()) {
            List<Integer> next = (List)iterator.next();
            System.out.println("next: " + next);
        }

        System.out.println("<- - - - - - - - - - - - - ->");

        for(int i = 1; i <= pager.getPageCount(); ++i) {
            List<Integer> page = pager.page(i);
            System.out.println("第 " + i + " 页数据是:" + page);
        }

    }

    private class Itr implements Iterator<List<T>> {
        int page = 1;

        Itr() {
        }

        public boolean hasNext() {
            return this.page <= PageUtils.this.getPageCount();
        }

        public List<T> next() {
            int i = this.page;
            if (i > PageUtils.this.getPageCount()) {
                return new ArrayList();
            } else {
                this.page = i + 1;
                return PageUtils.this.page(i);
            }
        }
    }
}
