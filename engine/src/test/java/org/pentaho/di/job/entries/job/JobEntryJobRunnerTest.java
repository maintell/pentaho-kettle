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


package org.pentaho.di.job.entries.job;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobEntryJobRunnerTest {

  private JobEntryJobRunner jobRunner;
  private Job mockJob;
  private JobMeta mockJobMeta;
  private Result mockResult;
  private LogChannelInterface mockLog;
  private Job parentJob;

  @Before
  public void setUp() throws Exception {
    mockJob = mock( Job.class );
    mockJobMeta = mock( JobMeta.class );
    mockResult = mock( Result.class );
    mockLog = mock( LogChannelInterface.class );
    jobRunner = new JobEntryJobRunner( mockJob, mockResult, 0, mockLog );
    parentJob = mock( Job.class );
  }

  @Test
  public void testRun() throws Exception {
    // Call all the NO-OP paths
    when( mockJob.isStopped() ).thenReturn( true );
    jobRunner.run();
    when( mockJob.isStopped() ).thenReturn( false );
    when( mockJob.getParentJob() ).thenReturn( null );
    when( mockJob.getJobMeta() ).thenReturn( mockJobMeta );
    jobRunner.run();
    when( parentJob.isStopped() ).thenReturn( true );
    when( mockJob.getParentJob() ).thenReturn( parentJob );
    jobRunner.run();
    when( parentJob.isStopped() ).thenReturn( false );
    when( mockJob.execute( Mockito.anyInt(), Mockito.any( Result.class ) ) ).thenReturn( mockResult );
    jobRunner.run();

  }

  @Test
  public void testRunSetsResult() throws Exception {
    when( mockJob.isStopped() ).thenReturn( false );
    when( mockJob.getParentJob() ).thenReturn( parentJob );
    when( parentJob.isStopped() ).thenReturn( false );
    when( mockJob.execute( Mockito.anyInt(), Mockito.any( Result.class ) ) ).thenReturn( mockResult );

    jobRunner.run();
    verify( mockJob, times( 1 ) ).setResult( Mockito.any( Result.class ) );
  }

  @Test
  public void testRunWithExceptionOnExecuteSetsResult() throws Exception {
    when( mockJob.isStopped() ).thenReturn( false );
    when( mockJob.getParentJob() ).thenReturn( parentJob );
    when( parentJob.isStopped() ).thenReturn( false );
    doThrow( KettleException.class ).when( mockJob ).execute( anyInt(), any( Result.class ) );

    jobRunner.run();
    verify( mockJob, times( 1 ) ).setResult( Mockito.any( Result.class ) );
  }

  @Test
  public void testRunWithExceptionOnFireJobSetsResult() throws KettleException {
    when( mockJob.isStopped() ).thenReturn( false );
    when( mockJob.getParentJob() ).thenReturn( parentJob );
    when( parentJob.isStopped() ).thenReturn( false );
    when( mockJob.execute( Mockito.anyInt(), Mockito.any( Result.class ) ) ).thenReturn( mockResult );

    doThrow( KettleException.class ).when( mockJob ).fireJobFinishListeners();

    jobRunner.run();
    verify( mockJob, times( 1 ) ).setResult( Mockito.any( Result.class ) );
    assertTrue( jobRunner.isFinished() );
  }

  @Test
  public void testRunWithExceptionOnExecuteAndFireJobSetsResult() throws KettleException {
    when( mockJob.isStopped() ).thenReturn( false );
    when( mockJob.getParentJob() ).thenReturn( parentJob );
    when( parentJob.isStopped() ).thenReturn( false );
    when( mockJob.execute( Mockito.anyInt(), Mockito.any( Result.class ) ) ).thenReturn( mockResult );

    doThrow( KettleException.class ).when( mockJob ).execute( anyInt(), any( Result.class ) );
    doThrow( KettleException.class ).when( mockJob ).fireJobFinishListeners();

    jobRunner.run();
    verify( mockJob, times( 1 ) ).setResult( Mockito.any( Result.class ) );
    assertTrue( jobRunner.isFinished() );
  }

  @Ignore( "Invalid test is testing if a mock can throw an exception!")
  @Test
  public void testRunWithException() throws Exception {
    when( mockJob.isStopped() ).thenReturn( false );
    when( mockJob.getParentJob() ).thenReturn( parentJob );
    when( mockJob.getJobMeta() ).thenReturn( mockJobMeta );
    when( parentJob.isStopped() ).thenReturn( false );
    doThrow( KettleException.class ).when( mockJob ).execute( anyInt(), any( Result.class ) );
    jobRunner.run();
    verify( mockResult, times( 1 ) ).setNrErrors( anyInt() );

    //[PDI-14981] catch more general exception to prevent thread hanging
    doThrow( KettleException.class ).when( mockJob ).fireJobFinishListeners();
    jobRunner.run();

  }

  @Test
  public void testGetSetResult() {
    assertEquals( mockResult, jobRunner.getResult() );
    jobRunner.setResult( null );
    assertNull( jobRunner.getResult() );
  }

  @Test
  public void testGetSetLog() {
    assertEquals( mockLog, jobRunner.getLog() );
    jobRunner.setLog( null );
    assertNull( jobRunner.getLog() );
  }

  @Test
  public void testGetSetJob() {
    assertEquals( mockJob, jobRunner.getJob() );
    jobRunner.setJob( null );
    assertNull( jobRunner.getJob() );
  }

  @Test
  public void testGetSetEntryNr() throws Exception {
    assertEquals( 0, jobRunner.getEntryNr() );
    jobRunner.setEntryNr( 1 );
    assertEquals( 1, jobRunner.getEntryNr() );
  }

  @Test
  public void testIsFinished() throws Exception {
    assertFalse( jobRunner.isFinished() );
    when( mockJob.isStopped() ).thenReturn( false );
    when( mockJob.getParentJob() ).thenReturn( parentJob );
    when( parentJob.isStopped() ).thenReturn( false );
    when( mockJob.execute( Mockito.anyInt(), Mockito.any( Result.class ) ) ).thenReturn( mockResult );
    jobRunner.run();
    assertTrue( jobRunner.isFinished() );
  }

  @Test
  public void testWaitUntilFinished() throws Exception {
    when( mockJob.isStopped() ).thenReturn( true );
    when( mockJob.getParentJob() ).thenReturn( parentJob );
    when( parentJob.isStopped() ).thenReturn( false );
    when( mockJob.execute( Mockito.anyInt(), Mockito.any( Result.class ) ) ).thenReturn( mockResult );
    jobRunner.waitUntilFinished();
  }
}
