package com.kewebsi.html;

import com.kewebsi.errorhandling.CanHaveError;

public interface PageVarEditor<T> extends CanHaveError  {   // TODO: Review if this class is necessary.

    public void setNonNullValueValidating(T val);

    public void setValueValidatingAllowNull(T val);

    /**
     * Returns the PageStateVar if a PageState exists and the PageState has a registered variable, otherwise null.
     * @return A PageStateVar if a PageState exists, otherwise null.
     */
    public PageStateVarIntf getPageStateVar();


    /**
     * CLIENT_INPUT_INCOMPLETE is meant for input elements that have multiple sub-elements like date/time or x/y
     * coordinates. The handling of an incomplete input might differ slighlty from an unparseable input. For example
     * the user might be prompted to complete the data rather then being informed of an error.
     *
     * SYNCHRONIZED is the standard case. It can mean on of the following situations:
     * - The client input has been accepted and applied to the server side data.
     * - The server data has been updated and the client was fed OR IS GOING TO BE fed the data.
     * Note that in both sub-cases of synchronized, server-side data will be communicated to the client when it has
     * in fact changed. (See HmtlPageVarField.valueModified()).
     *
     */
    enum ClientSyncState {CLIENT_INPUT_INCOMPLETE, CLIENT_INPUT_UNPARSEABLE_ON_CLIENT, CLIENT_INPUT_UNPARSEABLE_ON_SERVER, CLIENT_IS_SYNCED};

    // public void setClientSyncInfo(ClientSyncState clientSyncState);

    public ClientSyncState getClientSyncState();



}

