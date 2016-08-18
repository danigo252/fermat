package com.bitdubai.reference_niche_wallet.loss_protected_wallet.common.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Toast;

import com.bitdubai.android_fermat_ccp_loss_protected_wallet_bitcoin.R;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.utils.ImagesUtils;
import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_bch_api.layer.definition.crypto_fee.BitcoinFee;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_ccp_api.layer.request.crypto_payment.enums.CryptoPaymentState;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.loss_protected_wallet.LossProtectedWalletSettings;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.loss_protected_wallet.interfaces.LossProtectedPaymentRequest;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.loss_protected_wallet.interfaces.LossProtectedWallet;
import com.bitdubai.reference_niche_wallet.loss_protected_wallet.common.enums.ShowMoneyType;
import com.bitdubai.reference_niche_wallet.loss_protected_wallet.common.holders.PaymentHistoryItemViewHolder;
import com.bitdubai.reference_niche_wallet.loss_protected_wallet.common.popup.Confirm_Send_Payment_Dialog;
import com.bitdubai.reference_niche_wallet.loss_protected_wallet.common.utils.WalletUtils;
import com.bitdubai.reference_niche_wallet.loss_protected_wallet.common.utils.onRefreshList;
import com.bitdubai.reference_niche_wallet.loss_protected_wallet.session.SessionConstant;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.bitdubai.reference_niche_wallet.loss_protected_wallet.common.utils.WalletUtils.showMessage;

/**
 * Created by Matias Furszyfer on 2015.09.30..
 */
public class PaymentRequestHistoryAdapter  extends FermatAdapter<LossProtectedPaymentRequest, PaymentHistoryItemViewHolder>  {

    private onRefreshList onRefreshList;

    LossProtectedWallet lossProtectedWallet;
    LossProtectedWalletSettings lossProtectedWalletSettings;
    ReferenceAppFermatSession<LossProtectedWallet> appSession;
    Typeface tf;

    boolean lossProtectedEnabled;
    BlockchainNetworkType blockchainNetworkType;
    private String feeLevel = "NORMAL";


    protected PaymentRequestHistoryAdapter(Context context) {
        super(context);
    }

