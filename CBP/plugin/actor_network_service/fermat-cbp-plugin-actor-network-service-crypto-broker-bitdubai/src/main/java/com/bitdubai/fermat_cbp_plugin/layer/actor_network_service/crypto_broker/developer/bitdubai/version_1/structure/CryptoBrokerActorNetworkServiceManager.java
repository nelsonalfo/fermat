package com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.structure;

import android.util.Base64;

import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.PlatformComponentProfile;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.world.interfaces.Currency;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.ConnectionRequestAction;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.ProtocolState;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.RequestType;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantAcceptConnectionRequestException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantAnswerQuotesRequestException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantCancelConnectionRequestException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantConfirmException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantDenyConnectionRequestException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantDisconnectException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantExposeIdentitiesException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantExposeIdentityException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantListPendingConnectionRequestsException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantListPendingQuotesRequestsException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantRequestConnectionException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantRequestQuotesException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.ConnectionRequestNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.QuotesRequestNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.interfaces.CryptoBrokerExtraData;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.interfaces.CryptoBrokerExtraDataInfo;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.interfaces.CryptoBrokerExtraDataInfoTemp;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.interfaces.CryptoBrokerManager;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.utils.CryptoBrokerQuote;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.interfaces.CryptoBrokerSearch;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.utils.CryptoBrokerConnectionInformation;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.utils.CryptoBrokerConnectionRequest;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.utils.CryptoBrokerExposingData;
import com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.database.CryptoBrokerActorNetworkServiceDao;
import com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.exceptions.CantConfirmConnectionRequestException;
import com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.exceptions.CantHandleNewMessagesException;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.client.CommunicationsClientConnection;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.exceptions.CantRegisterComponentException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class <code>com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.structure.CryptoBrokerActorNetworkServiceManager</code>
 * is the manager of the plug-in crypto broker actor network service of the cbp platform.
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 20/11/2015.
 */
public final class CryptoBrokerActorNetworkServiceManager implements CryptoBrokerManager {

    private final CommunicationsClientConnection communicationsClientConnection;
    private final CryptoBrokerActorNetworkServiceDao cryptoBrokerActorNetworkServiceDao;
    private final ErrorManager                   errorManager                  ;
    private final PluginVersionReference         pluginVersionReference        ;
    private final QuotesHelpers helper;


    private PlatformComponentProfile platformComponentProfile;

    public CryptoBrokerActorNetworkServiceManager(final CommunicationsClientConnection communicationsClientConnection,
                                                  final CryptoBrokerActorNetworkServiceDao cryptoBrokerActorNetworkServiceDao,
                                                  final ErrorManager                   errorManager                  ,
                                                  final PluginVersionReference         pluginVersionReference        ) {

        this.communicationsClientConnection = communicationsClientConnection;
        this.cryptoBrokerActorNetworkServiceDao = cryptoBrokerActorNetworkServiceDao;
        this.errorManager                   = errorManager                  ;
        this.pluginVersionReference         = pluginVersionReference        ;

        helper = new QuotesHelpers(errorManager, pluginVersionReference);
    }

    private ConcurrentHashMap<String, CryptoBrokerExposingData> cryptoBrokersToExpose;

    @Override
    public final void exposeIdentity(final CryptoBrokerExposingData cryptoBroker) throws CantExposeIdentityException {

        try {

            if (!isRegistered()) {

                addCryptoBrokerToExpose(cryptoBroker);

            } else {

                final String imageString = Base64.encodeToString(cryptoBroker.getImage(), Base64.DEFAULT);

                final PlatformComponentProfile actorPlatformComponentProfile = communicationsClientConnection.constructPlatformComponentProfileFactory(
                        cryptoBroker.getPublicKey(),
                        (cryptoBroker.getAlias().toLowerCase()),
                        (cryptoBroker.getAlias().toLowerCase() + "_" + platformComponentProfile.getName().replace(" ", "_")),
                        NetworkServiceType.UNDEFINED,
                        PlatformComponentType.ACTOR_CRYPTO_BROKER,
                        imageString
                );

                communicationsClientConnection.registerComponentForCommunication(platformComponentProfile.getNetworkServiceType(), actorPlatformComponentProfile);

                if (cryptoBrokersToExpose != null && cryptoBrokersToExpose.containsKey(cryptoBroker.getPublicKey()))
                    cryptoBrokersToExpose.remove(cryptoBroker.getPublicKey());
            }

        } catch (final CantRegisterComponentException e) {

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantExposeIdentityException(e, null, "Problem trying to register an identity component.");

        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantExposeIdentityException(e, null, "Unhandled Exception.");
        }
    }

    private void addCryptoBrokerToExpose(final CryptoBrokerExposingData cryptoBrokerExposingData) {

        if (cryptoBrokersToExpose == null)
            cryptoBrokersToExpose = new ConcurrentHashMap<>();

        cryptoBrokersToExpose.putIfAbsent(cryptoBrokerExposingData.getPublicKey(), cryptoBrokerExposingData);
    }

