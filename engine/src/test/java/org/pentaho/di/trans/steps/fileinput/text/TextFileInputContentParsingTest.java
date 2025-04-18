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


package org.pentaho.di.trans.steps.fileinput.text;

import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.file.BaseFileField;

public class TextFileInputContentParsingTest extends BaseTextParsingTest {
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

  @Test
  public void testDefaultOptions() throws Exception {

    initByFile( "default.csv" );

    setFields( new BaseFileField( "f1", -1, -1 ), new BaseFileField( "f2", -1, -1 ),
      new BaseFileField( "f2", -1, -1 ) );

    process();

    check( new Object[][] { { "first", "1", "1.1" }, { "second", "2", "2.2" }, { "third", "3", "3.3" } } );
  }

  @Test
  public void testSeparator() throws Exception {

    meta.content.separator = ",";
    initByFile( "separator.csv" );

    setFields( new BaseFileField( "f1", -1, -1 ), new BaseFileField( "f2", -1, -1 ),
      new BaseFileField( "f2", -1, -1 ) );

    process();

    check( new Object[][] { { "first", "1", "1.1" }, { "second", "2", "2.2" }, { "third;third", "3", "3.3" } } );
  }

  @Test
  public void testEscape() throws Exception {

    meta.content.escapeCharacter = "\\";
    initByFile( "escape.csv" );

    setFields( new BaseFileField( "f1", -1, -1 ), new BaseFileField( "f2", -1, -1 ),
      new BaseFileField( "f2", -1, -1 ) );

    process();

    check( new Object[][] { { "first", "1", "1.1" }, { "second", "2", "2.2" }, { "third;third", "3", "3.3" } } );
  }

  @Test
  public void testHeader() throws Exception {

    meta.content.header = false;
    initByFile( "default.csv" );

    setFields( new BaseFileField( "f1", -1, -1 ), new BaseFileField( "f2", -1, -1 ),
      new BaseFileField( "f2", -1, -1 ) );

    process();

    check( new Object[][] {
      { "Field 1", "Field 2", "Field 3" },
      { "first", "1", "1.1" },
      { "second", "2", "2.2" },
      { "third", "3", "3.3" } } );
  }

  @Test
  public void testGzipCompression() throws Exception {

    meta.content.fileCompression = "GZip";
    initByFile( "default.csv.gz" );

    setFields( new BaseFileField( "f1", -1, -1 ), new BaseFileField( "f2", -1, -1 ),
      new BaseFileField( "f2", -1, -1 ) );

    process();

    check( new Object[][] { { "first", "1", "1.1" }, { "second", "2", "2.2" }, { "third", "3", "3.3" } } );
  }

  @Test
  public void testVfsGzipCompression() throws Exception {

    meta.content.fileCompression = "None";
    String url = "gz:" + this.getClass().getResource( inPrefix + "default.csv.gz" );
    initByURL( url );

    setFields( new BaseFileField( "f1", -1, -1 ), new BaseFileField( "f2", -1, -1 ),
      new BaseFileField( "f2", -1, -1 ) );

    process();

    check( new Object[][] { { "first", "1", "1.1" }, { "second", "2", "2.2" }, { "third", "3", "3.3" } } );
  }

  @Test
  public void testVfsBzip2Compression() throws Exception {

    meta.content.fileCompression = "None";
    String url = "bz2:" + this.getClass().getResource( inPrefix + "default.csv.bz2" );
    initByURL( url );

    setFields( new BaseFileField( "f1", -1, -1 ), new BaseFileField( "f2", -1, -1 ),
      new BaseFileField( "f2", -1, -1 ) );

    process();

    check( new Object[][] { { "first", "1", "1.1" }, { "second", "2", "2.2" }, { "third", "3", "3.3" } } );
  }

