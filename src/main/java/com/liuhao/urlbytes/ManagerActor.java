package com.liuhao.urlbytes;

import java.util.Arrays;
import java.util.List;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.liuhao.urlbytes.MasterActor.*;


public class ManagerActor {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("url_stat");
        final ActorRef masterActor =
                system.actorOf(MasterActor.props(), "masterActor");
        List<String> urls = Arrays.asList("https://baidu.com",
                "https://taobao.com", "http://www.csdn.com",
                "http://www.ctrip.com", "https://www.hao123.com");

        // masterActor得到url的列表，然后再传递给WorkerActor。WorkerActor执行url的网页的抓取，把每个网页的bytes值发送给masterActor.
        // masterActor最后输出，一共5个网页，共计xxx bytes.
        masterActor.tell(urls, ActorRef.noSender());
    }
}
