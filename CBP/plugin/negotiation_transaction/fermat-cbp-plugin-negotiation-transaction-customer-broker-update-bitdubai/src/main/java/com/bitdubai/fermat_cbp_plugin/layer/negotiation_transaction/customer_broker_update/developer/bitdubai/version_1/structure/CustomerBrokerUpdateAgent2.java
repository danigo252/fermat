package com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.AbstractAgent;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.EventManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Specialist;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Transaction;
import com.bitdubai.fermat_api.layer.all_definition.util.XMLParser;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.Broadcaster;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecordException;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogManager;
import com.bitdubai.fermat_cbp_api.all_definition.constants.CBPBroadcasterConstants;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransactionStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransactionType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransmissionType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationType;
import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventStatus;
import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventType;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.UnexpectedResultReturnedFromDatabaseException;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation_transaction.NegotiationPurchaseRecord;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation_transaction.NegotiationSaleRecord;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation_transaction.NegotiationTransaction;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiationManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.interfaces.CustomerBrokerSaleNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.interfaces.CustomerBrokerSaleNegotiationManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation_transaction.customer_broker_update.interfaces.CustomerBrokerUpdate;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.exceptions.CantSendConfirmToCryptoBrokerException;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.exceptions.CantSendConfirmToCryptoCustomerException;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.exceptions.CantSendNegotiationToCryptoBrokerException;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.exceptions.CantSendNegotiationToCryptoCustomerException;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.interfaces.NegotiationTransmission;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.interfaces.NegotiationTransmissionManager;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.NegotiationTransactionCustomerBrokerUpdatePluginRoot;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.database.CustomerBrokerUpdateNegotiationTransactionDatabaseDao;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.exceptions.CantGetNegotiationTransactionListException;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.exceptions.CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.exceptions.CantSendCustomerBrokerUpdateNegotiationTransactionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.bitdubai.fermat_api.layer.osa_android.broadcaster.BroadcasterType.NOTIFICATION_SERVICE;
import static com.bitdubai.fermat_api.layer.osa_android.broadcaster.BroadcasterType.UPDATE_VIEW;
import static com.bitdubai.fermat_cbp_api.all_definition.constants.CBPBroadcasterConstants.CBW_CANCEL_NEGOTIATION_NOTIFICATION;
import static com.bitdubai.fermat_cbp_api.all_definition.constants.CBPBroadcasterConstants.CBW_NEGOTIATION_UPDATE_VIEW;
import static com.bitdubai.fermat_cbp_api.all_definition.constants.CBPBroadcasterConstants.CBW_WAITING_FOR_BROKER_NOTIFICATION;
import static com.bitdubai.fermat_cbp_api.all_definition.constants.CBPBroadcasterConstants.CCW_NEGOTIATION_UPDATE_VIEW;
import static com.bitdubai.fermat_cbp_api.all_definition.constants.CBPBroadcasterConstants.CCW_WAITING_FOR_CUSTOMER_NOTIFICATION;

/**
 * Created by Yordin Alayn on 05.07.16.
 */
public class CustomerBrokerUpdateAgent2 extends AbstractAgent {

    private Database                                                database;
    private Thread                                                  agentThread;
    private LogManager                                              logManager;
    private EventManager                                            eventManager;
    private NegotiationTransactionCustomerBrokerUpdatePluginRoot    pluginRoot;
    private PluginDatabaseSystem                                    pluginDatabaseSystem;
    private UUID                                                    pluginId;
    private CustomerBrokerUpdateNegotiationTransactionDatabaseDao   dao;
    private NegotiationTransmissionManager                          negotiationTransmissionManager;
    private CustomerBrokerPurchaseNegotiation                       customerBrokerPurchaseNegotiation;
    private CustomerBrokerPurchaseNegotiationManager                customerBrokerPurchaseNegotiationManager;
    private CustomerBrokerSaleNegotiation                           customerBrokerSaleNegotiation;
    private CustomerBrokerSaleNegotiationManager                    customerBrokerSaleNegotiationManager;
    private Broadcaster                                             broadcaster;

