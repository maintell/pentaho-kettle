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


package org.pentaho.di.trans.steps.syslog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogIF;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * User: Dzmitry Stsiapanau Date: 1/23/14 Time: 11:04 AM
 */
public class SyslogMessageTest {

  private StepMockHelper<SyslogMessageMeta, SyslogMessageData> stepMockHelper;

  @Before
  public void setUp() throws Exception {
    stepMockHelper =
      new StepMockHelper<>( "SYSLOG_MESSAGE TEST", SyslogMessageMeta.class,
        SyslogMessageData.class );
    when( stepMockHelper.logChannelInterfaceFactory.create( any(), any( LoggingObjectInterface.class ) ) ).thenReturn(
        stepMockHelper.logChannelInterface );

  }

  @After
  public void cleanUp() {
    stepMockHelper.cleanUp();
  }

  @Test
  public void testDispose() {
    SyslogMessageData data = new SyslogMessageData();
    SyslogIF syslog = mock( SyslogIF.class );
    SyslogConfigIF syslogConfigIF = mock( SyslogConfigIF.class, RETURNS_MOCKS );
    when( syslog.getConfig() ).thenReturn( syslogConfigIF );
    final Boolean[] initialized = new Boolean[] { Boolean.FALSE };
    doAnswer( (Answer<Object>) invocation -> {
      initialized[ 0 ] = true;
      return initialized;
    } ).when( syslog ).initialize( anyString(), any() );
    doAnswer( (Answer<Object>) invocation -> {
      if ( !initialized[0] ) {
        throw new NullPointerException( "this.socket is null" );
      } else {
        initialized[0] = false;
      }
      return initialized;
    } ).when( syslog ).shutdown();
    SyslogMessageMeta meta = new SyslogMessageMeta();
    SyslogMessage syslogMessage =
        new SyslogMessage( stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta,
            stepMockHelper.trans );
    SyslogMessage sysLogMessageSpy = spy( syslogMessage );
    when( sysLogMessageSpy.getSyslog() ).thenReturn( syslog );
    meta.setServerName( "1" );
    meta.setMessageFieldName( "1" );
    sysLogMessageSpy.init( meta, data );
    sysLogMessageSpy.dispose( meta, data );
  }
}
