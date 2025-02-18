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


package org.pentaho.di.trans.steps.javafilter;

import java.util.List;

import org.codehaus.janino.ExpressionEvaluator;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * @author Matt
 * @since 8-sep-2005
 *
 */
public class JavaFilterData extends BaseStepData implements StepDataInterface {
  public static final int RETURN_TYPE_STRING = 0;
  public static final int RETURN_TYPE_NUMBER = 1;
  public static final int RETURN_TYPE_INTEGER = 2;
  public static final int RETURN_TYPE_LONG = 3;
  public static final int RETURN_TYPE_DATE = 4;
  public static final int RETURN_TYPE_BIGDECIMAL = 5;
  public static final int RETURN_TYPE_BYTE_ARRAY = 6;
  public static final int RETURN_TYPE_BOOLEAN = 7;

  public RowMetaInterface outputRowMeta;
  public int[] returnType;
  public int[] replaceIndex;

  public ExpressionEvaluator expressionEvaluator;
  public List<Integer> argumentIndexes;
  public String trueStepname;
  public String falseStepname;
  public boolean chosesTargetSteps;
  public RowSet trueRowSet;
  public RowSet falseRowSet;
  public Object[] argumentData;

  public JavaFilterData() {
    super();
  }

}
