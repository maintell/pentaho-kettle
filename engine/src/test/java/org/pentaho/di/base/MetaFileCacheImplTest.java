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

package org.pentaho.di.base;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.TransMeta;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith( MockitoJUnitRunner.class )

public class MetaFileCacheImplTest {
  private static final String TRANS_KEY = "transKey";
  private static final String JOB_KEY = "jobKey";
  private static final String TRANS_NAME = "transName";
  private static final String JOB_NAME = "jobName";

  @Mock private LogChannelInterface logger;
  private final TransMeta transMeta = new TransMeta();
  private final JobMeta jobMeta = new JobMeta();

  private MetaFileCacheImpl metaFileCacheImpl;

  @Before
  public void setup() {
    transMeta.setName( TRANS_NAME );
    jobMeta.setName( JOB_NAME );
    metaFileCacheImpl = new MetaFileCacheImpl( logger );
  }

  @Test
  public void cacheMetaAndGetCacheTest() {
    metaFileCacheImpl.cacheMeta( TRANS_KEY, transMeta );
    metaFileCacheImpl.cacheMeta( JOB_KEY, jobMeta );
    assertEquals( jobMeta, metaFileCacheImpl.getCachedJobMeta( JOB_KEY ) );
    assertEquals( transMeta, metaFileCacheImpl.getCachedTransMeta( TRANS_KEY ) );

    metaFileCacheImpl.getCachedJobMeta( JOB_KEY );
    MetaFileCacheImpl.MetaFileCacheEntry entry = metaFileCacheImpl.cacheMap.get( JOB_KEY + ".kjb" );
    assertEquals( 2, entry.getTimesUsed() );
  }

  @Test
  public void logCacheSummaryTest() {
    cacheMetaAndGetCacheTest();
    metaFileCacheImpl.logCacheSummary( logger );
    verify( logger, times( 3 ) ).logDetailed( anyString() );
  }
}
