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

package org.pentaho.di.trans.steps.propertyinput;

import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;

public class PropertyInputContentParsingTest extends BasePropertyParsingTest {
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();
  @Test
  public void testDefaultOptions() throws Exception {
    init( "default.properties" );

    PropertyInputField f1 = new PropertyInputField( "f1" );
    f1.setColumn( PropertyInputField.COLUMN_KEY );
    PropertyInputField f2 = new PropertyInputField( "f2" );
    f2.setColumn( PropertyInputField.COLUMN_VALUE );
    setFields( f1, f2 );

    process();

    check( new Object[][] { { "f1", "d1" }, { "f2", "d2" } } );
  }
}