    @Override
    public final void exposeIdentities(final Collection<CryptoBrokerExposingData> cryptoBrokerExposingDataList) throws CantExposeIdentitiesException {

        try {

            for (final CryptoBrokerExposingData cryptoBroker : cryptoBrokerExposingDataList)
                this.exposeIdentity(cryptoBroker);

        } catch (final CantExposeIdentityException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantExposeIdentitiesException(e, null, "Problem trying to expose an identity.");
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantExposeIdentitiesException(e, null, "Unhandled Exception.");
        }
    }

    private boolean isRegistered() {
        return platformComponentProfile != null;
    }

    public final void setPlatformComponentProfile(final PlatformComponentProfile platformComponentProfile) {

        this.platformComponentProfile = platformComponentProfile;

        if (platformComponentProfile != null && cryptoBrokersToExpose != null && !cryptoBrokersToExpose.isEmpty()) {

            try {

                this.exposeIdentities(cryptoBrokersToExpose.values());

            } catch (final CantExposeIdentitiesException e){

                errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            }
        }
    }

    @Override
    public final CryptoBrokerSearch getSearch() {
        return new CryptoBrokerActorNetworkServiceSearch(communicationsClientConnection, errorManager, pluginVersionReference);
    }

    /**
     * I indicate to the Agent the action that it must take:
     * - Protocol State: PROCESSING_SEND.
     * - Action        : REQUEST.
     * - Type          : SENT.
     */
    @Override
    public final void requestConnection(final CryptoBrokerConnectionInformation brokerInformation) throws CantRequestConnectionException {

        try {

            final ProtocolState           state  = ProtocolState          .PROCESSING_SEND;
            final RequestType             type   = RequestType            .SENT           ;
            final ConnectionRequestAction action = ConnectionRequestAction.REQUEST        ;

            cryptoBrokerActorNetworkServiceDao.createConnectionRequest(
                    brokerInformation,
                    state            ,
                    type             ,
                    action
            );

        } catch (final CantRequestConnectionException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantRequestConnectionException(e, null, "Unhandled Exception.");
        }
    }

    /**
     * I indicate to the Agent the action that it must take:
     * - Protocol State: PROCESSING_SEND.
     * - Action        : DISCONNECT.
     */
    @Override
    public final void disconnect(final UUID requestId) throws CantDisconnectException            ,
                                                              ConnectionRequestNotFoundException {

        try {

            final ProtocolState state = ProtocolState.PROCESSING_SEND;

            cryptoBrokerActorNetworkServiceDao.disconnectConnection(
                    requestId,
                    state
            );

        } catch (final CantDisconnectException | ConnectionRequestNotFoundException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantDisconnectException(e, null, "Unhandled Exception.");
        }
    }

    /**
     * I update the record with the new address an then, i indicate the ns agent the action that it must take:
     * - Action        : DENY.
     * - Protocol State: PROCESSING_SEND.
     */
    @Override
    public final void denyConnection(final UUID requestId) throws CantDenyConnectionRequestException ,
                                                                  ConnectionRequestNotFoundException {

        try {

            final ProtocolState protocolState = ProtocolState.PROCESSING_SEND;

            cryptoBrokerActorNetworkServiceDao.denyConnection(
                    requestId,
                    protocolState
            );

        } catch (final CantDenyConnectionRequestException | ConnectionRequestNotFoundException e){
            // ConnectionRequestNotFoundException - THIS SHOULD NOT HAPPEN.
            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantDenyConnectionRequestException(e, null, "Unhandled Exception.");
        }
    }

    /**
     * I update the record with the new address an then, i indicate the ns agent the action that it must take:
     * - Action        : CANCEL.
     * - Protocol State: PROCESSING_SEND.
     *
     * We must to validate if the record is in PENDING_REMOTE_ACTION.
     */
    @Override
    public final void cancelConnection(final UUID requestId) throws CantCancelConnectionRequestException,
                                                                    ConnectionRequestNotFoundException  {

    }

    /**
     * I update the record with the new address an then, i indicate the ns agent the action that it must take:
     * - Action        : ACCEPT.
     * - Protocol State: PROCESSING_SEND.
     */
    @Override
    public final void acceptConnection(final UUID requestId) throws CantAcceptConnectionRequestException,
                                                                    ConnectionRequestNotFoundException  {

        try {

            final ProtocolState protocolState = ProtocolState.PROCESSING_SEND;

            cryptoBrokerActorNetworkServiceDao.acceptConnection(
                    requestId,
                    protocolState
            );

        } catch (final CantAcceptConnectionRequestException | ConnectionRequestNotFoundException e){
            // ConnectionRequestNotFoundException - THIS SHOULD NOT HAPPEN.
            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantAcceptConnectionRequestException(e, null, "Unhandled Exception.");
        }
    }

    /**
     * we'll return all the request news with a pending local action.
     * State : PENDING_LOCAL_ACTION.
     *
     * @throws CantListPendingConnectionRequestsException  if something goes wrong.
     */
    @Override
    public final List<CryptoBrokerConnectionRequest> listPendingConnectionNews(Actors actorType) throws CantListPendingConnectionRequestsException {

        try {

            return cryptoBrokerActorNetworkServiceDao.listPendingConnectionNews(actorType);

        } catch (final CantListPendingConnectionRequestsException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListPendingConnectionRequestsException(e, null, "Unhandled Exception.");
        }
    }

