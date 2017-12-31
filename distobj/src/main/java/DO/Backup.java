package DO;

import DO.DO;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.transport.Connection;
import pt.haslab.ekit.Clique;

public class Backup {
    private int id;
    private DO d;
    private ThreadContext tc;
    private Connection c;
    private Clique cli;

    public Backup(DO d, ThreadContext tc, Connection c, Clique cli, int id){
        this.d = d;
        this.tc = tc;
        this.c = c;
        this.cli = cli;
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
}
