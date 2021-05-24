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
    public static int difficulty = 5;
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<>();
    public static Wallet walletA;
    public static Wallet walletB;

    public static int minimumTransaction;
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

        Security.addProvider(new BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();

        System.out.println("Private and public keys : ");
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

        Transaction transaction = new Transaction(walletA.publicKey,walletB.publicKey,5,null);
        transaction.generateSignature(walletA.privateKey);

        System.out.println("Is signature verified");
        System.out.println(transaction.verifySignature());

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


}