    /**
     * we'll return all the request updates with a pending local action.
     * State : PENDING_LOCAL_ACTION.
     *
     * @throws CantListPendingConnectionRequestsException  if something goes wrong.
     */
    @Override
    public final List<CryptoBrokerConnectionRequest> listPendingConnectionUpdates() throws CantListPendingConnectionRequestsException {

        try {

            return cryptoBrokerActorNetworkServiceDao.listPendingConnectionUpdates();

        } catch (final CantListPendingConnectionRequestsException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListPendingConnectionRequestsException(e, null, "Unhandled Exception.");
        }
    }


    @Override
    public CryptoBrokerExtraDataInfo requestQuotes(
            final String requesterPublicKey,
            final Actors requesterActorType,
            final String cryptoBrokerPublicKey) throws CantRequestQuotesException {

        try {

            final UUID newId = UUID.randomUUID();

            final ProtocolState state = ProtocolState.PROCESSING_SEND;
            final RequestType   type  = RequestType.SENT;

            CryptoBrokerActorNetworkServiceQuotesRequest temp = (CryptoBrokerActorNetworkServiceQuotesRequest) cryptoBrokerActorNetworkServiceDao.createQuotesRequest(
                    newId                ,
                    requesterPublicKey   ,
                    requesterActorType   ,
                    cryptoBrokerPublicKey,
                    state                ,
                    type
            );

            List<CryptoBrokerQuote> quotes = helper.getListQuotesForString(temp.listInformation());

            CryptoBrokerExtraDataInfo info = new CryptoBrokerActorNetworkServiceQuotesRequestTemp(
                    temp.getRequestId(),
                    temp.getRequesterPublicKey(),
                    temp.getRequesterActorType(),
                    temp.getCryptoBrokerPublicKey(),
                    temp.getUpdateTime(),
                    quotes,
                    temp.getType(),
                    temp.getState()
            );

            return info;

        } catch (final CantRequestQuotesException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantRequestQuotesException(e, null, "Unhandled Exception.");
        }
    }

    @Override
    public List<CryptoBrokerExtraDataInfo> listPendingQuotesRequests(final RequestType requestType) throws CantListPendingQuotesRequestsException {

        try {

            List<CryptoBrokerExtraDataInfo> res = new ArrayList<>();

            List<CryptoBrokerExtraDataInfoTemp> temp = cryptoBrokerActorNetworkServiceDao.listPendingQuotesRequests(ProtocolState.PENDING_LOCAL_ACTION, requestType);

            if(!temp.isEmpty()) {
                for (CryptoBrokerExtraDataInfoTemp t : temp) {
                    CryptoBrokerActorNetworkServiceQuotesRequest r = (CryptoBrokerActorNetworkServiceQuotesRequest) t;

                    List<CryptoBrokerQuote> quotes = helper.getListQuotesForString(t.listInformation());

                    CryptoBrokerExtraDataInfo info = new CryptoBrokerActorNetworkServiceQuotesRequestTemp(
                            r.getRequestId(),
                            r.getRequesterPublicKey(),
                            r.getRequesterActorType(),
                            r.getCryptoBrokerPublicKey(),
                            r.getUpdateTime(),
                            quotes,
                            r.getType(),
                            r.getState()
                    );

                    res.add(info);

                    if(requestType == RequestType.SENT){
                        cryptoBrokerActorNetworkServiceDao.changeProtocolStateQuote(r.getRequestId(), ProtocolState.DONE);
                    }
                }
            }

            return res;

        } catch (final CantListPendingQuotesRequestsException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListPendingQuotesRequestsException(e, null, "Unhandled Exception.");
        }
    }

    @Override
    public void answerQuotesRequest(final UUID                    requestId ,
                                    final long                    updateTime,
                                    final List<CryptoBrokerQuote> quotes    ) throws CantAnswerQuotesRequestException,
                                                                                     QuotesRequestNotFoundException  {

        try {

            cryptoBrokerActorNetworkServiceDao.answerQuotesRequest(
                    requestId                    ,
                    updateTime                   ,
                    quotes                       ,
                    ProtocolState.PROCESSING_SEND
            );

        } catch (final QuotesRequestNotFoundException | CantAnswerQuotesRequestException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantAnswerQuotesRequestException(e, null, "Unhandled Exception.");
        }
    }

    @Override
    public void confirm(final UUID requestId) throws CantConfirmException, ConnectionRequestNotFoundException {

        try {

            cryptoBrokerActorNetworkServiceDao.confirmActorConnectionRequest(requestId);

        } catch (final ConnectionRequestNotFoundException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final CantConfirmConnectionRequestException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantConfirmException(e, "", "Error in DAO, trying to confirm the request.");
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantConfirmException(e, null, "Unhandled Exception.");
        }
    }
}
