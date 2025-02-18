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


package org.pentaho.di.trans.steps.prioritizestreams;

import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.QueueRowSet;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.SingleRowRowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PrioritizeStreamsTest {

  private static StepMockHelper<PrioritizeStreamsMeta, StepDataInterface> stepMockHelper;

  @BeforeClass
  public static void setup() {
    stepMockHelper =
      new StepMockHelper<>( "Priority Streams Test",
        PrioritizeStreamsMeta.class, StepDataInterface.class );
    when( stepMockHelper.logChannelInterfaceFactory.create( any(), any( LoggingObjectInterface.class ) ) ).thenReturn(
        stepMockHelper.logChannelInterface );
    when( stepMockHelper.trans.isRunning() ).thenReturn( true );
  }

  @AfterClass
  public static void tearDown() {
    stepMockHelper.cleanUp();
  }

  /**
   * [PDI-9088] Prioritize streams step causing null pointer exception downstream 
   * @throws KettleException
   */
  @Test
  public void testProcessRow() throws KettleException {
    PrioritizeStreamsMeta meta = new PrioritizeStreamsMeta();
    meta.setStepName( new String[] { "high", "medium", "low" } );
    PrioritizeStreamsData data = new PrioritizeStreamsData();

    PrioritizeStreamsInner step = new PrioritizeStreamsInner( stepMockHelper );
    try {
      step.processRow( meta, data );
    } catch ( NullPointerException e ) {
      fail( "NullPointerException detecded, seems that RowMetaInterface was not set for RowSet you are attempting"
          + "to read from." );
    }

    Assert.assertEquals( "First waiting for row set is 'high'", data.currentRowSet.getClass(), SingleRowRowSet.class );
  }

  private static class PrioritizeStreamsInner extends PrioritizeStreams {

    public PrioritizeStreamsInner( StepMockHelper<PrioritizeStreamsMeta, StepDataInterface> stepMockHelper ) {
      super( stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta,
          stepMockHelper.trans );
    }

    @Override
    public RowSet findInputRowSet( String sourceStep ) {
      if ( sourceStep.equals( "high" ) ) {
        return new SingleRowRowSet();
      }
      return new QueueRowSet();
    }

    @Override
    protected void checkInputLayoutValid( RowMetaInterface referenceRowMeta, RowMetaInterface compareRowMeta ) {
      // always true.
    }

    @Override
    public Object[] getRowFrom( RowSet rowSet ) {
      rowSet.setRowMeta( new RowMeta() );
      return new Object[] {};
    }

    @Override
    public void putRow( RowMetaInterface rmi, Object[] input ) {
      if ( rmi == null ) {
        throw new NullPointerException();
      }
    }
  }
}
