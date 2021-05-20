import util.StringUtil;

import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;

    //constructor
    public Block(String data, String previousHash) {
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash(){
        String calculatehash = StringUtil.applySha256(
                previousHash+
                        Long.toString(timeStamp)+
                        Integer.toString(nonce)+
                        data);
        return calculatehash;
    }

    public void mineBlock(int difficulty){
        String target = new String(new char[difficulty]).replace('\0','0');

        while(!hash.substring(0,difficulty).equals(target)){
            nonce++;

            hash = calculateHash();
        }
        System.out.println("Block Mined !!! : " + hash);
    }
}
