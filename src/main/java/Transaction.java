import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    public String transactionId;// transaction의 hash
    public PublicKey sender; // 보내는 사람의 공개키
    public PublicKey recipient; // 받는 사람의 공개키
    public float value; // 보내는 금액
    public byte[] signature;// 보내는 사람의 전자 서명

    public ArrayList<TransactionInput> inputs = new ArrayList<>(); // 보내는 사람의 지갑에 있는 트랜잭션에서 UTXOs를 입력값으로 사용한다. ex)13200원을 보내려면 만원 1장, 천원 3장, 백원 2개
    public ArrayList<TransactionOutput> outputs = new ArrayList<>(); // 트랜잭션을 입력해서 나오는 결과 트랜잭션. 수신자, 채굴자, 본인(거스름돈같은 개념)에게 주는 등 다양한 output이 나올 수 있다.

    private static int sequence =0; // 몇번째 트랜잭션인지 센다.

    /**
     *
     * @param from 보내는 사람
     * @param to    받는 사람
     * @param value 보내는 금액
     * @param inputs 자신의 UTXOs에서 뽑아온 입력값
     */
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // 입력받은 값들을 이용해서 hash값을 만든다.
    private String calculateHash(){
        sequence++;
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender)+
                        StringUtil.getStringFromKey(recipient)+
                        Float.toString(value) +
                        sequence
        );
    }

    // 서명하기 :  개인키를 사용해서 전자서명을 한다.
    public void generateSignature(PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)+ Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }
    // 서명 검증하기 : 전자서명을 검증한다.
    public boolean verifySignature(){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender,data,signature);
    }

    // 트랜잭션
    public boolean processTransaction(){
        // #1 서명 검증하기
        if(verifySignature() == false){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }
        // #2 ledge에서 내가 사용하려는 TX가 기록된 UTXOs를 찾는다.
        for (TransactionInput input : inputs) {
            input.UTXO = NoobChain.UTXOs.get(input.transactionOutputId);
        }

        // #3 입력 금액이 최소 TX금액보다 큰지 확인한다.
        if(getInputsValue() < NoobChain.minimumTransaction){
            System.out.println("#Transaction Inputs to small " + getInputsValue());
            return false;
        }

        // #4 보내는 금액 계산하기 : TX에서 보내는 금액이 크면 다시 남는 금액을 돌려 받는 TXO를 생성한다.
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient,value,transactionId));
        outputs.add(new TransactionOutput(this.sender,leftOver,transactionId));

        // #5 TXOs 를 ledge에 저장한다.
        for (TransactionOutput output : outputs) {
            NoobChain.UTXOs.put(output.id,output);
        }

        // #6 입력값에 사용된 TXI을 ledge(UTXOs)에서 삭제한다.
        for (TransactionInput input : inputs) {
            if(input.UTXO == null) continue;
            NoobChain.UTXOs.remove(input.UTXO.id);
        }
        return true;
    }

    // ledge에서 입력하려는 값들의 합을 구한다.
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            total += i.UTXO.value;
        }
        return total;
    }

    // TX의 결과로 나온 값들의 합을 구한다.
    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

}
