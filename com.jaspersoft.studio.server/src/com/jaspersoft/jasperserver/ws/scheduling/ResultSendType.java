/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/


package com.jaspersoft.jasperserver.ws.scheduling;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.ReportJobSendTypeXmlAdapter;

/**
 * ResultSendType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */
public class ResultSendType implements java.io.Serializable {
    private java.lang.String value;
    private static java.util.HashMap table = new java.util.HashMap();

    // Constructor
    protected ResultSendType(java.lang.String value) {
        this.value = value;
        table.put(this.value,this);
    }

        // Constructor
    protected ResultSendType() {
    }

    public static final java.lang.String _SEND = ReportJobSendTypeXmlAdapter.SendType.SEND.name();
    public static final java.lang.String _SEND_ATTACHMENT = ReportJobSendTypeXmlAdapter.SendType.SEND_ATTACHMENT.name();
    public static final ResultSendType SEND = new ResultSendType(_SEND);
    public static final ResultSendType SEND_ATTACHMENT = new ResultSendType(_SEND_ATTACHMENT);

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static ResultSendType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ResultSendType enumeration = (ResultSendType)
            table.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ResultSendType fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return value;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(value);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResultSendType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.jasperforge.org/jasperserver/ws", "ResultSendType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
