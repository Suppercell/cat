package com.dianping.cat.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.cat.message.Event;

public class ProblemErrorCache {
    private static Map<String,LimitList<Event>> map=new ConcurrentHashMap<String, LimitList<Event>>();

    public static void  put(String domain, Event event){
        synchronized (map){
            LimitList<Event> list=map.get(domain);
            if (list==null){
                list=new LimitList<Event>(5);
            }
            list.add(event);
            map.put(domain,list);
        }
    }

    public  static LimitList<Event> get(String domain){
        return map.get(domain);
    }

}
