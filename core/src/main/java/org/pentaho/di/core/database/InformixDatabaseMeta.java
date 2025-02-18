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


package org.pentaho.di.core.database;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMetaInterface;

/**
 * Contains Informix specific information through static final members
 *
 * @author Matt
 * @since 11-mrt-2005
 */

public class InformixDatabaseMeta extends BaseDatabaseMeta implements DatabaseInterface {
  @Override
  public int[] getAccessTypeList() {
    return new int[] {
      DatabaseMeta.TYPE_ACCESS_NATIVE, DatabaseMeta.TYPE_ACCESS_JNDI };
  }

  @Override
  public int getDefaultDatabasePort() {
    if ( getAccessType() == DatabaseMeta.TYPE_ACCESS_NATIVE ) {
      return 1526;
    }
    return -1;
  }

  /**
   * @see DatabaseInterface#getNotFoundTK(boolean)
   */
  @Override
  public int getNotFoundTK( boolean useAutoinc ) {
    if ( supportsAutoInc() && useAutoinc ) {
      return 1;
    }
    return super.getNotFoundTK( useAutoinc );
  }

  @Override
  public String getDriverClass() {
    return "com.informix.jdbc.IfxDriver";
  }

  @Override
  public String getURL( String hostname, String port, String databaseName ) {
    return "jdbc:informix-sqli://"
      + hostname + ":" + port + "/" + databaseName + ":INFORMIXSERVER=" + getServername() + ";DELIMIDENT=Y";
  }

  /**
   * Indicates the need to insert a placeholder (0) for auto increment fields.
   *
   * @return true if we need a placeholder for auto increment fields in insert statements.
   */
  @Override
  public boolean needsPlaceHolder() {
    return true;
  }

  @Override
  public boolean needsToLockAllTables() {
    return false;
  }

  @Override
  public String getSQLQueryFields( String tableName ) {
    return "SELECT FIRST 1 * FROM " + tableName;
  }

  @Override
  public String getSQLTableExists( String tablename ) {
    return getSQLQueryFields( tablename );
  }

  @Override
  public String getSQLColumnExists( String columnname, String tablename ) {
    return getSQLQueryColumnFields( columnname, tablename );
  }

  public String getSQLQueryColumnFields( String columnname, String tableName ) {
    return "SELECT FIRST 1 " + columnname + " FROM " + tableName;
  }

  /**
   * Generates the SQL statement to add a column to the specified table
   *
   * @param tablename
   *          The table to add
   * @param v
   *          The column defined as a value
   * @param tk
   *          the name of the technical key field
   * @param useAutoinc
   *          whether or not this field uses auto increment
   * @param pk
   *          the name of the primary key field
   * @param semicolon
   *          whether or not to add a semi-colon behind the statement.
   * @return the SQL statement to add a column to the specified table
   */
  @Override
  public String getAddColumnStatement( String tablename, ValueMetaInterface v, String tk, boolean useAutoinc,
    String pk, boolean semicolon ) {
    return "ALTER TABLE " + tablename + " ADD " + getFieldDefinition( v, tk, pk, useAutoinc, true, false );
  }

  /**
   * Generates the SQL statement to modify a column in the specified table
   *
   * @param tablename
   *          The table to add
   * @param v
   *          The column defined as a value
   * @param tk
   *          the name of the technical key field
   * @param useAutoinc
   *          whether or not this field uses auto increment
   * @param pk
   *          the name of the primary key field
   * @param semicolon
   *          whether or not to add a semi-colon behind the statement.
   * @return the SQL statement to modify a column in the specified table
   */
  @Override
  public String getModifyColumnStatement( String tablename, ValueMetaInterface v, String tk, boolean useAutoinc,
    String pk, boolean semicolon ) {
    return "ALTER TABLE " + tablename + " MODIFY " + getFieldDefinition( v, tk, pk, useAutoinc, true, false );
  }

  @Override
  public String getFieldDefinition( ValueMetaInterface v, String tk, String pk, boolean useAutoinc,
                                    boolean addFieldName, boolean addCr ) {
    String retval = "";

    String fieldname = v.getName();
    int length = v.getLength();
    int precision = v.getPrecision();

    if ( addFieldName ) {
      retval += fieldname + " ";
    }

    int type = v.getType();
    switch ( type ) {
      case ValueMetaInterface.TYPE_TIMESTAMP:
        retval += "DATETIME";
        break;
      case ValueMetaInterface.TYPE_DATE:
        retval += "DATETIME YEAR to FRACTION";
        break;
      case ValueMetaInterface.TYPE_BOOLEAN:
        if ( supportsBooleanDataType() ) {
          retval += "BOOLEAN";
        } else {
          retval += "CHAR(1)";
        }
        break;
      case ValueMetaInterface.TYPE_NUMBER:
      case ValueMetaInterface.TYPE_INTEGER:
      case ValueMetaInterface.TYPE_BIGNUMBER:
        if ( fieldname.equalsIgnoreCase( tk ) || // Technical key
          fieldname.equalsIgnoreCase( pk ) // Primary key
        ) {
          if ( useAutoinc ) {
            retval += "SERIAL8";
          } else {
            retval += "INTEGER PRIMARY KEY";
          }
        } else {
          if ( ( length < 0 && precision < 0 ) || precision > 0 || length > 9 ) {
            retval += "FLOAT";
          } else { // Precision == 0 && length<=9
            retval += "INTEGER";
          }
        }
        break;
      case ValueMetaInterface.TYPE_STRING:
        if ( length >= DatabaseMeta.CLOB_LENGTH ) {
          retval += "CLOB";
        } else {
          if ( length < 256 ) {
            retval += "VARCHAR";
            if ( length > 0 ) {
              retval += "(" + length + ")";
            }
          } else {
            if ( length < 32768 ) {
              retval += "LVARCHAR";
            } else {
              retval += "TEXT";
            }
          }
        }
        break;
      default:
        retval += " UNKNOWN";
        break;
    }

    if ( addCr ) {
      retval += Const.CR;
    }

    return retval;
  }

  @Override
  public String getSQLLockTables( String[] tableNames ) {
    String sql = "";
    for ( int i = 0; i < tableNames.length; i++ ) {
      sql += "LOCK TABLE " + tableNames[i] + " IN EXCLUSIVE MODE;" + Const.CR;
    }
    return sql;
  }

  @Override
  public String getSQLUnlockTables( String[] tableNames ) {
    return null;
    /*
     * String sql=""; for (int i=0;i<tableNames.length;i++) { sql+="UNLOCK TABLE "+tableNames[i]+";"+Const.CR; } return
     * sql;
     */
  }

  @Override
  public String[] getUsedLibraries() {
    return new String[] { "ifxjdbc.jar" };
  }

  /**
   * Get the SQL to insert a new empty unknown record in a dimension.
   *
   * @param schemaTable
   *          the schema-table name to insert into
   * @param keyField
   *          The key field
   * @param versionField
   *          the version field
   * @return the SQL to insert the unknown record into the SCD.
   */
  @Override
  public String getSQLInsertAutoIncUnknownDimensionRow( String schemaTable, String keyField, String versionField ) {
    return "insert into " + schemaTable + "(" + keyField + ", " + versionField + ") values (1, 1)";
  }

}
