package com.liuhao.urlbytes;
import java.util.*;
import java.net.URL;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.liuhao.urlbytes.WorkerActor.Result;

public class MasterActor extends AbstractActor {
  static public Props props() {
    return Props.create(MasterActor.class);
  }

  //hold the urls list from ManagerActor
  private List<String> urls = null;
  //hold the results from wokers
  private List<Result> results = new ArrayList<Result>();

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(List.class, list -> { //list of urls
          urls = (List<String>) list;
          Iterator it = list.iterator();
          int i=0;
          while(it.hasNext()) {
            URL url = new URL(it.next().toString());
            System.out.println("sending:" + url);
            ActorRef worker = getContext().actorOf(WorkerActor.props(),"worker"+(i++));
            worker.tell(url,ActorRef.noSender());
          }
        })
        .match(Result.class, result -> { //result from work
          results.add(result);
          System.out.println("Get result:" + result.getUrl().toString() + ":" + result.getBytes());
          //all urls done by workers
          if (results.size() == urls.size()) {
            System.out.println("All done");
            Iterator it = results.iterator();
            int bytes_sum = 0;
            //get the sum of bytes
            while(it.hasNext()) {
              int bytes = ((Result)(it.next())).getBytes();
              if (bytes != -1) bytes_sum += bytes;
            }
            System.out.println("total proccessed pages:"+results.size() + ",total bytes:" + bytes_sum);
            getContext().stop(getSelf());
            //terminate the application
            getContext().system().terminate();
          }
        })
        .build();
  }
}
