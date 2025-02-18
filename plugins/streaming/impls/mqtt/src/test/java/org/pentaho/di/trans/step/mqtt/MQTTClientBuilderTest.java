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


package org.pentaho.di.trans.step.mqtt;

import com.google.common.collect.ImmutableMap;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;

import java.util.Collections;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith ( MockitoJUnitRunner.class )
public class MQTTClientBuilderTest {

  @Mock LogChannelInterface logger;
  @Mock MqttCallback callback;
  @Mock MQTTClientBuilder.ClientFactory factory;
  @Mock MqttClient client;
  @Mock StepInterface step;
  @Mock StepMeta meta;

  @Captor ArgumentCaptor<MqttConnectOptions> connectOptsCapture;
  @Captor ArgumentCaptor<String> clientIdCapture;

  VariableSpace space = new Variables();
  MQTTClientBuilder builder = MQTTClientBuilder.builder();

  @Before
  public void before() throws MqttException {
    when( factory.getClient( any(), clientIdCapture.capture(), any() ) )
      .thenReturn( client );
    when( step.getLogChannel() ).thenReturn( logger );
    when( step.getStepMeta() ).thenReturn( meta );
    when( step.getStepMeta().getName() ).thenReturn( "Step Name" );
    builder
      .withBroker( "127.0.0.1:101010" )
      .withStep( step );
    builder.clientFactory = factory;
  }

  @Test
  public void testValidBuilderParams() throws MqttException {
    MqttClient client = builder
      .withQos( "2" )
      .withUsername( "user" )
      .withPassword( "pass" )
      .withIsSecure( true )
      .withTopics( Collections.singletonList( "SomeTopic" ) )
      .withSslConfig( ImmutableMap.of( "ssl.trustStore", "/some/path" ) )
      .withCallback( callback )
      .withKeepAliveInterval( "1000" )
      .withMaxInflight( "2000" )
      .withConnectionTimeout( "3000" )
      .withCleanSession( "true" )
      .withStorageLevel( "/Users/NoName/Temp" )
      .withServerUris( "127.0.0.1:3000" )
      .withMqttVersion( "3" )
      .withAutomaticReconnect( "false" )
      .buildAndConnect();
    verify( client ).setCallback( callback );
    verify( factory ).getClient( anyString(), anyString(), any( MqttDefaultFilePersistence.class ) );
    verify( client ).connect( connectOptsCapture.capture() );

    MqttConnectOptions opts = connectOptsCapture.getValue();
    assertThat( opts.getUserName(), equalTo( "user" ) );
    assertThat( opts.getPassword(), equalTo( "pass".toCharArray() ) );
    Properties props = opts.getSSLProperties();
    assertThat( props.size(), equalTo( 1 ) );
    assertThat( props.getProperty( "com.ibm.ssl.trustStore" ), equalTo( "/some/path" ) );
    assertEquals( 1000, opts.getKeepAliveInterval() );
    assertEquals( 2000, opts.getMaxInflight() );
    assertEquals( 3000, opts.getConnectionTimeout() );
    assertEquals( true, opts.isCleanSession() );
    assertArrayEquals( new String[] { "ssl://127.0.0.1:3000" }, opts.getServerURIs() );
    assertEquals( 3, opts.getMqttVersion() );
    assertEquals( false, opts.isAutomaticReconnect() );
  }

  @Test
  public void testFailParsingQOSLevelNotInteger() {
    try {
      builder
        .withQos( "hello" )
        .buildAndConnect();
      fail( "Should of failed initialization." );
    } catch ( Exception e ) {
      // Initialization failed because QOS level isn't 0, 1 or 2
      assertTrue( e instanceof IllegalArgumentException );
    }
  }

  @Test
  public void testFailParsingQOSLevelNotInRange() {
    try {
      builder
        .withQos( "72" )
        .buildAndConnect();
      fail( "Should of failed initialization." );
    } catch ( Exception e ) {
      // Initialization failed because QOS level isn't 0, 1 or 2
      assertTrue( e instanceof IllegalArgumentException );
    }
  }

  @Test
  public void testEmptyClientId() throws MqttException {
    builder
      .withClientId( "asdf" )
      .buildAndConnect();
    assertEquals( "asdf", clientIdCapture.getValue() );

    builder
      .withClientId( null )
      .buildAndConnect();
    assertFalse( StringUtil.isEmpty( clientIdCapture.getValue() ) );
    assertNotEquals( "asdf", clientIdCapture.getValue() );

    builder
      .withClientId( "" )
      .buildAndConnect();
    assertFalse( StringUtil.isEmpty( clientIdCapture.getValue() ) );
    assertNotEquals( "asdf", clientIdCapture.getValue() );
  }

}
