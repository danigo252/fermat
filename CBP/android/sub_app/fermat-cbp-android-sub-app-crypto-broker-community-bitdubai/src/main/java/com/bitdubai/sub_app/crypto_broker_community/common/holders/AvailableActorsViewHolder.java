package com.bitdubai.sub_app.crypto_broker_community.common.holders;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bitdubai.fermat_android_api.layer.definition.wallet.utils.ImagesUtils;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.holders.FermatViewHolder;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_broker_community.interfaces.CryptoBrokerCommunityInformation;
import com.bitdubai.sub_app.crypto_broker_community.R;


/**
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 16/12/2015.
 *
 * @author lnacosta
 * @version 1.0.0
 */
public class AvailableActorsViewHolder extends FermatViewHolder {

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
    public AvailableActorsViewHolder(View itemView, int type) {
        super(itemView, type);
        res = itemView.getResources();

        brokerImage = (ImageView) itemView.findViewById(R.id.cbc_broker_image);
        connectionState = (ImageView) itemView.findViewById(R.id.cbc_status_icon);
        brokerName = (FermatTextView) itemView.findViewById(R.id.cbc_broker_name);
        brokerLocation = (FermatTextView) itemView.findViewById(R.id.cbc_location_text);
        connectionText = (FermatTextView) itemView.findViewById(R.id.cbc_connection_text);
    }

    public void bind(CryptoBrokerCommunityInformation data) {
        if (data.getConnectionState() != null) {
            switch (data.getConnectionState()) {
                case CONNECTED:
                    connectionState.setVisibility(View.VISIBLE);
                    connectionState.setImageResource(R.drawable.contacto_activo);
                    connectionText.setText(R.string.cbc_connection_state_connected);
                    connectionText.setVisibility(View.VISIBLE);
                    break;
                case PENDING_REMOTELY_ACCEPTANCE:
                    connectionState.setVisibility(View.VISIBLE);
                    connectionState.setImageResource(R.drawable.agregar_contacto);
                    connectionText.setText(R.string.cbc_connection_state_pending_acceptance);
                    connectionText.setVisibility(View.VISIBLE);
                    break;
                case PENDING_LOCALLY_ACCEPTANCE:
                    connectionState.setVisibility(View.VISIBLE);
                    connectionState.setImageResource(R.drawable.agregar_contacto);
                    connectionText.setText(R.string.cbc_connection_state_pending_acceptance);
                    connectionText.setVisibility(View.VISIBLE);
                    break;
                default:
                    connectionState.setVisibility(View.INVISIBLE);
                    connectionText.setVisibility(View.INVISIBLE);
                    break;
            }
        } else {
            connectionState.setVisibility(View.INVISIBLE);
            connectionText.setVisibility(View.INVISIBLE);
        }

        brokerName.setText(data.getAlias());
        brokerLocation.setText(String.format("%s / %s", data.getCountry(), data.getPlace()));
        brokerImage.setImageDrawable(getImgDrawable(data.getImage()));
    }

    private Drawable getImgDrawable(byte[] customerImg) {
        if (customerImg != null && customerImg.length > 0)
            return ImagesUtils.getRoundedBitmap(res, customerImg);

        return ImagesUtils.getRoundedBitmap(res, R.drawable.ic_profile_male);
    }
}