    public PaymentRequestHistoryAdapter(Context context, List<LossProtectedPaymentRequest> dataSet, LossProtectedWallet cryptoWallet, ReferenceAppFermatSession<LossProtectedWallet> referenceWalletSession,onRefreshList onRefresh) {
        super(context, dataSet);
        this.lossProtectedWallet = cryptoWallet;
        this.appSession =referenceWalletSession;
        this.onRefreshList = onRefresh;
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");

        try {

            if(appSession.getData(SessionConstant.BLOCKCHANIN_TYPE) != null)
                blockchainNetworkType = (BlockchainNetworkType)appSession.getData(SessionConstant.BLOCKCHANIN_TYPE);
            else
                blockchainNetworkType = BlockchainNetworkType.getDefaultBlockchainNetworkType();

            if(appSession.getData(SessionConstant.LOSS_PROTECTED_ENABLED) != null)
                lossProtectedEnabled = (boolean)appSession.getData(SessionConstant.LOSS_PROTECTED_ENABLED);
            else
                lossProtectedEnabled = true;

            if(appSession.getData(SessionConstant.FEE_LEVEL) != null)
                feeLevel = (String)appSession.getData(SessionConstant.FEE_LEVEL);
            else
                feeLevel = "NORMAL";



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnClickListerAcceptButton(View.OnClickListener onClickListener){


    }

    public void setOnClickListerRefuseButton(View.OnClickListener onClickListener){

    }

    /**
     * Create a new holder instance
     *
     * @param itemView View object
     * @param type     int type
     * @return ViewHolder
     */
    @Override
    protected PaymentHistoryItemViewHolder createHolder(View itemView, int type) {
        return new PaymentHistoryItemViewHolder(itemView);
    }

    /**
     * Get custom layout to use it.
     *
     * @return int Layout Resource id: Example: R.layout.row_item
     */
    @Override
    protected int getCardViewResource() {
        return R.layout.loss_history_request_row;
    }
    /**
     * Bind ViewHolder
     *
     * @param holder   ViewHolder object
     * @param data     Object data to render
     * @param position position to render
     */
    @Override
    protected void bindHolder(final PaymentHistoryItemViewHolder holder, final LossProtectedPaymentRequest data, int position) {

        final int MAX_DECIMAL_FOR_BALANCE_TRANSACTION = 8;
        final int MIN_DECIMAL_FOR_BALANCE_TRANSACTION = 2;

        try {
            holder.getContactIcon().setImageDrawable(ImagesUtils.getRoundedBitmap(context.getResources(), data.getContact().getProfilePicture()));
        }catch (Exception e){
            holder.getContactIcon().setImageDrawable(ImagesUtils.getRoundedBitmap(context.getResources(), R.drawable.ic_profile_male));
        }

        try {
            //set Amount transaction
            holder.getTxt_amount().setText(
                    WalletUtils.formatBalanceStringWithDecimalEntry(
                            data.getAmount(),
                            MAX_DECIMAL_FOR_BALANCE_TRANSACTION,
                            MIN_DECIMAL_FOR_BALANCE_TRANSACTION,
                            ShowMoneyType.BITCOIN.getCode())+ " BTC");

            holder.getTxt_amount().setTypeface(tf) ;
        }
            catch (Exception e){
                e.printStackTrace();
            }


        if(data.getContact() != null)
            holder.getTxt_contactName().setText(data.getContact().getActorName());
        else
            holder.getTxt_contactName().setText("Unknown");

        holder.getTxt_contactName().setTypeface(tf);

        holder.getTxt_notes().setText(data.getReason());
        holder.getTxt_notes().setTypeface(tf);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.US);
        holder.getTxt_time().setText(data.getDate() + " hs");
        holder.getTxt_time().setTypeface(tf);

        String state = "";
        switch (data.getState()){
            case WAITING_RECEPTION_CONFIRMATION:
                //state = "Waiting for response";
                state = context.getResources().getString(R.string.accept_text);

                break;
            case APPROVED:
                //state = "Accepted";
                state = context.getResources().getString(R.string.accepted_text);
                break;
            case PAID:
                //state = "Paid";
                state = context.getResources().getString(R.string.Paid_text);
                break;
            case PENDING_RESPONSE:
                //state = "Pending response";
                state = context.getResources().getString(R.string.Pending_response);
                break;
            case ERROR:
               // state = "Error";
                state = context.getResources().getString(R.string.Error);
                break;
            case NOT_SENT_YET:
                //state = "Not sent yet";
                state = context.getResources().getString(R.string.Not_sent_yet);
                break;
            case PAYMENT_PROCESS_STARTED:
                //state = "Payment process started";
                state = context.getResources().getString(R.string.Payment_process_started);
                break;
            case DENIED_BY_INCOMPATIBILITY:
                //state = "Denied by incompatibility";
                state = context.getResources().getString(R.string.Denied_by_incompatibility);
                break;
            case IN_APPROVING_PROCESS:
                //state = "In approving process";
                state = context.getResources().getString(R.string.In_approving_process);
                break;
            case REFUSED:
                //state = "Denied";
                state = context.getResources().getString(R.string.denied);
                break;
            default:
               // state = "Error, contact with support";
                state = context.getResources().getString(R.string.Error_contact_with_support);
                break;

        }


        if(data.getType() == 0) //SEND
        {
            holder.getLinear_layour_container_buttons().setVisibility(View.GONE);
            holder.getLinear_layour_container_state().setVisibility(View.VISIBLE);
            holder.getTxt_state().setText(state);
            holder.getTxt_state().setTypeface(tf);
        }
        else
        {
            if(data.getState().equals(CryptoPaymentState.APPROVED) || data.getState().equals(CryptoPaymentState.REFUSED) || data.getState().equals(CryptoPaymentState.ERROR)) {
                holder.getLinear_layour_container_buttons().setVisibility(View.GONE);
                holder.getLinear_layour_container_state().setVisibility(View.VISIBLE);

                holder.getTxt_state().setText(state);
                holder.getTxt_state().setTypeface(tf);
            }
            else
            {
                holder.getLinear_layour_container_buttons().setVisibility(View.VISIBLE);
                holder.getLinear_layour_container_state().setVisibility(View.GONE);

                holder.getTxt_state().setText(state);
                holder.getTxt_state().setTypeface(tf);
            }
        }



            holder.getBtn_accept_request().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        //verify loss protected settings
                        if(((double)appSession.getData(SessionConstant.ACTUAL_EXCHANGE_RATE)) != 0){



                            lossProtectedWallet = appSession.getModuleManager();




                            long availableBalance = lossProtectedWallet.getBalance(BalanceType.AVAILABLE, appSession.getAppPublicKey(), blockchainNetworkType, String.valueOf(appSession.getData(SessionConstant.ACTUAL_EXCHANGE_RATE)));


                            if( (data.getAmount() + BitcoinFee.valueOf(feeLevel).getFee()) > availableBalance) //the amount is greater than the available
                            {
                                //check loss protected settings
                                if (!lossProtectedEnabled) {
                                    //show dialog confirm
                                    Confirm_Send_Payment_Dialog confirm_send_dialog = new Confirm_Send_Payment_Dialog(context,
                                            data.getAmount(),
                                            data.getRequestId(), appSession,blockchainNetworkType,lossProtectedWallet);
                                    confirm_send_dialog.show();

                                    confirm_send_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            notifyDataSetChanged();
                                            onRefreshList.onRefresh();
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(context, "Action not allowed, You do not have enough funds", Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            {
                                //aprove payment request
                                lossProtectedWallet.approveRequest(data.getRequestId()
                                        , lossProtectedWallet.getSelectedActorIdentity().getPublicKey());
                                Toast.makeText(context, "Request accepted", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                onRefreshList.onRefresh();
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "Action not allowed.Could not retrieve the dollar exchange rate.\nCheck your internet connection.. ", Toast.LENGTH_LONG).show();

                        }



                    } catch (Exception e) {
                        showMessage(context, "Cant Accept or Denied Receive Payment Exception- " + e.getMessage());
                    }

                }
            });

        holder.getBtn_refuse_request().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    lossProtectedWallet.refuseRequest(data.getRequestId());
                    Toast.makeText(context, "Request denied", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    onRefreshList.onRefresh();
                } catch (Exception e) {
                    showMessage(context, "Cant Accept or Denied Receive Payment Exception- " + e.getMessage());
                }
            }
        });
    }



}