    private int                 iterationConfirmSend    = 0;
    private Map<UUID,Integer>   transactionSend         = new HashMap<>();
    
    public CustomerBrokerUpdateAgent2(
        long                                                    sleepTime,
        TimeUnit                                                timeUnit,
        long                                                    initDelayTime,
        PluginDatabaseSystem                                    pluginDatabaseSystem,
        LogManager                                              logManager,
        NegotiationTransactionCustomerBrokerUpdatePluginRoot    pluginRoot,
        EventManager                                            eventManager,
        UUID                                                    pluginId,
        CustomerBrokerUpdateNegotiationTransactionDatabaseDao   dao,
        NegotiationTransmissionManager                          negotiationTransmissionManager,
        CustomerBrokerPurchaseNegotiation                       customerBrokerPurchaseNegotiation,
        CustomerBrokerSaleNegotiation                           customerBrokerSaleNegotiation,
        CustomerBrokerPurchaseNegotiationManager                customerBrokerPurchaseNegotiationManager,
        CustomerBrokerSaleNegotiationManager                    customerBrokerSaleNegotiationManager,
        Broadcaster                                             broadcaster
    ) {
        
        super(sleepTime, timeUnit, initDelayTime);
        
        this.pluginDatabaseSystem                       = pluginDatabaseSystem;
        this.logManager                                 = logManager;
        this.pluginRoot                                 = pluginRoot;
        this.eventManager                               = eventManager;
        this.pluginId                                   = pluginId;
        this.dao                                        = dao;
        this.negotiationTransmissionManager             = negotiationTransmissionManager;
        this.customerBrokerPurchaseNegotiation          = customerBrokerPurchaseNegotiation;
        this.customerBrokerSaleNegotiation              = customerBrokerSaleNegotiation;
        this.customerBrokerPurchaseNegotiationManager   = customerBrokerPurchaseNegotiationManager;
        this.customerBrokerSaleNegotiationManager       = customerBrokerSaleNegotiationManager;
        this.broadcaster = broadcaster;
    }

