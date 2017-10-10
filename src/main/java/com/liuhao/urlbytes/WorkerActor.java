package com.liuhao.urlbytes;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;


public class WorkerActor extends AbstractActor {
  static public Props props() {
    return Props.create(WorkerActor.class, () -> new WorkerActor());
  }

  public static class Result{
    private URL url;
    private int bytes;
    public Result(URL url,int bytes) {
      this.url = url;
      this.bytes = bytes;
    }
    public URL getUrl() {
      return url;
    }

    public int getBytes() {
      return bytes;
    }
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(URL.class, url -> {
            int bytes = doWork(url);
            Result res = new Result(url,bytes);
            //tell the result to
            getContext().getParent().tell(res,ActorRef.noSender());
            getContext().stop(getSelf());
        })
        .build();
  }

  private int doWork(URL url) {
    int bytes = 0;
    try {
      InputStream in =url.openStream();
      while (in.available()!=0) {
        //read a byte
        in.read();
        bytes++;
      }
      in.close();
      return bytes;
    } catch (Exception e) {
      return -1;
    }
  }
}
