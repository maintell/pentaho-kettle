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

package org.pentaho.di.trans.steps.jsoninput.exception;

import org.pentaho.di.core.exception.KettleException;

public class JsonInputException extends KettleException {

  public JsonInputException() {
  }

  public JsonInputException( String message ) {
    super( message );
  }

  public JsonInputException( Throwable cause ) {
    super( cause );
  }

  public JsonInputException( String message, Throwable cause ) {
    super( message, cause );
  }

}
