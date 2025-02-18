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

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.StepInterface;

//import java.util.concurrent.TimeUnit;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by mburgess on 10/7/15.
 */
public class RowProducerTest {

  RowProducer rowProducer;
  StepInterface stepInterface;
  RowSet rowSet;
  RowMetaInterface rowMeta;
  Object[] rowData;

  @Before
  public void setUp() throws Exception {
    stepInterface = mock( StepInterface.class );
    rowSet = mock( RowSet.class );
    rowProducer = new RowProducer( stepInterface, rowSet );
    rowMeta = mock( RowMetaInterface.class );
    rowData = new Object[]{};
  }

  @Test
  public void testPutRow2Arg() {
    when( rowSet.putRowWait( any( RowMetaInterface.class ), any( Object[].class ), anyLong(), any( TimeUnit.class ) ) )
      .thenReturn( true );
    rowProducer.putRow( rowMeta, rowData );
    verify( rowSet, times( 1 ) ).putRowWait( rowMeta, rowData, Long.MAX_VALUE, TimeUnit.DAYS );
    assertTrue( rowProducer.putRow( rowMeta, rowData, true ) );
  }

  @Test
  public void testPutRow3Arg() {
    when( rowSet.putRowWait( any( RowMetaInterface.class ), any( Object[].class ), anyLong(), any( TimeUnit.class ) ) )
      .thenReturn( true );

    rowProducer.putRow( rowMeta, rowData, false );
    verify( rowSet, times( 1 ) ).putRow( rowMeta, rowData );
  }

  @Test
  public void testPutRowWait() {
    rowProducer.putRowWait( rowMeta, rowData, 1, TimeUnit.MILLISECONDS );
    verify( rowSet, times( 1 ) ).putRowWait( rowMeta, rowData, 1, TimeUnit.MILLISECONDS );
  }

  @Test
  public void testFinished() {
    rowProducer.finished();
    verify( rowSet, times( 1 ) ).setDone();
  }

  @Test
  public void testGetSetRowSet() {
    assertEquals( rowSet, rowProducer.getRowSet() );
    rowProducer.setRowSet( null );
    assertNull( rowProducer.getRowSet() );
    RowSet newRowSet = mock( RowSet.class );
    rowProducer.setRowSet( newRowSet );
    assertEquals( newRowSet, rowProducer.getRowSet() );
  }

  @Test
  public void testGetSetStepInterface() {
    assertEquals( stepInterface, rowProducer.getStepInterface() );
    rowProducer.setStepInterface( null );
    assertNull( rowProducer.getStepInterface() );
    StepInterface newStepInterface = mock( StepInterface.class );
    rowProducer.setStepInterface( newStepInterface );
    assertEquals( newStepInterface, rowProducer.getStepInterface() );
  }
}
