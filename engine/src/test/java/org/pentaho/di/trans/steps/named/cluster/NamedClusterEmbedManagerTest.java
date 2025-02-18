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

package org.pentaho.di.trans.steps.named.cluster;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.osgi.api.NamedClusterOsgi;
import org.pentaho.di.core.osgi.api.NamedClusterServiceOsgi;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by tkafalas on 7/14/2017.
 */
public class NamedClusterEmbedManagerTest {
  private final String CLUSTER1_NAME = "cluster1_name";
  private final String CLUSTER2_NAME = "cluster2_name";
  private AbstractMeta mockMeta;
  private NamedClusterEmbedManager namedClusterEmbedManager;
  private NamedClusterOsgi mockNamedCluster1;
  private NamedClusterOsgi mockNamedCluster2;
  private MetaStoreFactory mockMetaStoreFactory;
  NamedClusterServiceOsgi mockNamedClusterService;

  @Before
  public void setUp() {
    LogChannelInterface mockLog = mock( LogChannelInterface.class );
    mockMeta = mock( AbstractMeta.class );
    mockMetaStoreFactory = mock( MetaStoreFactory.class );
    NamedClusterEmbedManager.testMetaStoreFactory = mockMetaStoreFactory;
    IMetaStore mockMetaStore = mock( IMetaStore.class );
    mockNamedCluster1 = mock( NamedClusterOsgi.class );
    mockNamedCluster2 = mock( NamedClusterOsgi.class );
    when( mockNamedCluster1.getName() ).thenReturn( CLUSTER1_NAME );
    when( mockNamedCluster2.getName() ).thenReturn( CLUSTER2_NAME );
    mockNamedClusterService = mock( NamedClusterServiceOsgi.class );
    when( mockMeta.getNamedClusterServiceOsgi() ).thenReturn( mockNamedClusterService );
    when( mockMeta.getMetaStore() ).thenReturn( mockMetaStore );
    when( mockNamedClusterService.getClusterTemplate() ).thenReturn( mock( NamedClusterOsgi.class ) );
    when( mockNamedClusterService.getNamedClusterByName( eq( CLUSTER1_NAME ), any() ) ).thenReturn( mockNamedCluster1 );
    when( mockNamedClusterService.getNamedClusterByName( eq( CLUSTER2_NAME ), any() ) ).thenReturn( mockNamedCluster2 );

    namedClusterEmbedManager = new NamedClusterEmbedManager( mockMeta, mockLog );
  }

  @Test
  public void testRegisterUrlNc() throws Exception {
    namedClusterEmbedManager.registerUrl( "hc://" + CLUSTER1_NAME + "/dir1/dir2" );
    verify( mockMetaStoreFactory ).saveElement( mockNamedCluster1 );
  }

  @Test
  public void testRegisterUrlNotNc() throws Exception {
    namedClusterEmbedManager.registerUrl( "hdfs://" + CLUSTER1_NAME + "/dir1/dir2" );
    verify( mockMetaStoreFactory, never() ).saveElement( any() );
  }

  @Test
  public void testRegisterUrlRegularFile() throws Exception {
    namedClusterEmbedManager.registerUrl( "/" + CLUSTER1_NAME + "/dir1/dir2" );
    verify( mockMetaStoreFactory, never() ).saveElement( any() );
  }

  @Test
  public void testRegisterUrlFullVariable() throws Exception {
    when( mockNamedClusterService.listNames( mockMeta.getMetaStore() ) )
      .thenReturn( Arrays.asList( CLUSTER1_NAME, CLUSTER2_NAME ) );

    namedClusterEmbedManager.registerUrl( "${variable)" );
    verify( mockMetaStoreFactory ).saveElement( mockNamedCluster1 );
    verify( mockMetaStoreFactory ).saveElement( mockNamedCluster2 );
  }


  @Test
  public void testRegisterUrlClusterVariable() throws Exception {
    when( mockNamedClusterService.listNames( mockMeta.getMetaStore() ) )
      .thenReturn( Arrays.asList( CLUSTER1_NAME, CLUSTER2_NAME ) );

    namedClusterEmbedManager.registerUrl( "hc://${variable)/dir1/file" );
    verify( mockMetaStoreFactory ).saveElement( mockNamedCluster1 );
    verify( mockMetaStoreFactory ).saveElement( mockNamedCluster2 );
  }

  @Test
  public void testRegisterUrlAlreadyRegistered() throws Exception {
    when( mockMetaStoreFactory.loadElement( CLUSTER1_NAME ) ).thenReturn( mockNamedCluster1 );
    namedClusterEmbedManager.registerUrl( "hc://" + CLUSTER1_NAME + "/dir1/dir2" );
    verify( mockMetaStoreFactory, times( 0 ) ).saveElement( mockNamedCluster1 );
  }

  @Test
  public void testClear() throws Exception {
    when( mockMetaStoreFactory.getElements() )
      .thenReturn( Arrays.asList( mockNamedCluster1, mockNamedCluster2 ) );

    namedClusterEmbedManager.clear( );
    verify( mockMetaStoreFactory ).deleteElement( CLUSTER1_NAME );
    verify( mockMetaStoreFactory ).deleteElement( CLUSTER2_NAME );
  }

  @Test
  public void testPassEmbeddedMetastoreKey() {
    Variables mockVariables = mock( Variables.class );
    namedClusterEmbedManager.passEmbeddedMetastoreKey( mockVariables, "key" );
    verify( mockVariables ).setVariable( anyString(), anyString() );
  }

  @Test
  public void testAddingClusterToMetaData() throws MetaStoreException {
    namedClusterEmbedManager.addClusterToMeta( CLUSTER1_NAME );
    verify( mockMetaStoreFactory ).saveElement( mockNamedCluster1 );
  }
}
