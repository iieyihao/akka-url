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

  public WorkerActor() {
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(String.class, url -> {
            int bytes = doWork(url);
            //tell the result to
            getContext().getParent().tell(url+":" +bytes,ActorRef.noSender());
            getContext().stop(getSelf());
        })
        .build();
  }

  private int doWork(String urls) {
    int bytes = 0;
    try {
      URL url = new URL(urls);
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