  @Test
  public void testFixedWidth() throws Exception {

    meta.content.fileType = "Fixed";
    initByFile( "fixed.csv" );

    setFields( new BaseFileField( "f1", 0, 7 ), new BaseFileField( "f2", 8, 7 ), new BaseFileField( "f3",
        16, 7 ) );

    process();

    check( new Object[][] { { "first  ", "1      ", "1.1" }, { "second ", "2      ", "2.2" }, { "third  ", "3      ", "3.3" } } );
  }

  @Test
  public void testFixedWidthBytes() throws Exception {

    meta.content.header = false;
    meta.content.fileType = "Fixed";
    meta.content.fileFormat = "Unix";
    meta.content.encoding = "Shift_JIS";
    meta.content.length = "Bytes";
    initByFile( "test-fixed-length-bytes.txt" );

    setFields(
        new BaseFileField( "f1", 0, 5 ),
        new BaseFileField( "f2", 5, 3 ),
        new BaseFileField( "f3", 8, 1 ),
        new BaseFileField( "f4", 9, 3 ) );

    process();

    check( new Object[][] { { "1.000", "個 ", "T", "1.0" }, { "2.000", "M  ", "Z", "1.0" } } );
  }

  @Test
  public void testFixedWidthCharacters() throws Exception {
    meta.content.header = false;
    meta.content.fileType = "Fixed";
    meta.content.fileFormat = "DOS";
    meta.content.encoding = "ISO-8859-1";
    meta.content.length = "Characters";
    initByFile( "test-fixed-length-characters.txt" );

    setFields(
        new BaseFileField( "f1", 0, 3 ),
        new BaseFileField( "f2", 3, 2 ),
        new BaseFileField( "f3", 5, 2 ),
        new BaseFileField( "f4", 7, 4 ) );

    process();
    check( new Object[][] { { "ABC", "DE", "FG", "HIJK" }, { "LmN", "oP", "qR", "sTuV" } } );
  }

  @Test
  public void testFilterEmptyBacklog5381() throws Exception {

    meta.content.header = false;
    meta.content.fileType = "Fixed";
    meta.content.noEmptyLines = true;
    meta.content.fileFormat = "mixed";
    initByFile( "filterempty-BACKLOG-5381.csv" );

    setFields( new BaseFileField( "f", 0, 100 ) );

    process();

    check( new Object[][] { { "FirstLine => FirstLine " }, { "ThirdLine => SecondLine" }, { "SixthLine => ThirdLine" },
      { "NinthLine => FourthLine" }, { "" } } );
  }

  @Test
  public void testFilterVariables() throws Exception {

    initByFile( "default.csv" );

    Variables vars = new Variables();
    vars.setVariable( "VAR_TEST", "second" );
    data.filterProcessor =
        new TextFileFilterProcessor( new TextFileFilter[] { new TextFileFilter( 0, "${VAR_TEST}", false, false ) },
            vars );
    setFields( new BaseFileField( "f1", -1, -1 ), new BaseFileField( "f2", -1, -1 ),
      new BaseFileField( "f2", -1, -1 ) );

    process();

    check( new Object[][] { { "first", "1", "1.1" }, { "third", "3", "3.3" } } );
  }

  @Test
  public void testBOM_UTF8() throws Exception {

    meta.content.encoding = "UTF-32LE";
    meta.content.header = false;
    initByFile( "test-BOM-UTF-8.txt" );

    setFields( new BaseFileField( "f1", -1, -1 ), new BaseFileField( "f2", -1, -1 ) );

    process();

    check( new Object[][] { { "data", "1" } } );
  }

  @Test
  public void testBOM_UTF16BE() throws Exception {

    meta.content.encoding = "UTF-32LE";
    meta.content.header = false;
    initByFile( "test-BOM-UTF-16BE.txt" );

    setFields( new BaseFileField( "f1", -1, -1 ), new BaseFileField( "f2", -1, -1 ) );

    process();

    check( new Object[][] { { "data", "1" } } );
  }
}
