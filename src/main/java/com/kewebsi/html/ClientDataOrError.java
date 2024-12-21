package com.kewebsi.html;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kewebsi.controller.BaseVal;
import com.kewebsi.errorhandling.ErrorInfo;

/**
 * Data to be sent from the server to the client
 */
public class ClientDataOrError {



    protected String data;
    public ErrorInfo error;

    public ClientDataOrError(BaseVal data, ErrorInfo error) {
        this.data = data.toJsonString();
        this.error = error;
    }

    public ClientDataOrError(ErrorInfo error) {
        this.data = data;
        this.error = error;
    }

    /**
     *
     * @param baseVal Can be null
     */
    public ClientDataOrError(BaseVal baseVal) {
       setData(baseVal);
       this.error = error;
    }


    @JsonIgnore
    public Object getData() {
        return data;
    }



    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }


    /**
     * Can be null;
     * @param baseVal
     */

    public void setData(BaseVal baseVal) {
        if (baseVal == null) {
            data = null;
        } else {
            data = baseVal.toJsonString();
        }
    }



}
