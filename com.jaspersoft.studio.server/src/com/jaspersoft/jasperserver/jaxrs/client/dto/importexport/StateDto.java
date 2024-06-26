/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/

package com.jaspersoft.jasperserver.jaxrs.client.dto.importexport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

/**
 * @author: Zakhar.Tomchenco
 */
@XmlRootElement(name = "state")
public class StateDto {

    private String id;
    private String message;
    private String phase;
    private ErrorDescriptor errorDescriptor;


    public StateDto() {
        super();
    }

    @XmlElement(name = "id")
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "phase")
    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    @XmlElement(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorDescriptor getErrorDescriptor() {
        return errorDescriptor;
    }

    public void setErrorDescriptor(ErrorDescriptor errorDescriptor) {
        this.errorDescriptor = errorDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StateDto)) return false;

        StateDto stateDto = (StateDto) o;

        if (errorDescriptor != null ? !errorDescriptor.equals(stateDto.errorDescriptor) : stateDto.errorDescriptor != null)
            return false;
        if (id != null ? !id.equals(stateDto.id) : stateDto.id != null) return false;
        if (message != null ? !message.equals(stateDto.message) : stateDto.message != null) return false;
        if (phase != null ? !phase.equals(stateDto.phase) : stateDto.phase != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (phase != null ? phase.hashCode() : 0);
        result = 31 * result + (errorDescriptor != null ? errorDescriptor.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StateDto{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", phase='" + phase + '\'' +
                ", errorDescriptor=" + errorDescriptor +
                '}';
    }
}
