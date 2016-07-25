package org.fermat.fermat_dap_android_wallet_redeem_point.models;

import com.bitdubai.fermat_api.layer.all_definition.util.BitcoinConverter;

import org.fermat.fermat_dap_android_wallet_redeem_point.v3.util.Utils;
import org.fermat.fermat_dap_api.layer.all_definition.util.DAPStandardFormats;
import org.fermat.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bitdubai.fermat_api.layer.all_definition.util.BitcoinConverter.Currency.BITCOIN;
import static com.bitdubai.fermat_api.layer.all_definition.util.BitcoinConverter.Currency.SATOSHI;

/**
 * Created by francisco on 08/10/15.
 */
public class DigitalAsset implements Serializable {

    private String name;
    private String amount;
    private Long availableBalanceQuantity;
    private Long bookBalanceQuantity;
    private Long availableBalance;
    private Timestamp expDate;
    private Timestamp date;
    private String walletPublicKey;
    private String assetPublicKey;
    private ActorAssetUser actorAssetUser;
    private byte[] image;
    private String actorUserNameFrom;
    private String actorIssuerNameFrom;
    private String actorIssuerAddress;
    private byte[] imageActorUserFrom;
    private byte[] imageActorIssuerFrom;
    private Status status;
    private String assetDescription;

    public enum Status {
        PENDING("PENDING"),
        CONFIRMED("CONFIRMED"),
        ACEPTED("ACEPTED"),
        DELIVERED("DELIVERED");

        private String status;

        Status(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }
    }


    public DigitalAsset() {
    }

    public DigitalAsset(String name, String amount) {
        setName(name);
        setAmount(amount);
    }

    public static ArrayList<DigitalAsset> getAssets() {
        List<DigitalAsset> assets = new ArrayList<>();
        assets.add(new DigitalAsset("KFC Coupon", "150.457"));
        assets.add(new DigitalAsset("Burgerking Coupon", "150.457"));
        assets.add(new DigitalAsset("MacDonalds Coupon", "150.457"));
        assets.add(new DigitalAsset("Free Coupon", "150.457"));
        return (ArrayList<DigitalAsset>) assets;
    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return String.format("%s | %s BTC", name, amount);
    }

    public String getWalletPublicKey() {
        return walletPublicKey;
    }

    public void setWalletPublicKey(String walletPublicKey) {
        this.walletPublicKey = walletPublicKey;
    }

    public String getAssetPublicKey() {
        return assetPublicKey;
    }

    public void setAssetPublicKey(String assetPublicKey) {
        this.assetPublicKey = assetPublicKey;
    }

    public ActorAssetUser getActorAssetUser() {
        return actorAssetUser;
    }

    public void setActorAssetUser(ActorAssetUser actorAssetUser) {
        this.actorAssetUser = actorAssetUser;
    }

    public Long getAvailableBalanceQuantity() {
        return availableBalanceQuantity;
    }

    public void setAvailableBalanceQuantity(Long availableBalanceQuantity) {
        this.availableBalanceQuantity = availableBalanceQuantity;
    }

    public Long getBookBalanceQuantity() {
        return bookBalanceQuantity;
    }

    public void setBookBalanceQuantity(Long bookBalanceQuantity) {
        this.bookBalanceQuantity = bookBalanceQuantity;
    }

    public void setAvailableBalance(Long availableBalance) {
        this.availableBalance = availableBalance;
    }

    public Long getAvailableBalance() {
        return availableBalance;
    }

    public Double getAvailableBalanceBitcoin() {
        return BitcoinConverter.convert(Double.valueOf(availableBalance), SATOSHI, BITCOIN);
    }

    public String getFormattedAvailableBalanceBitcoin() {
        return DAPStandardFormats.BITCOIN_FORMAT.format(getAvailableBalanceBitcoin());
    }

    public Date getExpDate() {
        return expDate;
    }

    public String getFormattedExpDate() {
        if (expDate == null) return "No expiration date";
        return DAPStandardFormats.DATE_FORMAT.format(expDate);
    }

    public void setExpDate(Timestamp expDate) {
        this.expDate = expDate;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getActorUserNameFrom() {
        return actorUserNameFrom;
    }

    public void setActorUserNameFrom(String actorUserNameFrom) {
        this.actorUserNameFrom = actorUserNameFrom;
    }

    public byte[] getImageActorUserFrom() {
        return imageActorUserFrom;
    }

    public void setImageActorUserFrom(byte[] imageActorUserFrom) {
        this.imageActorUserFrom = imageActorUserFrom;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getFormattedDate() {
        return (date == null) ? "No date" : Utils.getTimeAgo(date.getTime());
    }

    public String getActorIssuerNameFrom() {
        return actorIssuerNameFrom;
    }

    public void setActorIssuerNameFrom(String actorIssuerNameFrom) {
        this.actorIssuerNameFrom = actorIssuerNameFrom;
    }

    public String getActorIssuerAddress() {
        return actorIssuerAddress;
    }

    public void setActorIssuerAddress(String actorIssuerAddress) {
        this.actorIssuerAddress = actorIssuerAddress;
    }

    public byte[] getImageActorIssuerFrom() {
        return imageActorIssuerFrom;
    }

    public void setImageActorIssuerFrom(byte[] imageActorIssuerFrom) {
        this.imageActorIssuerFrom = imageActorIssuerFrom;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAssetDescription() {
        return assetDescription;
    }

    public void setAssetDescription(String assetDescription) {
        this.assetDescription = assetDescription;
    }
}
