package com.fintech.sst.net.bean;


import java.util.List;

public class PageList<T> {

    private PageMsg pageMsg;

    private List<T> list;

    public PageMsg getPageMsg() {
        return pageMsg;
    }

    public void setPageMsg(PageMsg pageMsg) {
        this.pageMsg = pageMsg;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageList{" +
                "pageMsg=" + pageMsg +
                ", list=" + list +
                '}';
    }
}
