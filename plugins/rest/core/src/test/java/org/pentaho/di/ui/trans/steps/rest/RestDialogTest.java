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


package org.pentaho.di.ui.trans.steps.rest;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.trans.steps.rest.RestMeta;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RestDialogTest {

  private Label bodyl = mock( Label.class );
  private ComboVar body = mock( ComboVar.class );
  private ComboVar type = mock( ComboVar.class );

  private Label paramsl = mock( Label.class );
  private TableView params = mock( TableView.class );
  private Button paramsb = mock( Button.class );

  private Label matrixl = mock( Label.class );
  private TableView matrix = mock( TableView.class );
  private Button matrixb = mock( Button.class );

  private ComboVar method = mock( ComboVar.class );

  private RestDialog dialog = mock( RestDialog.class );

  @Before
  public void setup() {
    doCallRealMethod().when( dialog ).setMethod();

    ReflectionTestUtils.setField( dialog, "wlBody", bodyl );
    ReflectionTestUtils.setField( dialog, "wBody", body );
    ReflectionTestUtils.setField( dialog, "wApplicationType", type );

    ReflectionTestUtils.setField( dialog, "wlParameters", paramsl );
    ReflectionTestUtils.setField( dialog, "wParameters", params );
    ReflectionTestUtils.setField( dialog, "wGet", paramsb );

    ReflectionTestUtils.setField( dialog, "wlMatrixParameters", matrixl );
    ReflectionTestUtils.setField( dialog, "wMatrixParameters", matrix );
    ReflectionTestUtils.setField( dialog, "wMatrixGet", matrixb );

    ReflectionTestUtils.setField( dialog, "wMethod", method );
  }

  @Test
  public void testSetMethod_GET() {
    doReturn( RestMeta.HTTP_METHOD_GET ).when( method ).getText();

    dialog.setMethod();

    verify( bodyl, times( 1 ) ).setEnabled( false );
    verify( body, times( 1 ) ).setEnabled( false );
    verify( type, times( 1 ) ).setEnabled( false );

    verify( paramsl, times( 1 ) ).setEnabled( false );
    verify( params, times( 1 ) ).setEnabled( false );
    verify( paramsb, times( 1 ) ).setEnabled( false );

    verify( matrixl, times( 1 ) ).setEnabled( false );
    verify( matrix, times( 1 ) ).setEnabled( false );
    verify( matrixb, times( 1 ) ).setEnabled( false );
  }

  @Test
  public void testSetMethod_POST() {
    doReturn( RestMeta.HTTP_METHOD_POST ).when( method ).getText();

    dialog.setMethod();

    verify( bodyl, times( 1 ) ).setEnabled( true );
    verify( body, times( 1 ) ).setEnabled( true );
    verify( type, times( 1 ) ).setEnabled( true );

    verify( paramsl, times( 1 ) ).setEnabled( true );
    verify( params, times( 1 ) ).setEnabled( true );
    verify( paramsb, times( 1 ) ).setEnabled( true );

    verify( matrixl, times( 1 ) ).setEnabled( true );
    verify( matrix, times( 1 ) ).setEnabled( true );
    verify( matrixb, times( 1 ) ).setEnabled( true );
  }

  @Test
  public void testSetMethod_PUT() {
    doReturn( RestMeta.HTTP_METHOD_PUT ).when( method ).getText();

    dialog.setMethod();

    verify( bodyl, times( 1 ) ).setEnabled( true );
    verify( body, times( 1 ) ).setEnabled( true );
    verify( type, times( 1 ) ).setEnabled( true );

    verify( paramsl, times( 1 ) ).setEnabled( true );
    verify( params, times( 1 ) ).setEnabled( true );
    verify( paramsb, times( 1 ) ).setEnabled( true );

    verify( matrixl, times( 1 ) ).setEnabled( true );
    verify( matrix, times( 1 ) ).setEnabled( true );
    verify( matrixb, times( 1 ) ).setEnabled( true );
  }

  @Test
  public void testSetMethod_PATCH() {
    doReturn( RestMeta.HTTP_METHOD_PATCH ).when( method ).getText();

    dialog.setMethod();

    verify( bodyl, times( 1 ) ).setEnabled( true );
    verify( body, times( 1 ) ).setEnabled( true );
    verify( type, times( 1 ) ).setEnabled( true );

    verify( paramsl, times( 1 ) ).setEnabled( true );
    verify( params, times( 1 ) ).setEnabled( true );
    verify( paramsb, times( 1 ) ).setEnabled( true );

    verify( matrixl, times( 1 ) ).setEnabled( true );
    verify( matrix, times( 1 ) ).setEnabled( true );
    verify( matrixb, times( 1 ) ).setEnabled( true );
  }

  @Test
  public void testSetMethod_DELETE() {
    doReturn( RestMeta.HTTP_METHOD_DELETE ).when( method ).getText();

    dialog.setMethod();

    verify( bodyl, times( 1 ) ).setEnabled( false );
    verify( body, times( 1 ) ).setEnabled( false );
    verify( type, times( 1 ) ).setEnabled( false );

    verify( paramsl, times( 1 ) ).setEnabled( true );
    verify( params, times( 1 ) ).setEnabled( true );
    verify( paramsb, times( 1 ) ).setEnabled( true );

    verify( matrixl, times( 1 ) ).setEnabled( true );
    verify( matrix, times( 1 ) ).setEnabled( true );
    verify( matrixb, times( 1 ) ).setEnabled( true );
  }

  @Test
  public void testSetMethod_OPTIONS() {
    doReturn( RestMeta.HTTP_METHOD_OPTIONS ).when( method ).getText();

    dialog.setMethod();

    verify( bodyl, times( 1 ) ).setEnabled( false );
    verify( body, times( 1 ) ).setEnabled( false );
    verify( type, times( 1 ) ).setEnabled( false );

    verify( paramsl, times( 1 ) ).setEnabled( false );
    verify( params, times( 1 ) ).setEnabled( false );
    verify( paramsb, times( 1 ) ).setEnabled( false );

    verify( matrixl, times( 1 ) ).setEnabled( false );
    verify( matrix, times( 1 ) ).setEnabled( false );
    verify( matrixb, times( 1 ) ).setEnabled( false );
  }

  @Test
  public void testSetMethod_HEAD() {
    doReturn( RestMeta.HTTP_METHOD_HEAD ).when( method ).getText();

    dialog.setMethod();

    verify( bodyl, times( 1 ) ).setEnabled( false );
    verify( body, times( 1 ) ).setEnabled( false );
    verify( type, times( 1 ) ).setEnabled( false );

    verify( paramsl, times( 1 ) ).setEnabled( false );
    verify( params, times( 1 ) ).setEnabled( false );
    verify( paramsb, times( 1 ) ).setEnabled( false );

    verify( matrixl, times( 1 ) ).setEnabled( false );
    verify( matrix, times( 1 ) ).setEnabled( false );
    verify( matrixb, times( 1 ) ).setEnabled( false );
  }
}
