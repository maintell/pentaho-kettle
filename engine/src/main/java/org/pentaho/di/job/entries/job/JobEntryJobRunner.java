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

import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPointHandler;
import org.pentaho.di.core.extension.KettleExtensionPoint;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.Job;

/**
 * @author Matt
 * @since 6-apr-2005
 */
public class JobEntryJobRunner implements Runnable {
  private static Class<?> PKG = Job.class; // for i18n purposes, needed by Translator2!!

  private Job job;
  private Result result;
  private LogChannelInterface log;
  private int entryNr;
  private boolean finished;

  /**
   *
   */
  public JobEntryJobRunner( Job job, Result result, int entryNr, LogChannelInterface log ) {
    this.job = job;
    this.result = result;
    this.log = log;
    this.entryNr = entryNr;
    finished = false;
  }

  public void run() {
    try {
      if ( job.isStopped() || ( job.getParentJob() != null && job.getParentJob().isStopped() ) ) {
        return;
      }

      // This JobEntryRunner is a replacement for the Job thread.
      // The job thread is never started because we simply want to wait for the result.
      //
      ExtensionPointHandler.callExtensionPoint( log, KettleExtensionPoint.JobStart.id, getJob() );

      job.fireJobStartListeners(); // Fire the start listeners
      result = job.execute( entryNr + 1, result );
    } catch ( KettleException e ) {
      e.printStackTrace();
      log.logError( "An error occurred executing this job entry : ", e );
      result.setResult( false );
      result.setNrErrors( 1 );
    } finally {
      //[PDI-14981] otherwise will get null pointer exception if 'job finished' listeners will be using it
      job.setResult( result );
      try {
        ExtensionPointHandler.callExtensionPoint( log, KettleExtensionPoint.JobFinish.id, getJob() );
        job.getJobMeta().disposeEmbeddedMetastoreProvider();
        log.logDebug( BaseMessages.getString( PKG, "Job.Log.DisposeEmbeddedMetastore" ) );
        job.fireJobFinishListeners();

        //catch more general exception to prevent thread hanging
      } catch ( Exception e ) {
        result.setNrErrors( 1 );
        result.setResult( false );
        log.logError( BaseMessages.getString( PKG, "Job.Log.ErrorExecJob", e.getMessage() ), e );
      }
      job.setFinished( true );
    }
    finished = true;
  }

  /**
   * @param result The result to set.
   */
  public void setResult( Result result ) {
    this.result = result;
  }

  /**
   * @return Returns the result.
   */
  public Result getResult() {
    return result;
  }

  /**
   * @return Returns the log.
   */
  public LogChannelInterface getLog() {
    return log;
  }

  /**
   * @param log The log to set.
   */
  public void setLog( LogChannelInterface log ) {
    this.log = log;
  }

  /**
   * @return Returns the job.
   */
  public Job getJob() {
    return job;
  }

  /**
   * @param job The job to set.
   */
  public void setJob( Job job ) {
    this.job = job;
  }

  /**
   * @return Returns the entryNr.
   */
  public int getEntryNr() {
    return entryNr;
  }

  /**
   * @param entryNr The entryNr to set.
   */
  public void setEntryNr( int entryNr ) {
    this.entryNr = entryNr;
  }

  /**
   * @return Returns the finished.
   */
  public boolean isFinished() {
    return finished;
  }

  public void waitUntilFinished() {
    while ( !isFinished() && !job.isStopped() ) {
      try {
        Thread.sleep( 0, 1 );
      } catch ( InterruptedException e ) {
        // Ignore errors
      }
    }
  }
}
