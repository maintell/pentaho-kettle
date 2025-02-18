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


package org.pentaho.di.repository.kdr.delegates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;

/**
 */
public class KettleDatabaseRepositoryConnectionDelegateUnitTest {
  private DatabaseMeta databaseMeta;
  private KettleDatabaseRepository repository;
  private Database database;
  private KettleDatabaseRepositoryConnectionDelegate kettleDatabaseRepositoryConnectionDelegate;

  @Before
  public void setup() {
    repository = mock( KettleDatabaseRepository.class );
    databaseMeta = mock( DatabaseMeta.class );
    database = mock( Database.class );
    kettleDatabaseRepositoryConnectionDelegate =
      new KettleDatabaseRepositoryConnectionDelegate( repository, databaseMeta );
    kettleDatabaseRepositoryConnectionDelegate.database = database;
  }

  @Test
  public void createIdsWithsValueQuery() {
    final String table = "table";
    final String id = "id";
    final String lookup = "lookup";
    final String expectedTemplate = String.format( "select %s from %s where %s in ", id, table, lookup ) + "(%s)";

    assertTrue( String.format( expectedTemplate, "?" ).equalsIgnoreCase(
        KettleDatabaseRepositoryConnectionDelegate.createIdsWithValuesQuery( table, id, lookup, 1 ) ) );
    assertTrue(
        String.format( expectedTemplate, "?,?" ).equalsIgnoreCase(
          KettleDatabaseRepositoryConnectionDelegate.createIdsWithValuesQuery( table, id, lookup, 2 ) ) );
  }

  @Test
  public void testGetValueToIdMap() throws KettleException {
    String tablename = "test-tablename";
    String idfield = "test-idfield";
    String lookupfield = "test-lookupfield";
    List<Object[]> rows = new ArrayList<>();
    int id = 1234;
    LongObjectId longObjectId = new LongObjectId( id );
    rows.add( new Object[] { lookupfield, id } );
    when( database.getRows( eq( "SELECT " + lookupfield + ", " + idfield + " FROM " + tablename ), any(
        RowMetaInterface.class ),
      eq( new Object[] {} ), eq( ResultSet.FETCH_FORWARD ),
      eq( false ), eq( -1 ), eq( null ) ) ).thenReturn( rows );
    Map<String, LongObjectId> valueToIdMap =
      kettleDatabaseRepositoryConnectionDelegate.getValueToIdMap( tablename, idfield, lookupfield );
    assertEquals( 1, valueToIdMap.size() );
    assertEquals( longObjectId, valueToIdMap.get( lookupfield ) );
  }
}
