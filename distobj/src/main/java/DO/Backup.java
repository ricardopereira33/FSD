package DO;

import DO.DO;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Connection;
import pt.haslab.ekit.Clique;

public class Backup {
    private int id;
    private DO d;
    private ThreadContext tc;
    private Connection c;
    private Clique cli;

    public Backup(){}

    public Backup(DO d, ThreadContext tc, Connection c, Clique cli, int id){
        this.d  = d.clone();
        setConnects(tc,c,cli);
        this.id = id;
    }

    public DO getD() {
        return d;
    }

    public ThreadContext getTc() {
        return tc;
    }

    public Connection getC() {
        return c;
    }

    public Clique getCli() {
        return cli;
    }

    public int getId() {
        return id;
    }

    public void update(DO d, ThreadContext tcManager, Connection conManager, Clique cli, int id) {
        this.d  = d.clone();
        setConnects(tcManager,conManager,cli);
        this.id = id;
    }

    private void setConnects(ThreadContext tc, Connection c, Clique cli){
        if(tc != null)
            this.tc = tc;
        else this.tc = null;
        if(tc != null)
            this.c = c;
        else this.c = null;
        if(tc != null)
            this.cli = cli;
        else this.cli = null;
    }
}
