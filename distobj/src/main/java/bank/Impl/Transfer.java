package bank.Impl;

public class Transfer {
    private String send;
    private int value;

    public Transfer(String s, int v){
        send = s;
        value = v;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
