/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
/**
 * UserAndRoleManagement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 09, 2010 (01:02:43 CEST) WSDL2Java emitter.
 */

package com.jaspersoft.ireport.jasperserver.ws.userandroles;

public interface UserAndRoleManagement extends java.rmi.Remote {
    public com.jaspersoft.ireport.jasperserver.ws.WSUser[] findUsers(com.jaspersoft.ireport.jasperserver.ws.userandroles.WSUserSearchCriteria criteria) throws java.rmi.RemoteException;
    public com.jaspersoft.ireport.jasperserver.ws.WSUser putUser(com.jaspersoft.ireport.jasperserver.ws.WSUser user) throws java.rmi.RemoteException;
    public void deleteUser(com.jaspersoft.ireport.jasperserver.ws.WSUser user) throws java.rmi.RemoteException;
    public com.jaspersoft.ireport.jasperserver.ws.WSRole[] findRoles(com.jaspersoft.ireport.jasperserver.ws.userandroles.WSRoleSearchCriteria criteria) throws java.rmi.RemoteException;
    public com.jaspersoft.ireport.jasperserver.ws.WSRole putRole(com.jaspersoft.ireport.jasperserver.ws.WSRole role) throws java.rmi.RemoteException;
    public com.jaspersoft.ireport.jasperserver.ws.WSRole updateRoleName(com.jaspersoft.ireport.jasperserver.ws.WSRole oldRole, java.lang.String newName) throws java.rmi.RemoteException;
    public void deleteRole(com.jaspersoft.ireport.jasperserver.ws.WSRole role) throws java.rmi.RemoteException;
}
