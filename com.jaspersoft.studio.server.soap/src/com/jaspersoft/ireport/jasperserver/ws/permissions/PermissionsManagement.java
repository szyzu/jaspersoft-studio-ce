/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
/**
 * PermissionsManagement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 09, 2010 (01:02:43 CEST) WSDL2Java emitter.
 */

package com.jaspersoft.ireport.jasperserver.ws.permissions;

public interface PermissionsManagement extends java.rmi.Remote {
    public com.jaspersoft.ireport.jasperserver.ws.permissions.WSObjectPermission[] getPermissionsForObject(java.lang.String targetURI) throws java.rmi.RemoteException;
    public com.jaspersoft.ireport.jasperserver.ws.permissions.WSObjectPermission putPermission(com.jaspersoft.ireport.jasperserver.ws.permissions.WSObjectPermission objectPermission) throws java.rmi.RemoteException;
    public void deletePermission(com.jaspersoft.ireport.jasperserver.ws.permissions.WSObjectPermission objectPermission) throws java.rmi.RemoteException;
}
