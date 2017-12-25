/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import log.*;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import java.io.IOException;
import java.util.ArrayList;
import pt.haslab.ekit.Clique;
import pt.haslab.ekit.Log;

/**
 *
 * @author Ricardo
 */
public class Communicate {
    private final Address[] addresses;
    private final Log l;
    private final Transport t;
    private final ThreadContext tc;
    private ArrayList<Integer> users;
    private final Clique c;
    private int value;
    private int num;

    public Communicate (Address[] addr, Transport t, ThreadContext tc, Clique c, Log l){
        this.addresses = addr;
        this.t = t;
        this.tc = tc;
        this.c = c;
        this.users = new ArrayList<>();
        this.value = 0;
        this.l = l;
        this.num = 0;
    }
    
    private void registEvents(){
        tc.execute(() -> {
            c.handler(Integer.class, (j,m) -> {
                l.append(j);
            });
            c.handler(Prepare.class, (sender, msg)-> {
                System.out.println("Prepare");
                l.append(msg);
                c.send(0,new Ok("ok"));
            });
            c.handler(Commit.class, (sender, msg)-> {
                System.out.println("Commit");
                l.append(msg);
            });
            c.handler(Ok.class, (sender, msg)->{
                System.out.println("Ok");
                if((addresses.length - 2) == 1){
                    l.append(new Commit("commit"));
                    num = 0;
                    System.out.println("send");
                    for(int i = 1; i < addresses.length ; i++){
                        c.send(i, new Commit("commit"));
                    }
                }
                else num++;

            });
            c.handler(Abort.class, (sender, msg)->{
                System.out.println("Abort");
                l.append(msg);
                num = 0;
                for(int i = 1; i < addresses.length ; i++){
                    c.send(i,new Rollback("rollback"));
                }
            });
            c.handler(Rollback.class, (sender,msg) ->{
                System.out.println("Rollback");
                l.append(new Abort("abort"));
            });
            c.onException(e->{
                System.out.println("Erro: "+ e.getMessage() +". :(");
            });

            c.onClose( e -> {
                System.out.println("Erro2: "+ e);
            });
        }).join();  
    }
    
    public void start() throws IOException{
         //regist events
         registEvents();
     }
}
