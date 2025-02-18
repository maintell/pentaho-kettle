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


package org.pentaho.di.trans.steps.tableinput;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.StepPartitioningMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class TableInputTest {

  TableInputMeta mockStepMetaInterface;
  TableInputData mockStepDataInterface;
  TableInput mockTableInput;

  @Before
  public void setUp() {

    StepMeta mockStepMeta = mock( StepMeta.class );
    TransMeta mockTransMeta = mock( TransMeta.class );
    Trans mockTrans = mock( Trans.class );
    StepPartitioningMeta mockStepPartitioningMeta = mock( StepPartitioningMeta.class );

    when( mockStepMeta.getName() ).thenReturn( "MockStep" );
    when( mockTransMeta.findStep( anyString() ) ).thenReturn( mockStepMeta );
    when( mockStepMeta.getTargetStepPartitioningMeta() ).thenReturn( mockStepPartitioningMeta );

    mockStepMetaInterface = mock( TableInputMeta.class, withSettings().extraInterfaces( StepMetaInterface.class ) );
    mockStepDataInterface = mock( TableInputData.class, withSettings().extraInterfaces( StepMetaInterface.class ) );
    mockStepDataInterface.db = mock( Database.class );
    mockTableInput = spy( new TableInput( mockStepMeta, mockStepDataInterface, 1, mockTransMeta, mockTrans ) );
  }

  @Test
  public void testStopRunningWhenStepIsStopped() throws KettleException {
    doReturn( true ).when( mockTableInput ).isStopped();

    mockTableInput.stopRunning( mockStepMetaInterface, mockStepDataInterface );

    verify( mockTableInput, times( 1 ) ).isStopped();
    verify( mockStepDataInterface, times( 0 ) ).isDisposed();
  }

  @Test
  public void testStopRunningWhenStepDataInterfaceIsDisposed() throws KettleException {
    doReturn( false ).when( mockTableInput ).isStopped();
    doReturn( true ).when( mockStepDataInterface ).isDisposed();

    mockTableInput.stopRunning( mockStepMetaInterface, mockStepDataInterface );

    verify( mockTableInput, times( 1 ) ).isStopped();
    verify( mockStepDataInterface, times( 1 ) ).isDisposed();
  }

  @Test
  public void testStopRunningWhenStepIsNotStoppedNorStepDataInterfaceIsDisposedAndDatabaseConnectionIsValid() throws KettleException {
    doReturn( false ).when( mockTableInput ).isStopped();
    doReturn( false ).when( mockStepDataInterface ).isDisposed();
    when( mockStepDataInterface.db.getConnection() ).thenReturn( mock( Connection.class ) );

    mockTableInput.stopRunning( mockStepMetaInterface, mockStepDataInterface );

    verify( mockTableInput, times( 1 ) ).isStopped();
    verify( mockStepDataInterface, times( 1 ) ).isDisposed();
    verify( mockStepDataInterface.db, times( 1 ) ).getConnection();
    verify( mockStepDataInterface.db, times( 1 ) ).cancelQuery();
    assertTrue( mockStepDataInterface.isCanceled );

  }

  @Test
  public void testStopRunningWhenStepIsNotStoppedNorStepDataInterfaceIsDisposedAndDatabaseConnectionIsNotValid() throws KettleException {
    doReturn( false ).when( mockTableInput ).isStopped();
    doReturn( false ).when( mockStepDataInterface ).isDisposed();
    when( mockStepDataInterface.db.getConnection() ).thenReturn( null );

    mockTableInput.stopRunning( mockStepMetaInterface, mockStepDataInterface );

    verify( mockTableInput, times( 1 ) ).isStopped();
    verify( mockStepDataInterface, times( 1 ) ).isDisposed();
    verify( mockStepDataInterface.db, times( 1 ) ).getConnection();
    verify( mockStepDataInterface.db, times( 0 ) ).cancelStatement( any( PreparedStatement.class ) );
    assertFalse( mockStepDataInterface.isCanceled );
  }
}
