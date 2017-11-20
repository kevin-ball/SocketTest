

/**
 * Quick and dirty plaveholder for the server message
 */
public class ServerMessage {

    protected ServerMessageType type;
    protected int src;
    protected int dest;
    protected int playerID;
    protected String msg;

    public ServerMessage(ServerMessageType type, int src, int dest, String msg) {
        this.type = type;
        this.src = src;
        this.dest = dest;
        this.playerID = src;
        this.msg = msg;
    }
    public ServerMessage(ServerMessageType type, String msg) {
        this(type,-1, -1,msg);
    }
    public ServerMessage() {
        this(ServerMessageType.Other,-1,-1,"Dummy Message");
    }


    public String toStringHeader() {
        String ret = type + " src: " + src + " dest: " + dest ;
        return ret;
    }

    public String toStringMessage() {
        String ret = " msg: " + msg ;
        return ret;
    }

    public String toString() {
        String ret = toStringHeader() + toStringMessage() ;
        return ret;
    }

    public void print() {
        System.out.println(this.toString());
    }

    // Setters & Getters
    public ServerMessageType getType() {
        return type;
    }

    public void setType(ServerMessageType type) {
        this.type = type;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getDest() {
        return dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
