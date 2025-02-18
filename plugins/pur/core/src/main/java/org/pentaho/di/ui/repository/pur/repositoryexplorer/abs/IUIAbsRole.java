/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.di.ui.repository.pur.repositoryexplorer.abs;

import java.util.List;

import org.pentaho.di.ui.repository.pur.repositoryexplorer.IUIRole;

public interface IUIAbsRole extends IUIRole {
  /**
   * Associate a logical role to the runtime role
   * 
   * @param logical
   *          role name to be associated
   */
  public void addLogicalRole( String logicalRole );

  /**
   * Remove the logical role association from this particular runtime role
   * 
   * @param logical
   *          role name to be un associated
   */
  public void removeLogicalRole( String logicalRole );

  /**
   * Check whether a logical role is associated to this runtime role
   * 
   * @param logical
   *          role name to be checked
   */
  public boolean containsLogicalRole( String logicalRole );

  /**
   * Associate set of logical roles to this particular runtime role
   * 
   * @param list
   *          of logical role name
   */
  public void setLogicalRoles( List<String> logicalRoles );

  /**
   * Retrieve the list of roles association for this particular runtime role
   * 
   * @return list of associated roles
   */
  public List<String> getLogicalRoles();

}
