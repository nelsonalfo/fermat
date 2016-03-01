package com.bitdubai.fermat_bch_plugin.layer.crypto_network.bitcoin.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.ProtocolStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoTransaction;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.BitcoinNetworkSelector;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.interfaces.BitcoinNetworkConfiguration;
import com.bitdubai.fermat_bch_api.layer.crypto_network.enums.TransactionTypes;
import com.bitdubai.fermat_bch_plugin.layer.crypto_network.bitcoin.developer.bitdubai.version_1.database.BitcoinCryptoNetworkDatabaseDao;
import com.bitdubai.fermat_bch_plugin.layer.crypto_network.bitcoin.developer.bitdubai.version_1.exceptions.CantExecuteDatabaseOperationException;

import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChainListener;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.FilteredBlock;
import org.bitcoinj.core.GetDataMessage;
import org.bitcoinj.core.Message;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.PeerEventListener;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.core.WalletEventListener;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Created by rodrigo on 10/4/15.
 */
public class BitcoinNetworkEvents implements WalletEventListener, PeerEventListener, BlockChainListener {
    /**
     * Class variables
     */
    BitcoinCryptoNetworkDatabaseDao dao;
    File walletFilename;
    final BlockchainNetworkType NETWORK_TYPE;
    final Context context;
    final NetworkParameters NETWORK_PARAMETERS;

    /**
     * Platform variables
     */
    PluginDatabaseSystem pluginDatabaseSystem;
    UUID pluginId;

    /**
     * Constructor
     * @param pluginDatabaseSystem
     */
    public BitcoinNetworkEvents(BlockchainNetworkType blockchainNetworkType, PluginDatabaseSystem pluginDatabaseSystem, UUID pluginId, File walletFilename, Context context) {
        this.NETWORK_TYPE = blockchainNetworkType;
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginId = pluginId;
        this.walletFilename = walletFilename;
        this.context = context;
        this.NETWORK_PARAMETERS = context.getParams();
    }

    @Override
    public void onPeersDiscovered(Set<PeerAddress> peerAddresses) {

    }

    @Override
    public void onBlocksDownloaded(Peer peer, Block block, FilteredBlock filteredBlock, int blocksLeft) {
        if (blocksLeft % 1000 == 0)
            System.out.println("***CryptoNetwork*** Block downloaded on " + NETWORK_TYPE.getCode() + ". Pending blocks: " + blocksLeft);
        //System.out.println("*****CryptoNetwork " + filteredBlock.toString());
    }



    @Override
    public void onChainDownloadStarted(Peer peer, int blocksLeft) {
        System.out.println("***CryptoNetwork*** Blockchain Download started for " + NETWORK_TYPE.getCode() + " network. Blocks left: " + blocksLeft);
    }

    @Override
    public void onPeerConnected(Peer peer, int peerCount) {
        StringBuilder logAggresive = new StringBuilder("***CryptoNetwork*** New Peer connection on " + NETWORK_TYPE.getCode() + "  to " + peer.toString());
        logAggresive.append(System.getProperty("line.separator"));
        logAggresive.append("Total connected peers for Network " + NETWORK_TYPE.getCode() + ": " + peerCount);
        System.out.println(logAggresive.toString());
    }

    @Override
    public void onPeerDisconnected(Peer peer, int peerCount) {

    }

    @Override
    public Message onPreMessageReceived(Peer peer, Message m) {
        return null;
    }

    @Override
    public void onTransaction(Peer peer, Transaction t) {
        System.out.println("Transaction on Crypto Network:" + t.toString());
    }

