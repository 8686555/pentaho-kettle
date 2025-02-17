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

import java.util.List;

import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransTestFactory;
import org.pentaho.di.trans.TransformationIT;

/**
 * Test class for the RegexEval step.
 *
 * Needs a lot more cases.
 *
 * @author Sven Boden
 * @author Daniel Einspanjer
 * @since 05-05-2008
 */
public class RegexEvalIT extends TransformationIT {
  public RegexEvalIT() throws KettleException {
    super();
  }

  public RowMetaInterface createSourceRowMetaInterface() {
    return createRowMetaInterface( new ValueMetaString( "field1" ) );
  }

  public RowMetaInterface createResultRowMetaInterface1() {
    RowMetaInterface rm = createSourceRowMetaInterface();
    rm.addValueMeta( new ValueMetaBoolean( "res" ) );
    return rm;
  }

  public RowMetaInterface createResultRowMetaInterface2() {
    RowMetaInterface rm = createResultRowMetaInterface1();
    rm.addValueMeta( new ValueMetaInteger( "cap" ) );
    return rm;
  }

  public RowMetaInterface createResultRowMetaInterface3() {
    return createRowMetaInterface(
      new ValueMetaString( "field1" ),
      new ValueMetaBoolean( "res" ),
      new ValueMetaString( "cap" ),
      new ValueMetaString( "capIfNull" ),
      new ValueMetaString( "capNullIf" ),
      new ValueMetaInteger( "capIfNullNullIf" ) );
  }

  public List<RowMetaAndData> createSourceData() {
    return createData( createSourceRowMetaInterface(), new Object[][] {
      new Object[] { "abc" }, new Object[] { "ABC" }, new Object[] { "123" }, new Object[] { "abc" } } );
  }

  public List<RowMetaAndData> createResultData1() {
    return createData( createResultRowMetaInterface1(), new Object[][] {
      new Object[] { "abc", Boolean.valueOf( true ) }, new Object[] { "ABC", Boolean.valueOf( false ) },
      new Object[] { "123", Boolean.valueOf( false ) }, new Object[] { "abc", Boolean.valueOf( true ) } } );
  }

  public List<RowMetaAndData> createResultData2() {
    return createData( createResultRowMetaInterface2(), new Object[][] {
      new Object[] { "abc", Boolean.valueOf( false ) }, new Object[] { "ABC", Boolean.valueOf( false ) },
      new Object[] { "123", Boolean.valueOf( true ), Long.valueOf( 2 ) },
      new Object[] { "abc", Boolean.valueOf( false ) } } );
  }

  public List<RowMetaAndData> createResultData3() {
    return createData( createResultRowMetaInterface3(), new Object[][] {
      // ((a)|([A1]))([B2]?).*
      new Object[] { "abc", Boolean.valueOf( true ), "a", "a", null, Long.valueOf( 0 ) },
      new Object[] { "ABC", Boolean.valueOf( true ), "A", "x", "A", Long.valueOf( 0 ) },
      new Object[] { "123", Boolean.valueOf( true ), "1", "x", null, Long.valueOf( 2 ) },
      new Object[] { "abc", Boolean.valueOf( true ), "a", "a", null, Long.valueOf( 0 ) } } );
  }

  public void testRegexEval1() throws Exception {
    String regexStepName = "regexeval";
    RegexEvalMeta regexEvalMeta = new RegexEvalMeta();

    regexEvalMeta.setScript( "[abc]*" );
    regexEvalMeta.setMatcher( "field1" );
    regexEvalMeta.setResultFieldName( "res" );

    TransMeta transMeta =
      TransTestFactory.generateTestTransformation( new Variables(), regexEvalMeta, regexStepName );

    // Now execute the transformation and get the result from the dummy step.
    //
    List<RowMetaAndData> result =
      TransTestFactory.executeTestTransformation(
        transMeta, TransTestFactory.INJECTOR_STEPNAME, regexStepName, TransTestFactory.DUMMY_STEPNAME,
        createSourceData() );

    checkRows( createResultData1(), result );
  }

  public void testRegexEval2() throws Exception {
    String regexStepName = "regexeval";
    RegexEvalMeta regexEvalMeta = new RegexEvalMeta();

    regexEvalMeta.setScript( "\\d(\\d)\\d" );
    regexEvalMeta.setMatcher( "field1" );
    regexEvalMeta.setResultFieldName( "res" );
    regexEvalMeta.setAllowCaptureGroupsFlag( true );
    regexEvalMeta.allocate( 1 );
    //CHECKSTYLE:Indentation:OFF
    regexEvalMeta.getFieldName()[0] = "cap";
    regexEvalMeta.getFieldType()[0] = ValueMetaInterface.TYPE_INTEGER;

    TransMeta transMeta =
      TransTestFactory.generateTestTransformation( new Variables(), regexEvalMeta, regexStepName );

    // Now execute the transformation and get the result from the dummy step.
    //
    List<RowMetaAndData> result =
      TransTestFactory.executeTestTransformation(
        transMeta, TransTestFactory.INJECTOR_STEPNAME, regexStepName, TransTestFactory.DUMMY_STEPNAME,
        createSourceData() );

    checkRows( createResultData2(), result );
  }

  public void testRegexEval3() throws Exception {
    String regexStepName = "regexeval";
    RegexEvalMeta regexEvalMeta = new RegexEvalMeta();

    regexEvalMeta.setScript( "((a)|([A1]))([B2]?).*" );
    regexEvalMeta.setMatcher( "field1" );
    regexEvalMeta.setResultFieldName( "res" );
    regexEvalMeta.setAllowCaptureGroupsFlag( true );

    regexEvalMeta.allocate( 4 );

    regexEvalMeta.getFieldName()[0] = "cap";
    regexEvalMeta.getFieldType()[0] = ValueMetaInterface.TYPE_STRING;

    regexEvalMeta.getFieldName()[1] = "capIfNull";
    regexEvalMeta.getFieldType()[1] = ValueMetaInterface.TYPE_STRING;
    regexEvalMeta.getFieldIfNull()[1] = "x";

    regexEvalMeta.getFieldName()[2] = "capNullIf";
    regexEvalMeta.getFieldType()[2] = ValueMetaInterface.TYPE_STRING;
    regexEvalMeta.getFieldNullIf()[2] = "1";

    regexEvalMeta.getFieldName()[3] = "capIfNullNullIf";
    regexEvalMeta.getFieldType()[3] = ValueMetaInterface.TYPE_INTEGER;
    regexEvalMeta.getFieldIfNull()[3] = "0";
    regexEvalMeta.getFieldNullIf()[3] = "B";

    TransMeta transMeta =
      TransTestFactory.generateTestTransformation( new Variables(), regexEvalMeta, regexStepName );

    // Now execute the transformation and get the result from the dummy step.
    //
    List<RowMetaAndData> result =
      TransTestFactory.executeTestTransformation(
        transMeta, TransTestFactory.INJECTOR_STEPNAME, regexStepName, TransTestFactory.DUMMY_STEPNAME,
        createSourceData() );

    checkRows( createResultData3(), result );
  }
}
