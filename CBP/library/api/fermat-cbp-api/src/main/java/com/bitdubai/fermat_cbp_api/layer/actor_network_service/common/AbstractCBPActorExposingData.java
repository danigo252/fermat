package com.bitdubai.fermat_cbp_api.layer.actor_network_service.common;

import com.bitdubai.fermat_api.layer.osa_android.location_system.Location;

import java.util.Arrays;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 05/07/16.
 */
public abstract class AbstractCBPActorExposingData {

    private final String publicKey;
    private final String alias    ;
    private final byte[] image    ;
    private final Location location;
    private final long refreshInterval;
    private final long accuracy;

    /**
     * Default constructor with parameters
     * @param publicKey
     * @param alias
     * @param image
     * @param location
     * @param refreshInterval
     * @param accuracy
     */
    public AbstractCBPActorExposingData(final String publicKey,
                                        final String alias,
                                        final byte[] image,
                                        final Location location,
                                        final long refreshInterval,
                                        final long accuracy) {

        this.publicKey = publicKey;
        this.alias     = alias    ;
        this.image     = image    ;
        this.location  = location;
        this.refreshInterval = refreshInterval;
        this.accuracy = accuracy;
    }

    /**
     * @return a string representing the public key.
     */
    public final String getPublicKey() {
        return publicKey;
    }

    /**
     * @return a string representing the alias of the crypto customer.
     */
    public final String getAlias() {
        return alias;
    }

    /**
     * @return an array of bytes with the image exposed by the Crypto Customer.
     */
    public final byte[] getImage() {
        return image;
    }

    /**
     * @return an locarion  with the image exposed by the Crypto Customer.
     */
    public final Location getLocation() {
        return location;
    }

    /**
     * @return an long with interval refresh by the Crypto Customer.
     */
    public final long getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * @return an long with accuracy refresh by the Crypto Customer.
     */
    public final long getAccuracy() {
        return accuracy;
    }

    @Override
    public String toString() {
        return "AbstractCBPActorExposingData{" +
                "publicKey='" + publicKey + '\'' +
                ", alias='" + alias + '\'' +
                ", image=" + Arrays.toString(image) +
                ", location=" + location +
                ", refreshInterval=" + refreshInterval +
                ", accuracy=" + accuracy +
                '}';
    }

}
