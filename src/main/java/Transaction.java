import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    public String transactionId;// transaction의 hash
    public PublicKey sender;
    public PublicKey recipient;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence =0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash(){
        sequence++;
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender)+
                        StringUtil.getStringFromKey(recipient)+
                        Float.toString(value) +
                        sequence
        );
    }

    public void generateSignature(PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)+ Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature(){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender,data,signature);
    }

    public boolean processTransaction(){

        if(verifySignature() == false){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }
        for (TransactionInput input : inputs) {
            input.UTXO = NoobChain.UTXOs.get(input.transactionOutputId);
        }

        if(getInputsValue() < NoobChain.minimumTransaction){
            System.out.println("#Transaction Inputs to small " + getInputsValue());
            return false;
        }

        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient,value,transactionId));
        outputs.add(new TransactionOutput(this.sender,leftOver,transactionId));

        for (TransactionOutput output : outputs) {
            NoobChain.UTXOs.put(output.id,output);
        }

        for (TransactionInput input : inputs) {
            if(input.UTXO == null) continue;
            NoobChain.UTXOs.remove(input.UTXO.id);
        }
        return true;
    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            total += i.UTXO.value;
        }
        return total;
    }

    //returns sum of outputs:
    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

}
