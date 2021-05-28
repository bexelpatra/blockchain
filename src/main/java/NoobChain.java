import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

/*
    참고자료 : https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa

    참고자료라기 보다는 필사하는 느낌으로 공부해봤다.

    1. 블록체인을 만들고 data를 저장한다.
    2. 전자서명을 통해 블록을 연결한다.
    3. pow 알고리즘을 이용해서 새로운 블록의 타당성을 검증한다.

 */
public class NoobChain {

    public static ArrayList<Block> blockChain = new ArrayList<>();
    public static int difficulty = 5; //  채굴 난이도. hash에서 앞에 0이붙는 개수
    // 아직 사용되지 않은 transactionout들, 잔고개념으로 볼 수 있다.
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<>();

    // 임의의 지갑 두개
    public static Wallet walletA;
    public static Wallet walletB;

    public static float minimumTransaction = 0.1f; // 최소 전송 금액
    public static Transaction genesisTransaction; // 시작 트랜잭션
    public static void main(String[] args) {

        // todo chapter 1
        //  make block
//        Block genesisBlock = new Block("hello block","0");
//        System.out.println(String.format("Hash for block 1 : %s",genesisBlock.hash));
//
//        Block secondBlock = new Block("i am second block",genesisBlock.hash);
//        System.out.println(String.format("Hash for block 2 : %s",secondBlock.hash));
//
//        Block thirdBlock = new Block("i am second block",secondBlock.hash);
//        System.out.println(String.format("Hash for block 3 : %s",thirdBlock.hash));

        // todo chapter 2
        //  chaining block
//        blockChain.add(new Block("hello block, i am first","0"));
//        blockChain.add(new Block("second",blockChain.get(blockChain.size()-1).hash));
//        blockChain.add(new Block("third",blockChain.get(blockChain.size()-1).hash));
//
//        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
//        System.out.println(blockchainJson);

        // todo chapter 3
        //  mining
//        blockChain.add(new Block("hello block", "0"));
//        System.out.println("Trying to Mine block 1... ");
//        blockChain.get(0).mineBlock(difficulty);
//
//        blockChain.add(new Block("second",blockChain.get(blockChain.size()-1).hash));
//        System.out.println("Trying to Mine block 2... ");
//        blockChain.get(1).mineBlock(difficulty);
//
//        blockChain.add(new Block("third",blockChain.get(blockChain.size()-1).hash));
//        System.out.println("Trying to Mine block 3... ");
//        blockChain.get(2).mineBlock(difficulty);
//
//        System.out.println("\nBlockchain is Valid: " + isChainValid());
//
//        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
//        System.out.println("\nThe block chain: ");
//        System.out.println(blockchainJson);

        // todo chapter 4
        //  wallet

//        Security.addProvider(new BouncyCastleProvider());
//
//        walletA = new Wallet();
//        walletB = new Wallet();
//
//        System.out.println("Private and public keys : ");
//        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
//        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
//
//        Transaction transaction = new Transaction(walletA.publicKey,walletB.publicKey,5,null);
//        transaction.generateSignature(walletA.privateKey);
//
//        System.out.println("Is signature verified");
//        System.out.println(transaction.verifySignature());

        // security provider로 bouncyCastle을 사용
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // 지갑 생성하기
        // 지갑은 트랜잭션 결과와 잔액을 저장한다.
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        //최초 트랜잭션 생성하기, 100NoobChain을 지급
        // 이외의 지급 조건이 없으므로 100Noob이 끝
        // 비트코인의 경우 새롭게 블록을 채굴하면 처음 50bit 부터 시작해서 4년마다 반감기를 거치면서 지급한다. 2021년 기준 블록 1개를 채굴하면 6.25bit 지급
        // 비트코인에서 새로운 채굴 난이도는 10분에 한개가 생성되도록 조정된다. 하루 2,102,000개 생성
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction
        genesisTransaction.transactionId = "0"; //manually set the transaction id
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //testing
        Block block1 = new Block(genesis.hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 1));
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 2));
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 3));
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 4));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();

    }

//    public static Boolean isChainValid(){
//        Block currentBlock;
//        Block previousBlock;
//        String hashTarget = new String(new char[difficulty]).replace('\0','0');
//
//        for(int i=1; i<blockChain.size();i++){
//            currentBlock = blockChain.get(i);
//            previousBlock = blockChain.get(i-1);
//
//            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
//                System.out.println("current hashes not equal");
//                return false;
//            }
//
//            if(!previousBlock.hash.equals(currentBlock.previousHash)){
//                System.out.println("previous hashes not equal");
//                return false;
//            }
//            if(!currentBlock.hash.substring(0,difficulty).equals(hashTarget)){
//                System.out.println("this block hasn't been mined");
//                return false;
//            }
//        }
//        return true;
//    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        //loop through blockchain to check hashes:
        for(int i=1; i < blockChain.size(); i++) {

            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if( currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockChain.add(newBlock);
    }

}