    @Override
    protected Runnable agentJob() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {

                    doTheMainTask();

                } catch (
                        CantSendCustomerBrokerUpdateNegotiationTransactionException | 
                        CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException | 
                        CantUpdateRecordException e) {
                    pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
                }
            }
        };
        return runnable;
    }

    @Override
    protected void onErrorOccur() {
        pluginRoot.reportError(
                UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN,
                new Exception("CustomerBrokerCloseAgent2 Error"));
    }

    private void doTheMainTask() throws 
            CantSendCustomerBrokerUpdateNegotiationTransactionException,
            CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException,
            CantUpdateRecordException
    {

        try {

            String                              negotiationXML;
            NegotiationType                     negotiationType;
            UUID                                transactionId;
            List<CustomerBrokerUpdate> negotiationPendingToSubmitList;
            CustomerBrokerPurchaseNegotiation   purchaseNegotiation = new NegotiationPurchaseRecord();
            CustomerBrokerSaleNegotiation       saleNegotiation     = new NegotiationSaleRecord();
            int                                 timeConfirmSend     = 20;

            iterationConfirmSend++;

            //SEND NEGOTIATION PENDING (CUSTOMER_BROKER_NEW_STATUS_NEGOTIATION_COLUMN_NAME = NegotiationTransactionStatus.PENDING_SUBMIT)
            negotiationPendingToSubmitList = dao.getPendingToSubmitNegotiation();
            if (!negotiationPendingToSubmitList.isEmpty()) {
                for (CustomerBrokerUpdate negotiationTransaction : negotiationPendingToSubmitList) {

                    System.out.print("\n\n**** 5) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - NEGOTIATION FOR SEND " +
                            "\n - TransactionId: " + negotiationTransaction.getTransactionId() +
                            "\n - Status: " + negotiationTransaction.getStatusTransaction() +
                            " ****\n");

                    negotiationXML = negotiationTransaction.getNegotiationXML();
                    negotiationType = negotiationTransaction.getNegotiationType();
                    transactionId = negotiationTransaction.getTransactionId();

                    switch (negotiationType) {
                        case PURCHASE:
                            purchaseNegotiation = (CustomerBrokerPurchaseNegotiation) XMLParser.parseXML(negotiationXML, purchaseNegotiation);
                            System.out.print("\n\n**** 6) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - PURCHASE NEGOTIATION SEND negotiationId(XML): " + purchaseNegotiation.getNegotiationId() + " ****\n" +
                                    "\n - Status :" + purchaseNegotiation.getStatus().getCode());
                            //SEND NEGOTIATION TO BROKER
                            negotiationTransmissionManager.sendNegotiationToCryptoBroker(negotiationTransaction, NegotiationTransactionType.CUSTOMER_BROKER_UPDATE);

                            break;

                        case SALE:
                            saleNegotiation = (CustomerBrokerSaleNegotiation) XMLParser.parseXML(negotiationXML, saleNegotiation);
                            System.out.print("\n\n**** 6) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - SALE NEGOTIATION SEND negotiationId(XML): " + saleNegotiation.getNegotiationId() + " ****\n" +
                                    "\n - Status :" + saleNegotiation.getStatus().getCode());
                            //SEND NEGOTIATION TO CUSTOMER
                            negotiationTransmissionManager.sendNegotiationToCryptoCustomer(negotiationTransaction, NegotiationTransactionType.CUSTOMER_BROKER_UPDATE);

                            break;
                    }

                    //Update the Negotiation Transaction
//                        System.out.print("\n\n**** 7) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - UPDATE STATUS SALE NEGOTIATION STATUS : " + NegotiationTransactionStatus.SENDING_NEGOTIATION.getCode() + " ****\n");
                    dao.updateStatusRegisterCustomerBrokerUpdateNegotiationTranasction(transactionId, NegotiationTransactionStatus.SENDING_NEGOTIATION);
                    CustomerBrokerUpdate transactionDao = dao.getRegisterCustomerBrokerUpdateNegotiationTranasction(transactionId);
                    System.out.print("\n\n**** 6.1) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - STATUS TRANSACTION: " + transactionDao.getStatusTransaction().getCode() + " ****\n");

                }
            }

            //SEND CONFIRM PENDING (CUSTOMER_BROKER_NEW_STATUS_NEGOTIATION_COLUMN_NAME = NegotiationTransactionStatus.PENDING_CONFIRMATION)
            negotiationPendingToSubmitList = dao.getPendingToConfirmtNegotiation();
            if (!negotiationPendingToSubmitList.isEmpty()) {
                for (CustomerBrokerUpdate negotiationTransaction : negotiationPendingToSubmitList) {

                    System.out.print("\n\n**** 22) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CONFIRMATION FOR SEND ****\n");

                    transactionId = negotiationTransaction.getTransactionId();
                    negotiationType = negotiationTransaction.getNegotiationType();

                    switch (negotiationType) {
                        case PURCHASE:
                            System.out.print("\n**** 23) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CONFIRMATION SEND PURCHASE NEGOTIATION negotiationId(XML): " + negotiationTransaction.getTransactionId() + " ****\n");
                            //SEND CONFIRM NEGOTIATION TO BROKER
                            negotiationTransmissionManager.sendConfirmNegotiationToCryptoBroker(negotiationTransaction, NegotiationTransactionType.CUSTOMER_BROKER_UPDATE);
                            break;
                        case SALE:
                            System.out.print("\n**** 23) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CONFIRMATION SEND SALE NEGOTIATION negotiationId(XML): " + negotiationTransaction.getTransactionId() + " ****\n");
                            //SEND NEGOTIATION TO CUSTOMER
                            negotiationTransmissionManager.sendConfirmNegotiationToCryptoCustomer(negotiationTransaction, NegotiationTransactionType.CUSTOMER_BROKER_UPDATE);
                            break;
                    }

                    //Update the Negotiation Transaction
                    dao.updateStatusRegisterCustomerBrokerUpdateNegotiationTranasction(transactionId, NegotiationTransactionStatus.CONFIRM_NEGOTIATION);

                    //CONFIRM TRANSACTION IS DONE
                    dao.confirmTransaction(transactionId);

                }
            }

            //Check if pending events
            List<UUID> pendingEventsIdList = dao.getPendingEvents();
            for (UUID eventId : pendingEventsIdList) {
                checkPendingEvent(eventId);
            }

            //SEND TRNSACTION AGAIN IF NOT IS CONFIRM
            if(timeConfirmSend == iterationConfirmSend){

                CustomerBrokerUpdateForwardTransaction forwardTransaction = new CustomerBrokerUpdateForwardTransaction(
                        dao,
                        pluginRoot,
                        transactionSend
                );

                forwardTransaction.pendingToConfirmtTransaction();
                transactionSend = forwardTransaction.getTransactionSend();

                iterationConfirmSend = 0;
            }

        } catch (CantSendNegotiationToCryptoBrokerException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantSendCustomerBrokerUpdateNegotiationTransactionException(CantSendCustomerBrokerUpdateNegotiationTransactionException.DEFAULT_MESSAGE, e, "Sending Purchase Negotiation", "Error in Negotiation Transmission Network Service");
        } catch (CantSendNegotiationToCryptoCustomerException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantSendCustomerBrokerUpdateNegotiationTransactionException(CantSendCustomerBrokerUpdateNegotiationTransactionException.DEFAULT_MESSAGE, e, "Sending Sale Negotiation", "Error in Negotiation Transmission Network Service");
        } catch (CantSendConfirmToCryptoBrokerException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException(CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException.DEFAULT_MESSAGE, e, "Sending Confirm Purchase Negotiation", "Error in Negotiation Transmission Network Service");
        } catch (CantSendConfirmToCryptoCustomerException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException(CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException.DEFAULT_MESSAGE, e, "Sending Confirm Sale Negotiation", "Error in Negotiation Transmission Network Service");
        } catch (CantGetNegotiationTransactionListException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantSendCustomerBrokerUpdateNegotiationTransactionException(e.getMessage(), FermatException.wrapException(e), "Sending Negotiation", "Cannot get the Negotiation list from database");
        } catch (Exception e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantSendCustomerBrokerUpdateNegotiationTransactionException(e.getMessage(), FermatException.wrapException(e), "Sending Negotiation", "UNKNOWN FAILURE.");
        }
        
    }


    //CHECK PENDING EVEN
    private void checkPendingEvent(UUID eventId) throws UnexpectedResultReturnedFromDatabaseException {

        try {
            UUID transactionId;
            UUID transmissionId;
            NegotiationTransmission negotiationTransmission;
            NegotiationTransaction negotiationTransaction;
            NegotiationType negotiationType;
            String negotiationXML;
            CustomerBrokerUpdatePurchaseNegotiationTransaction  customerBrokerUpdatePurchaseNegotiationTransaction;
            CustomerBrokerUpdateSaleNegotiationTransaction      customerBrokerUpdateSaleNegotiationTransaction;
            CustomerBrokerPurchaseNegotiation purchaseNegotiation = new NegotiationPurchaseRecord();
            CustomerBrokerSaleNegotiation saleNegotiation = new NegotiationSaleRecord();

            String eventTypeCode = dao.getEventType(eventId);

            //EVENT - RECEIVE NEGOTIATION
            if (eventTypeCode.equals(EventType.INCOMING_NEGOTIATION_TRANSMISSION_TRANSACTION_UPDATE.getCode())) {

                List<Transaction<NegotiationTransmission>> pendingTransactionList = negotiationTransmissionManager.getPendingTransactions(Specialist.UNKNOWN_SPECIALIST);
                for (Transaction<NegotiationTransmission> record : pendingTransactionList) {

                    negotiationTransmission = record.getInformation();

                    if (negotiationTransmission.getNegotiationTransactionType().getCode().equals(NegotiationTransactionType.CUSTOMER_BROKER_UPDATE.getCode())) {

                        negotiationXML = negotiationTransmission.getNegotiationXML();
                        transmissionId = negotiationTransmission.getTransmissionId();
                        transactionId = negotiationTransmission.getTransactionId();
                        negotiationType = negotiationTransmission.getNegotiationType();

                        if (negotiationXML != null) {

                            negotiationTransaction = dao.getRegisterCustomerBrokerUpdateNegotiationTranasction(transactionId);

                            if (negotiationTransmission.getTransmissionType().equals(NegotiationTransmissionType.TRANSMISSION_NEGOTIATION)) {

                                if(negotiationTransaction == null) {

                                    switch (negotiationType) {
                                        case PURCHASE:

                                            System.out.print("\n**** 19) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CREATE PURCHASE NEGOTIATION TRANSACTION  ****\n");
                                            //UPDATE PURCHASE NEGOTIATION
                                            purchaseNegotiation = (CustomerBrokerPurchaseNegotiation) XMLParser.parseXML(negotiationXML, purchaseNegotiation);

                                            customerBrokerUpdatePurchaseNegotiationTransaction = new CustomerBrokerUpdatePurchaseNegotiationTransaction(
                                                    customerBrokerPurchaseNegotiationManager,
                                                    dao,
                                                    pluginRoot
                                            );

                                            final String purchaseCancelReason = purchaseNegotiation.getCancelReason();
                                            System.out.println("CancelReason: " + purchaseCancelReason);

                                            final String customerWalletPublicKey = "crypto_customer_wallet"; // TODO: Esto es provisorio. Hay que obtenerlo del Wallet Manager de WPD hasta que matias haga los cambios para que no sea necesario enviar esto
                                            if (purchaseCancelReason != null && !purchaseCancelReason.isEmpty() && !purchaseCancelReason.equalsIgnoreCase("null")) {
                                                System.out.print("\n**** 20) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CANCEL PURCHASE NEGOTIATION TRANSACTION  ****\n");
                                                //CANCEL NEGOTIATION
                                                customerBrokerUpdatePurchaseNegotiationTransaction.receiveCancelPurchaseNegotiationTranasction(transactionId, purchaseNegotiation);

                                                broadcaster.publish(NOTIFICATION_SERVICE, customerWalletPublicKey, CBPBroadcasterConstants.CCW_CANCEL_NEGOTIATION_NOTIFICATION);
                                                broadcaster.publish(UPDATE_VIEW, CCW_NEGOTIATION_UPDATE_VIEW);
                                            } else {
                                                System.out.print("\n**** 20) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - UPDATE PURCHASE NEGOTIATION TRANSACTION  ****\n");
                                                //UPDATE NEGOTIATION
                                                customerBrokerUpdatePurchaseNegotiationTransaction.receivePurchaseNegotiationTranasction(transactionId, purchaseNegotiation);

                                                broadcaster.publish(NOTIFICATION_SERVICE, customerWalletPublicKey, CCW_WAITING_FOR_CUSTOMER_NOTIFICATION);
                                                broadcaster.publish(UPDATE_VIEW, CCW_NEGOTIATION_UPDATE_VIEW);
                                            }

                                            break;

                                        case SALE:
                                            System.out.print("\n**** 19) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CREATE SALE NEGOTIATION TRANSACTION  ****\n");
                                            //UPDATE SALE NEGOTIATION
                                            saleNegotiation = (CustomerBrokerSaleNegotiation) XMLParser.parseXML(negotiationXML, saleNegotiation);


                                            customerBrokerUpdateSaleNegotiationTransaction = new CustomerBrokerUpdateSaleNegotiationTransaction(
                                                    customerBrokerSaleNegotiationManager,
                                                    dao,
                                                    pluginRoot
                                            );

                                            final String saleCancelReason = saleNegotiation.getCancelReason();
                                            System.out.println("CancelReason: " + saleCancelReason);

                                            final String brokerWalletPublicKey = "crypto_broker_wallet"; // TODO: Esto es provisorio. Hay que obtenerlo del Wallet Manager de WPD hasta que matias haga los cambios para que no sea necesario enviar esto
                                            if (saleCancelReason != null && !saleCancelReason.isEmpty() && !saleCancelReason.equalsIgnoreCase("null")) {
                                                System.out.print("\n**** 20) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CANCEL SALE NEGOTIATION TRANSACTION  ****\n");
                                                //CANCEL NEGOTIATION
                                                customerBrokerUpdateSaleNegotiationTransaction.receiveCancelSaleNegotiationTranasction(transactionId, saleNegotiation);

                                                broadcaster.publish(NOTIFICATION_SERVICE, brokerWalletPublicKey, CBW_CANCEL_NEGOTIATION_NOTIFICATION);
                                                broadcaster.publish(UPDATE_VIEW, CBW_NEGOTIATION_UPDATE_VIEW);
                                            } else {
                                                System.out.print("\n**** 20) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - UPDATE SALE NEGOTIATION TRANSACTION  ****\n");
                                                //UPDATE NEGOTIATION
                                                customerBrokerUpdateSaleNegotiationTransaction.receiveSaleNegotiationTranasction(transactionId, saleNegotiation);

                                                broadcaster.publish(NOTIFICATION_SERVICE, brokerWalletPublicKey, CBW_WAITING_FOR_BROKER_NOTIFICATION);
                                                broadcaster.publish(UPDATE_VIEW, CBW_NEGOTIATION_UPDATE_VIEW);
                                            }

                                            break;
                                    }

                                } else {

                                    System.out.print("\n**** 20) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CREATE PURCHASE NEGOTIATION TRANSACTION REPEAT SEND ****\n");
                                    //CONFIRM TRANSACTION
                                    dao.updateStatusRegisterCustomerBrokerUpdateNegotiationTranasction(
                                            transactionId,
                                            NegotiationTransactionStatus.PENDING_SUBMIT_CONFIRM);

                                }


                            } else if (negotiationTransmission.getTransmissionType().equals(NegotiationTransmissionType.TRANSMISSION_CONFIRM)) {

                                System.out.print("\n**** 25.1) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - UPDATE NEGOTIATION TRANSACTION CONFIRM ****\n");

                                if(!negotiationTransaction.getStatusTransaction().getCode().equals(NegotiationTransactionStatus.CONFIRM_NEGOTIATION.getCode())) {

                                    switch (negotiationType) {
                                        case PURCHASE:
                                            System.out.print("\n**** 25.2) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - UPDATE PURCHASE NEGOTIATION TRANSACTION CONFIRM ****\n");

                                            purchaseNegotiation = (CustomerBrokerPurchaseNegotiation) XMLParser.parseXML(negotiationXML, purchaseNegotiation);
                                            final String purchaseCancelReason = purchaseNegotiation.getCancelReason();
                                            if (purchaseCancelReason == null || purchaseCancelReason.isEmpty() || purchaseCancelReason.equalsIgnoreCase("null"))
                                                customerBrokerPurchaseNegotiationManager.waitForBroker(purchaseNegotiation);

                                            broadcaster.publish(UPDATE_VIEW, CCW_NEGOTIATION_UPDATE_VIEW);

                                            break;
                                        case SALE:
                                            System.out.print("\n**** 25.2) NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - UPDATE SALE NEGOTIATION TRANSACTION CONFIRM ****\n");

                                            saleNegotiation = (CustomerBrokerSaleNegotiation) XMLParser.parseXML(negotiationXML, saleNegotiation);
                                            final String saleCancelReason = saleNegotiation.getCancelReason();
                                            if (saleCancelReason == null || saleCancelReason.isEmpty() || saleCancelReason.equalsIgnoreCase("null"))
                                                customerBrokerSaleNegotiationManager.waitForCustomer(saleNegotiation);

                                            broadcaster.publish(UPDATE_VIEW, CBW_NEGOTIATION_UPDATE_VIEW);

                                            break;
                                    }

                                    //CONFIRM TRANSACTION
                                    dao.updateStatusRegisterCustomerBrokerUpdateNegotiationTranasction(transactionId, NegotiationTransactionStatus.CONFIRM_NEGOTIATION);

                                }

                                //CONFIRM TRANSACTION IS DONE
                                dao.confirmTransaction(transactionId);

                            }

                            //NOTIFIED EVENT
                            dao.updateEventTansactionStatus(eventId, EventStatus.NOTIFIED);
                            //CONFIRM TRANSMISSION
                            negotiationTransmissionManager.confirmReception(transmissionId);

                        }
                    }
                }
            }

        } catch (Exception e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            e.printStackTrace();
        }
    }
    
}