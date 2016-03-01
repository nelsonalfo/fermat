package com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_user_wallet.interfaces;

import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuer;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.exceptions.CantCalculateBalanceException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.exceptions.CantRegisterCreditException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.exceptions.CantRegisterDebitException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.BalanceType;

import java.util.List;
import java.util.Map;

/**
 * Created by franklin on 05/10/15.
 */
public interface AssetUserWalletBalance {

    //TODO: Documentar
    long getBalance() throws CantCalculateBalanceException;

    List<AssetUserWalletList> getAssetUserWalletBalances() throws CantCalculateBalanceException;

    Map<ActorAssetIssuer, AssetUserWalletList> getWalletBalanceByIssuer() throws CantCalculateBalanceException;

    void debit(AssetUserWalletTransactionRecord assetUserWalletTransactionRecord, BalanceType balanceType) throws CantRegisterDebitException; //TODO: Debemos de definir la estructura de la transaccion

    void credit(AssetUserWalletTransactionRecord assetUserWalletTransactionRecord, BalanceType balanceType) throws CantRegisterCreditException; //TODO: Debemos de definir la estructura de la transaccion

}
