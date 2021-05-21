package bgu.spl.net.impl.messages;


import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;

public abstract class Message<T> {
    private short opcode;
    private boolean type;
    //private T MessageParams;
    String result;
    short resultType;

    public Message(Short opcode){ //, T MessageParams){
        this.opcode = opcode;
        this.resultType = 13;
    }

    /*//public T getParams(){
        return MessageParams;
    }*/

    public short getOpcode() { return opcode; }

    public boolean getType() {return type;}

    public void setType(boolean type) { this.type = type;}

    public abstract void build(T params);

    public String getResult() {
        return result;
    }

    public Short getResultType() {
        return resultType;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setResultType(short resultType) {
        this.resultType = resultType;
    }

    public abstract void execute(Database DB, User user);
}