/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fsd.logtest;

import fsd.logtest.message.*;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import pt.haslab.ekit.Clique;
import pt.haslab.ekit.Log;

/**
 *
 * @author Ricardo
 */
public class Communicate {
    private final Address[] addresses;
    private final Log l;
    private ArrayList<Integer> users;
    private final int id;
    private final Transport t;
    private final ThreadContext tc;
    private Counter count;
    public int coord;
    private final Clique c;
    private int value;
    private int num;

    public Communicate (Address[] addr, Transport t, ThreadContext tc, Clique c, int id, Log l, Counter count){
        this.addresses = addr;
        this.t = t;
        this.tc = tc;
        this.c = c;
        this.id = id;
        this.coord = 0;
        this.users = new ArrayList<>();
        this.value = 0;
        this.l = l;
        this.num = 0;
        this.count = count;
    }
    
    private void registEvents(){
        tc.execute(() -> {
            c.handler(Integer.class, (j,m) -> {
                if(id!=coord){
                    users.add(m);
                    System.out.println("#" + id + " received: " + m + " from " + j);
                    count.incr(m);
                    l.append(value);
                }
                else {
                    l.append(j);
                }
            });
            c.handler(Prepare.class, (sender, msg)-> {
                System.out.println("Prepare");
                l.append(msg);
                c.send(coord,new Ok("ok"));
            });
            c.handler(Commit.class, (sender, msg)-> {
                System.out.println("Commit");
                l.append(msg);
            });
            c.handler(Ok.class, (sender, msg)->{
                System.out.println("Ok");
                if(id == coord){
                    if(num == 1){
                        l.append(new Commit("commit"));
                        num = 0;
                        System.out.println("send");
                        for(int i = 1; i < addresses.length ; i++){
                            c.send(i, new Commit("commit"));
                        }
                    }
                    else num++;
                }
            });
            c.handler(Abort.class, (sender, msg)->{
                System.out.println("Abort");
                l.append(msg);
                num = 0;
                for(int i = 1; i < addresses.length ; i++){
                    if(sender!=i){
                        c.send(i,new Rollback("rollback"));
                    }
                }
            });
            c.handler(Rollback.class, (sender,msg) ->{
                System.out.println("Rollback");
                l.append(new Abort("abort"));
            });
            c.onException(e->{
                System.out.println("Erro: "+ e.getMessage() +". :(");
            });
            
            c.open().thenRun(() -> {
                System.out.println("Starting...");
                
                // initialization code
                for(int i = 0; i < addresses.length ; i++){
                    if(i!=id){
                        c.send(i,33);
                    }
                }
                if(id==coord){
                    for(int i = 1; i < addresses.length ; i++){
                        c.send(i, new Prepare("prepare"));
                    }
                }
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
