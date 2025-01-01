package com.kewebsi.html;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kewebsi.controller.BaseVal;
import com.kewebsi.errorhandling.ErrorUpdate;

/**
 * Data to be sent from the server to the client
 */
public class ClientDataOrError {



    protected String data;
    public ErrorUpdate error;

    public ClientDataOrError(BaseVal data, ErrorUpdate error) {
        this.data = data.toJsonString();
        this.error = error;
    }

    public ClientDataOrError(ErrorUpdate error) {
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



    public ErrorUpdate getError() {
        return error;
    }

    public void setError(ErrorUpdate error) {
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
