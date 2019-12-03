package com.dianping.cat.support;

import java.util.ArrayList;
import java.util.List;

public class LimitList<T> {
    private List<T> list;

    private int     limit;

    private int     point;

    public LimitList(int limit) {
        this.list = new ArrayList<T>();
        this.limit = limit;
        this.point = 0;
    }

    public void add(T t) {
            if (list.size() < limit) {
                list.add(t);
            } else {
                list.remove(point);
                list.add(point, t);
                point++;
                if (point == limit) {
                    point = 0;
                }
            }
    }

    public List<T> getList() {
        return list;
    }
}
