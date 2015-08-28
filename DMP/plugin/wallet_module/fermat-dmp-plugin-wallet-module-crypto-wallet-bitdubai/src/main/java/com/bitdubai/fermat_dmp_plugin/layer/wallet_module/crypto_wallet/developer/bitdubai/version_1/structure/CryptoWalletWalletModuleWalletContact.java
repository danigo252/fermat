package com.bitdubai.fermat_dmp_plugin.layer.wallet_module.crypto_wallet.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_contacts.interfaces.WalletContactRecord;
import com.bitdubai.fermat_api.layer.dmp_wallet_module.crypto_wallet.interfaces.CryptoWalletWalletContact;

import java.util.UUID;

/**
 * The Class <code>com.bitdubai.fermat_dmp_plugin.layer.wallet_module.crypto_wallet.developer.bitdubai.version_1.structure.CryptoWalletWalletModuleWalletContact</code>
 * implements the functionality of a CryptoWalletWalletContact.
 * <p/>
 *
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 28/08/2015.
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CryptoWalletWalletModuleWalletContact implements CryptoWalletWalletContact {

    UUID contactId;

    String walletPublicKey;

    Actors actorType;

    CryptoAddress receivedCryptoAddress;

    UUID actorId;

    String actorName;

    byte[] profilePicture;

    public CryptoWalletWalletModuleWalletContact(WalletContactRecord walletContactRecord, byte[] profilePicture) {
        this.contactId = walletContactRecord.getContactId();
        this.walletPublicKey = walletContactRecord.getWalletPublicKey();
        this.actorType = walletContactRecord.getActorType();
        this.receivedCryptoAddress = walletContactRecord.getReceivedCryptoAddress();
        this.actorId = walletContactRecord.getActorId();
        this.actorName = walletContactRecord.getActorName();
        this.profilePicture = profilePicture;
    }

    public CryptoWalletWalletModuleWalletContact(WalletContactRecord walletContactRecord) {
        this(walletContactRecord, null);
    }

    @Override
    public UUID getContactId() {
        return contactId;
    }

    @Override
    public String getWalletPublicKey() {
        return walletPublicKey;
    }

    @Override
    public Actors getActorType() {
        return actorType;
    }

    @Override
    public CryptoAddress getReceivedCryptoAddress() {
        return receivedCryptoAddress;
    }

    @Override
    public UUID getActorId() {
        return actorId;
    }

    @Override
    public String getActorName() {
        return actorName;
    }

    @Override
    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setContactId(UUID contactId) {
        this.contactId = contactId;
    }

    public void setWalletPublicKey(String walletPublicKey) {
        this.walletPublicKey = walletPublicKey;
    }

    public void setActorType(Actors actorType) {
        this.actorType = actorType;
    }

    public void setReceivedCryptoAddress(CryptoAddress receivedCryptoAddress) {
        this.receivedCryptoAddress = receivedCryptoAddress;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
}
