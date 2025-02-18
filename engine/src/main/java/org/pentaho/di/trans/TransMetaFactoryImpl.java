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


package org.pentaho.di.trans;

import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.repository.Repository;
import org.w3c.dom.Node;

public class TransMetaFactoryImpl implements TransMetaFactory {

  @Override
  public TransMeta create( Node transnode, Repository rep ) throws KettleXMLException, KettleMissingPluginsException {
    return new TransMeta( transnode, rep );
  }
}
