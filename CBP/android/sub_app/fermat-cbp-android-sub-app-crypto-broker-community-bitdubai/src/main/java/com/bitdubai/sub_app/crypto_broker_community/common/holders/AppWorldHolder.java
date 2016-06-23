package com.bitdubai.sub_app.crypto_broker_community.common.holders;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bitdubai.fermat_android_api.layer.definition.wallet.utils.ImagesUtils;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.holders.FermatViewHolder;
import com.bitdubai.fermat_api.layer.actor_connection.common.enums.ConnectionState;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_broker_community.interfaces.CryptoBrokerCommunityInformation;
import com.bitdubai.sub_app.crypto_broker_community.R;


/**
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 16/12/2015.
 *
 * @author lnacosta
 * @version 1.0.0
 */
public class AppWorldHolder extends FermatViewHolder {

    private ImageView brokerImage;
    private ImageView connectionState;
    private FermatTextView brokerName;
    private FermatTextView brokerLocation;
    private FermatTextView connectionText;

    private Resources res;


    /**
     * Constructor
     *
     * @param itemView cast elements in layout
     * @param type     the view older type ID
     */
    public AppWorldHolder(View itemView, int type) {
        super(itemView, type);
        res = itemView.getResources();

        brokerImage = (ImageView) itemView.findViewById(R.id.cbc_broker_image);
        connectionState = (ImageView) itemView.findViewById(R.id.cbc_status_icon);
        brokerName = (FermatTextView) itemView.findViewById(R.id.cbc_broker_name);
        brokerLocation = (FermatTextView) itemView.findViewById(R.id.cbc_location_text);
        connectionText = (FermatTextView) itemView.findViewById(R.id.cbc_connection_text);
    }

    public void bind(CryptoBrokerCommunityInformation data) {
        brokerName.setText(data.getAlias());

        if (data.getConnectionState() != null && data.getConnectionState() == ConnectionState.CONNECTED) {
            connectionState.setImageResource(R.drawable.contacto_activo);
            connectionText.setText("Is connected with you");
            connectionText.setVisibility(View.VISIBLE);
        } else {
            connectionState.setImageResource(R.drawable.agregar_contacto);
            connectionText.setVisibility(View.INVISIBLE);
        }

        brokerImage.setImageDrawable(getImgDrawable(data.getImage()));
    }

    private Drawable getImgDrawable(byte[] customerImg) {
        if (customerImg != null && customerImg.length > 0)
            return ImagesUtils.getRoundedBitmap(res, customerImg);

        return ImagesUtils.getRoundedBitmap(res, R.drawable.ic_profile_male);
    }
}
