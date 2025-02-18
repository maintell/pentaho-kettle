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


package org.pentaho.di.trans.steps.pentahoreporting;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.CompoundResource;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.StrictStubs.class )
public class PentahoReportingOutputTest {

  private static final String QUERY_NAME = "LocalFileQueryName";
  private URL testResourceUrl;
  private ResourceKey resourceKey;

  @Before
  public void setUp() throws ResourceException {

    testResourceUrl = this.getClass().getResource( "relative-path.prpt" );
    resourceKey =
      new ResourceKey( "org.pentaho.reporting.libraries.resourceloader.loader.URLResourceLoader",
        testResourceUrl, null );

    DataFactory mockedDataFactory = mock( DataFactory.class );
    when( mockedDataFactory.getQueryNames() ).thenReturn( new String[] { QUERY_NAME } );

    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    MasterReport mockedMasterReport = mock( MasterReport.class );
    when( mockedMasterReport.getDataFactory() ).thenReturn( mockedDataFactory );
    when( mockedMasterReport.getResourceManager() ).thenReturn( manager );


    Resource resource = mock( CompoundResource.class );
    when( resource.getResource() ).thenReturn( mockedMasterReport );
    when( resource.getDependencies() ).thenReturn( new ResourceKey[] {} );
    when( resource.getSource() ).thenReturn( resourceKey );
    when( resource.getTargetType() ).thenReturn( MasterReport.class );
    manager.getFactoryCache().put( resource );
  }

  @Test
  public void testLoadLocalReport() throws Exception {

    MasterReport report = PentahoReportingOutput.loadMasterReport( testResourceUrl.getPath() );

    URL returnedUrl = report.getResourceManager().toURL( resourceKey );

    assertTrue( returnedUrl.equals( testResourceUrl ) );
    assertTrue( QUERY_NAME.equals( report.getDataFactory().getQueryNames()[ 0 ] ) );

  }

  @Test
  public void testLocalFile() throws KettleFileException, MalformedURLException {
    Object keyValue = PentahoReportingOutput.getKeyValue(
      PentahoReportingOutput.getFileObject( testResourceUrl.getPath(), null ) );

    assertTrue( keyValue instanceof URL );

  }

  @Test( expected = KettleException.class )
  public void testProcessRowWitUsingValuesFromFields() throws KettleException {
    PentahoReportingOutput pentahoReportingOutput = mock( PentahoReportingOutput.class );
    PentahoReportingOutputMeta meta = mock( PentahoReportingOutputMeta.class );
    PentahoReportingOutputData data = mock( PentahoReportingOutputData.class );
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    LogChannelInterface log = mock( LogChannelInterface.class );

    when( pentahoReportingOutput.getRow() ).thenReturn( new Object[] { "Value1", "value2" } );
    when( pentahoReportingOutput.processRow( meta, data ) ).thenCallRealMethod();
    when( meta.getUseValuesFromFields() ).thenReturn( true );
    when( pentahoReportingOutput.getInputRowMeta() ).thenReturn( rowMetaInterface );
    when( meta.getInputFileField() ).thenReturn( "field" );
    when( rowMetaInterface.indexOfValue( "field" ) ).thenReturn( -1 );
    ReflectionTestUtils.setField( pentahoReportingOutput, "first", true );
    ReflectionTestUtils.setField( pentahoReportingOutput, "log", log );

    pentahoReportingOutput.processRow( meta, data );
  }

  @Test
  public void testProcessRowWithoutUsingValuesFromFields() throws KettleException {
    // Static mock to avoid PentahoReportingOutput.performPentahoReportingBoot(), so that
    // we don't need to mock DefaultResourceManagerBackend
    try ( MockedStatic<PentahoReportingOutput> mocked = mockStatic( PentahoReportingOutput.class ) ) {
      String inputFileString = "inputFile";
      String outputFileString = "outputFile";
      PentahoReportingOutput pentahoReportingOutput = mock( PentahoReportingOutput.class );
      PentahoReportingOutputMeta meta = mock( PentahoReportingOutputMeta.class );
      PentahoReportingOutputData data = mock( PentahoReportingOutputData.class );
      LogChannelInterface log = mock( LogChannelInterface.class );

      when( pentahoReportingOutput.getRow() ).thenReturn( new Object[] { "Value1", "value2" } );
      when( pentahoReportingOutput.processRow( meta, data ) ).thenCallRealMethod();
      when( meta.getUseValuesFromFields() ).thenReturn( false );
      when( meta.getInputFile() ).thenReturn( inputFileString);
      when( meta.getOutputFile() ).thenReturn( outputFileString );

      ReflectionTestUtils.setField( pentahoReportingOutput, "first", true );
      ReflectionTestUtils.setField( pentahoReportingOutput, "log", log );

      pentahoReportingOutput.processRow( meta, data );

      verify( pentahoReportingOutput, times( 1 ) )
        .processReport( any(), eq( inputFileString ), eq( outputFileString ), any(), any() );
    }
  }

}
