package com.bitdubai.wallet_platform_api.layer._11_network_service.intra_user;

import java.util.UUID;

/**
 * Created by ciencias on 2/13/15.
 */
public interface IntraUserNetworkService {
    
    public IntraUser getSystemUser (UUID userId);
}