    @Nullable
    @Override
    public List<Message> getData(Peer peer, GetDataMessage m) {
        return null;
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        /**
         * I will save the wallet after a change.
         */
        try {
            wallet.saveToFile(walletFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Register the new incoming transaction into the database
         */
        saveIncomingTransaction(wallet, tx);
    }

    /**
     * Registers the outgoing transaction
     * @param wallet
     * @param tx
     * @param prevBalance
     * @param newBalance
     */
    @Override
    public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {

    }

    /**
     * Saves into database a transaction that we are sending.
     * @param wallet
     * @param tx
     * @param transactionId
     */
    public void saveOutgoingTransaction(Wallet wallet, Transaction tx, UUID transactionId) {
        /**
         * Register the new outgoing transaction into the database
         */
        try {
            getDao().saveNewOutgoingTransaction(transactionId,
                    tx.getHashAsString(),
                    getBlockHash(tx),
                    NETWORK_TYPE,
                    CryptoStatus.ON_CRYPTO_NETWORK,
                    tx.getConfidence().getDepthInBlocks(),
                    getOutgoingTransactionAddressTo(wallet,tx),
                    getOutgoingTransactionAddressFrom(tx),
                    getOutgoingTransactionValue(wallet,tx),
                    getTransactionOpReturn(tx),
                    ProtocolStatus.TO_BE_NOTIFIED);

            /**
             * If the transaction is already under blocks, I will save it.
             */
            if (CryptoTransaction.getTransactionCryptoStatus(tx) != CryptoStatus.ON_CRYPTO_NETWORK){
                if (CryptoTransaction.getTransactionCryptoStatus(tx) == CryptoStatus.IRREVERSIBLE){
                    saveMissingOutgoingTransaction(wallet, tx, CryptoStatus.ON_BLOCKCHAIN);
                    saveMissingOutgoingTransaction(wallet, tx, CryptoTransaction.getTransactionCryptoStatus(tx));
                }
                if (CryptoTransaction.getTransactionCryptoStatus(tx) == CryptoStatus.ON_BLOCKCHAIN)
                    saveMissingOutgoingTransaction(wallet, tx, CryptoTransaction.getTransactionCryptoStatus(tx));
            }
        } catch (Exception e) {
            /**
             * if there is an error in getting information from the transaction object.
             * I will try saving the transaction with minimal information.
             * I will complete this info in the agent that triggers the events.
             */
            e.printStackTrace();
            try{
                CryptoAddress errorAddress = new CryptoAddress("error", CryptoCurrency.BITCOIN);
                getDao().saveNewOutgoingTransaction(transactionId,
                        tx.getHashAsString(),
                        getBlockHash(tx),
                        NETWORK_TYPE,
                        CryptoTransaction.getTransactionCryptoStatus(tx),
                        0,
                        errorAddress,
                        errorAddress,
                        0,
                        "",
                        ProtocolStatus.TO_BE_NOTIFIED);
            } catch (CantExecuteDatabaseOperationException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Extracts from the outputs, the ones used to generate the op_Return and the value with in it.
     * if it doesn't has any, returns and empty string.
     * @param tx
     * @return
     */
    private String getTransactionOpReturn(Transaction tx) {
        String hash = "";
        try{
            for (TransactionOutput output : tx.getOutputs()){
                /**
                 * if this is an OP_RETURN output, I will get the hash
                 */
                if (output.getScriptPubKey().isOpReturn()){
                    /**
                     * I get the chunks of the Script to get the op_Return value
                     */
                    for (ScriptChunk chunk : output.getScriptPubKey().getChunks()){
                        if (chunk.equalsOpCode(64))
                            hash = new String(chunk.data);
                    }
                }
            }
        } catch (Exception e){
            return "";
        }
        return hash;
    }

    @Override
    public void onReorganize(Wallet wallet) {

    }

    @Override
    public synchronized void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
        /**
         * Depending this is a outgoing or incoming transaction, I will set the CryptoStatus
         */
        CryptoStatus cryptoStatus = CryptoTransaction.getTransactionCryptoStatus(tx);
        try {
            if (isIncomingTransaction(tx.getHashAsString()))
                addMissingTransactions(wallet, tx, cryptoStatus, TransactionTypes.INCOMING);
            else
                addMissingTransactions(wallet, tx, cryptoStatus, TransactionTypes.OUTGOING);
        } catch (CantExecuteDatabaseOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWalletChanged(Wallet wallet) {
        /**
         * I will save the wallet after a change.
         */
        try {
            wallet.saveToFile(walletFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {

    }

    @Override
    public void onKeysAdded(List<ECKey> keys) {
        // I may need to reset the wallet in this case?
    }

    /**
     * instantiates the database object
     * @return
     */
    private BitcoinCryptoNetworkDatabaseDao getDao(){
        if (dao == null)
            dao = new BitcoinCryptoNetworkDatabaseDao(this.pluginId, this.pluginDatabaseSystem);
        return dao;
    }

    /**
     * Extracts the Address To from an Incoming Transaction
     * @param tx
     * @return
     */
    private CryptoAddress getIncomingTransactionAddressTo (Wallet wallet, Transaction tx){
        Address address = null;

        /**
         * I will loop from the outputs that include keys that are in my wallet
         */
        for (TransactionOutput output : tx.getWalletOutputs(wallet)){
            /**
             * get the address from the output
             */
            address = output.getScriptPubKey().getToAddress(NETWORK_PARAMETERS);
        }
        CryptoAddress cryptoAddress;
        if (address != null)
            cryptoAddress = new CryptoAddress(address.toString(), CryptoCurrency.BITCOIN);
        else
            cryptoAddress = new CryptoAddress("Empty", CryptoCurrency.BITCOIN);

        return cryptoAddress;
    }

    /**
     * Extracts the AddressFrom from an Incoming Transaction
     * @param tx
     * @return
     */
    private CryptoAddress getIncomingTransactionAddressFrom (Transaction tx){
        CryptoAddress cryptoAddress= null;
        try{
            Address address = null;

            for (TransactionInput input : tx.getInputs()){
                if (input.getFromAddress() != null)
                    address = input.getFromAddress();
            }

            cryptoAddress = new CryptoAddress(address.toString(), CryptoCurrency.BITCOIN);
        } catch (Exception e){
            /**
             * if there is an error, because this may not always be possible to get.
             */
            cryptoAddress = new CryptoAddress("Empty", CryptoCurrency.BITCOIN);
        }
        return cryptoAddress;
    }

    /**
     * Extracts the Address From from an Outgoing Transaction
     * @param tx
     * @return
     */
    private CryptoAddress getOutgoingTransactionAddressFrom (Transaction tx){
        return getIncomingTransactionAddressFrom(tx);
    }

    /**
     * Extracts the Address To from an outgoing Transaction
     * @param tx
     * @return
     */
    private CryptoAddress getOutgoingTransactionAddressTo (Wallet wallet, Transaction tx){
        CryptoAddress cryptoAddress = new CryptoAddress("Empty", CryptoCurrency.BITCOIN);
        Address address = null;
        try{
            for (TransactionOutput output : tx.getOutputs()){
                if (output.getScriptPubKey().isSentToAddress()){
                    address = output.getScriptPubKey().getToAddress(NETWORK_PARAMETERS);
                    cryptoAddress = new CryptoAddress(address.toString(), CryptoCurrency.BITCOIN);
                    return cryptoAddress;
                }
            }
        } catch (Exception e){
            return cryptoAddress;
        }
        return cryptoAddress;
    }

    /**
     * determines if the passed transaction is incoming or outgoing transaction
     * @param txHash
     * @return true if is an IncomingTransaction, false if is outgoing.
     */
    private boolean isIncomingTransaction(String txHash) throws CantExecuteDatabaseOperationException {
        return getDao().isIncomingTransaction(txHash);
    }

    /**
     * Creates a new platform transaction with the new crypto Status
     * @param transactionType
     * @param wallet
     * @param tx
     */
    private void setTransactionCryptoStatus(TransactionTypes transactionType , Wallet wallet, Transaction tx) {

        /**
         * I will get the previous CryptoStatus of the transaction to see if it is a Reversion
         */
        CryptoStatus storedCryptoStatus = null;
        try {
            storedCryptoStatus = getStoredTransactionCryptoStatus(transactionType, tx.getHashAsString());
        } catch (CantExecuteDatabaseOperationException e) {
            e.printStackTrace();
        }
        /**
         * Also get the current CryptoStatus that triggered the event.
         */
        CryptoStatus currentCryptoStatus = CryptoTransaction.getTransactionCryptoStatus(tx);


        /**
         * if the stored CryptoStatus is the same as the current one (for example IRREVERSIBLE)
         * then there is nothing left to do
         */
        if (storedCryptoStatus == currentCryptoStatus)
            return;

        CryptoStatus cryptoStatusToSet = null;
        switch (storedCryptoStatus) {
            case ON_CRYPTO_NETWORK:

            case ON_BLOCKCHAIN:
                /**
                 * If it was as ON_BLOCKCHAIN and now is ON_CRYPTO_NETWORK, then Is a reversion to ON CRYPTO_NETWORK
                 */
                if (currentCryptoStatus == CryptoStatus.ON_CRYPTO_NETWORK)
                    cryptoStatusToSet = CryptoStatus.REVERSED_ON_CRYPTO_NETWORK;
                else
                    cryptoStatusToSet = currentCryptoStatus;
                break;
            case IRREVERSIBLE:
                /**
                 * If the transaction was in irreversible and now is something different, is a reversion.
                 */
                if (currentCryptoStatus == CryptoStatus.ON_CRYPTO_NETWORK)
                    cryptoStatusToSet = CryptoStatus.REVERSED_ON_CRYPTO_NETWORK;
                else if (currentCryptoStatus == CryptoStatus.ON_BLOCKCHAIN)
                    cryptoStatusToSet = CryptoStatus.REVERSED_ON_BLOCKCHAIN;
                else
                    cryptoStatusToSet = currentCryptoStatus;
                break;
            default:
                cryptoStatusToSet = currentCryptoStatus;
        }

        /**
         * Now I will set the new transaction on the incoming or outgoing tables
         */
        switch (transactionType) {
            case INCOMING:
                /**
                 * Register the new incoming transaction into the database
                 */
                saveIncomingTransaction(wallet, tx);
                break;
            case OUTGOING:
                /**
                 * Register the new incoming transaction into the database
                 */
               saveOutgoingTransaction(wallet,tx, null);
                break;
        }
    }

    /**
     * gets the stored crypto status from the specified transaction.
     * @param transactionType
     * @param txHash
     * @return
     * @throws CantExecuteDatabaseOperationException
     */
    private CryptoStatus getStoredTransactionCryptoStatus(TransactionTypes transactionType, String txHash) throws CantExecuteDatabaseOperationException {
        return getDao().getStoredTransactionCryptoStatus(transactionType, txHash);
    }

    /**
     * saves the new incoming transaction into the database
     * @param wallet
     * @param tx
     */
    private void saveIncomingTransaction(Wallet wallet, Transaction tx) {
        CryptoStatus cryptoStatus = CryptoTransaction.getTransactionCryptoStatus(tx);

        /**
         * I will insert any missing previous state for this transaction
         */
        addMissingTransactions(wallet, tx, cryptoStatus, TransactionTypes.INCOMING);

        /**
         * saves into database the incoming transaction if it is new.
         */
        if (isNewTransaction(tx.getHashAsString(), cryptoStatus)) {
            try {
                getDao().saveNewIncomingTransaction(tx.getHashAsString(),
                        getBlockHash(tx),
                        NETWORK_TYPE,
                        cryptoStatus,
                        tx.getConfidence().getDepthInBlocks(),
                        getIncomingTransactionAddressTo(wallet, tx),
                        getIncomingTransactionAddressFrom(tx),
                        getIncomingTransactionValue(wallet, tx),
                        getTransactionOpReturn(tx),
                        ProtocolStatus.TO_BE_NOTIFIED);
            } catch (Exception e) {
                /**
                 * if there is an error in getting information from the transaction object.
                 * I will try saving the transaction with minimal information.
                 * I will complete this info in the agent that triggers the events.
                 */
                e.printStackTrace();
                try {
                    CryptoAddress errorAddress = new CryptoAddress("error", CryptoCurrency.BITCOIN);
                    getDao().saveNewIncomingTransaction(tx.getHashAsString(),
                            getBlockHash(tx),
                            NETWORK_TYPE,
                            CryptoTransaction.getTransactionCryptoStatus(tx),
                            0,
                            errorAddress,
                            errorAddress,
                            0,
                            "",
                            ProtocolStatus.TO_BE_NOTIFIED);
                } catch (CantExecuteDatabaseOperationException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }

    /**
     * Verifies if this transaction already exists in the database or not.
     * @param txHash
     * @param cryptoStatus
     * @return
     */
    private boolean isNewTransaction(String txHash, CryptoStatus cryptoStatus){
        try {
            return getDao().isNewTransaction(txHash, cryptoStatus);
        } catch (CantExecuteDatabaseOperationException e) {
            /**
             * if there is an error I can't risk loosing this transaction.
             */
            return true;
        }
    }

    /**
     * saves into the database any missed transaction with the corresponding CryptoStatus.
     * @param wallet
     * @param tx
     * @param missedCryptoStatus
     */
    private void saveMissingIncomingTransaction(Wallet wallet, Transaction tx, CryptoStatus missedCryptoStatus) throws CantExecuteDatabaseOperationException {
        getDao().saveNewIncomingTransaction(tx.getHashAsString(),
                getBlockHash(tx),
                NETWORK_TYPE,
                missedCryptoStatus,
                tx.getConfidence().getDepthInBlocks(),
                getIncomingTransactionAddressTo(wallet, tx),
                getIncomingTransactionAddressFrom(tx),
                getIncomingTransactionValue(wallet, tx),
                getTransactionOpReturn(tx),
                ProtocolStatus.TO_BE_NOTIFIED);
    }

    /**
     * Gets the first block Hash in which this transaction appears in
     * @param tx
     * @return
     */
    private String getBlockHash(Transaction tx) {
        try{
            for (Map.Entry<Sha256Hash, Integer> entry : tx.getAppearsInHashes().entrySet()){
                Sha256Hash hash = entry.getKey();
                return hash.toString();
            }
            /**
             * will return null if the transaction is not in a block
             */
            return null;
        } catch (Exception e){
            return null;
        }

    }

    /**
     * saves into the database any missed transaction with the corresponding CryptoStatus.
     * @param wallet
     * @param tx
     * @param missedCryptoStatus
     */
    private void saveMissingOutgoingTransaction(Wallet wallet, Transaction tx, CryptoStatus missedCryptoStatus) throws CantExecuteDatabaseOperationException {
        getDao().saveNewOutgoingTransaction(null, tx.getHashAsString(),
                getBlockHash(tx),
                NETWORK_TYPE,
                missedCryptoStatus,
                tx.getConfidence().getDepthInBlocks(),
                getOutgoingTransactionAddressTo(wallet, tx),
                getOutgoingTransactionAddressFrom(tx),
                getOutgoingTransactionValue(wallet, tx),
                getTransactionOpReturn(tx),
                ProtocolStatus.TO_BE_NOTIFIED);
    }




    /**
     * gets the value sent to me in a transaction
     * @param wallet
     * @param tx
     * @return
     */
    private long getIncomingTransactionValue(Wallet wallet, Transaction tx) {
        try{
            return tx.getValueSentToMe(wallet).getValue();
        } catch (Exception e){
            return 0;
        }
    }

    /**
     * gets the value that I'm sending in a transaction
     * @param wallet
     * @param tx
     * @return
     */
    private long getOutgoingTransactionValue(Wallet wallet, Transaction tx) {
        /**
         * I need to check the outputs for the value that is being sent
         */
        long value = 0;
        try{
            for (TransactionOutput output : tx.getOutputs()){
                if (output.getScriptPubKey().isSentToAddress())
                    value = output.getValue().getValue();
                    return value;
            }
        } catch (Exception e){
            return 0;
        }

        return value;
    }

    /**
     * Will add any missing transaction in case we detect the transaction only as inBlockChain or Irreversible
     * @param wallet
     * @param tx
     * @param currentCryptoStatus
     */
    private synchronized void addMissingTransactions(Wallet wallet, Transaction tx, CryptoStatus currentCryptoStatus, TransactionTypes transactionType){
        /**
         * I get the last store CryptoStatus from the database, if any.
         */
        CryptoStatus storedCryptoStatus = null;
        try {
            storedCryptoStatus = getDao().getStoredTransactionCryptoStatus(null, tx.getHashAsString());
        } catch (CantExecuteDatabaseOperationException e) {
            storedCryptoStatus = null;
        }

        /**
         * If I don't have any transaction and we receive ON_BLOCKCHAIN, I will insert a new one with ON_CRYPTO_NETWORK
         */
        if (currentCryptoStatus == CryptoStatus.ON_BLOCKCHAIN){
            if (storedCryptoStatus == null){
                try{
                    /**
                     * depending on the transaction type, I will add it as incoming or outgoing
                     */
                    if (transactionType == TransactionTypes.INCOMING){
                        saveMissingIncomingTransaction(wallet, tx, CryptoStatus.ON_CRYPTO_NETWORK);
                        saveMissingIncomingTransaction(wallet, tx, CryptoStatus.ON_BLOCKCHAIN);
                    }
                    //else I can't never save the Outgoing transaction on CryptoNetwork
                    else
                        saveMissingOutgoingTransaction(wallet, tx, CryptoStatus.ON_BLOCKCHAIN);
                } catch (CantExecuteDatabaseOperationException e){
                    e.printStackTrace(); //I will continue to at least save the incoming transaction.
                }
            }

            if (storedCryptoStatus == CryptoStatus.ON_CRYPTO_NETWORK){
                try{
                    /**
                     * depending on the transaction type, I will add it as incoming or outgoing
                     */
                    if (transactionType == TransactionTypes.INCOMING){
                        saveMissingIncomingTransaction(wallet, tx, CryptoStatus.ON_BLOCKCHAIN);
                    } else
                        saveMissingOutgoingTransaction(wallet, tx, CryptoStatus.ON_BLOCKCHAIN);
                } catch (CantExecuteDatabaseOperationException e){
                    e.printStackTrace(); //I will continue to at least save the incoming transaction.
                }
            }
        }

        /**
         * if the current status is IRREVERSIBLE, I will insert both ON_CRYPTO_NETWORK and ON_BLOCKCHAIN if they are missing.
         */
        if (currentCryptoStatus == CryptoStatus.IRREVERSIBLE){
            if (storedCryptoStatus == null){
                try{
                    if (transactionType == TransactionTypes.INCOMING){
                        saveMissingIncomingTransaction(wallet, tx, CryptoStatus.ON_CRYPTO_NETWORK);
                        saveMissingIncomingTransaction(wallet, tx, CryptoStatus.ON_BLOCKCHAIN);
                        saveMissingIncomingTransaction(wallet, tx, CryptoStatus.IRREVERSIBLE);
                    } else {
                        //saveMissingOutgoingTransaction(wallet, tx, CryptoStatus.ON_CRYPTO_NETWORK);
                        saveMissingOutgoingTransaction(wallet, tx, CryptoStatus.ON_BLOCKCHAIN);
                        saveMissingOutgoingTransaction(wallet, tx, CryptoStatus.IRREVERSIBLE);
                    }

                } catch (CantExecuteDatabaseOperationException e){
                    e.printStackTrace(); //I will continue to at least save the incoming transaction.
                }
            }

            if (storedCryptoStatus == CryptoStatus.ON_CRYPTO_NETWORK){
                try{
                    if (transactionType == TransactionTypes.INCOMING){
                        saveMissingIncomingTransaction(wallet, tx, CryptoStatus.ON_BLOCKCHAIN);
                        saveMissingIncomingTransaction(wallet, tx, CryptoStatus.IRREVERSIBLE);
                    } else {
                        saveMissingOutgoingTransaction(wallet, tx, CryptoStatus.ON_BLOCKCHAIN);
                        saveMissingOutgoingTransaction(wallet, tx, CryptoStatus.IRREVERSIBLE);
                    }

                } catch (CantExecuteDatabaseOperationException e){
                    e.printStackTrace(); //I will continue to at least save the incoming transaction.
                }
            }

            if (storedCryptoStatus == CryptoStatus.ON_BLOCKCHAIN){
                try{
                    if (transactionType == TransactionTypes.INCOMING){
                        saveMissingIncomingTransaction(wallet, tx, CryptoStatus.IRREVERSIBLE);
                    } else {
                        saveMissingOutgoingTransaction(wallet, tx, CryptoStatus.IRREVERSIBLE);
                    }

                } catch (CantExecuteDatabaseOperationException e){
                    e.printStackTrace(); //I will continue to at least save the incoming transaction.
                }
            }
        }
    }

    /**
     * Blockchain events
     * @param block
     * @throws VerificationException
     */
    @Override
    public void notifyNewBestBlock(StoredBlock block) throws VerificationException {

    }

    @Override
    public void reorganize(StoredBlock splitPoint, List<StoredBlock> oldBlocks, List<StoredBlock> newBlocks) throws VerificationException {

    }

    @Override
    public boolean isTransactionRelevant(Transaction tx) throws ScriptException {
        return true;
    }

    @Override
    public void receiveFromBlock(Transaction tx, StoredBlock block, AbstractBlockChain.NewBlockType blockType, int relativityOffset) throws VerificationException {
        //System.out.println("received from block " + tx.toString());
    }

    @Override
    public boolean notifyTransactionIsInBlock(Sha256Hash txHash, StoredBlock block, AbstractBlockChain.NewBlockType blockType, int relativityOffset) throws VerificationException {
        return true;
    }
}

