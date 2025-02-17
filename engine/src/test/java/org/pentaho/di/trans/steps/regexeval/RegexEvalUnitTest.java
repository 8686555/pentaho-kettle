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


package org.pentaho.di.trans.steps.regexeval;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;

public class RegexEvalUnitTest {
  private StepMockHelper<RegexEvalMeta, RegexEvalData> stepMockHelper;

  @Before
  public void setup() {
    stepMockHelper =
      new StepMockHelper<>(
        "REGEX EVAL TEST", RegexEvalMeta.class, RegexEvalData.class );
    when( stepMockHelper.logChannelInterfaceFactory.create( any(), any( LoggingObjectInterface.class ) ) )
      .thenReturn( stepMockHelper.logChannelInterface );
    when( stepMockHelper.trans.isRunning() ).thenReturn( true );
  }

  @After
  public void tearDown() {
    stepMockHelper.cleanUp();
  }

  @Test
  public void testOutputIsMuchBiggerThanInputDoesntThrowArrayIndexOutOfBounds() throws KettleException {
    RegexEval regexEval =
      new RegexEval(
        stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta,
        stepMockHelper.trans );
    when( stepMockHelper.processRowsStepMetaInterface.isAllowCaptureGroupsFlagSet() ).thenReturn( true );
    String[] outFields = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k" };
    when( stepMockHelper.processRowsStepMetaInterface.getFieldName() ).thenReturn( outFields );
    when( stepMockHelper.processRowsStepMetaInterface.getMatcher() ).thenReturn( "\\.+" );
    stepMockHelper.processRowsStepDataInterface.pattern = Pattern.compile( "(a)(b)(c)(d)(e)(f)(g)(h)(i)(j)(k)" );
    Object[] inputRow = new Object[] {};
    RowSet inputRowSet = stepMockHelper.getMockInputRowSet( inputRow );
    RowMetaInterface mockInputRowMeta = mock( RowMetaInterface.class );
    RowMetaInterface mockOutputRoMeta = mock( RowMetaInterface.class );
    when( mockOutputRoMeta.size() ).thenReturn( outFields.length );
    when( mockInputRowMeta.size() ).thenReturn( 0 );
    when( inputRowSet.getRowMeta() ).thenReturn( mockInputRowMeta );
    when( mockInputRowMeta.clone() ).thenReturn( mockOutputRoMeta );
    when( mockInputRowMeta.isNull( any( Object[].class ), anyInt() ) ).thenReturn( true );
    regexEval.addRowSetToInputRowSets( inputRowSet );

    regexEval.init( stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface );
    regexEval
      .processRow( stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface );
  }
}
