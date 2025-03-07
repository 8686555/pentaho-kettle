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


package org.pentaho.di.trans.steps.univariatestats;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * Holds temporary data and has routines for computing derived statistics.
 *
 * @author Mark Hall (mhall{[at]}pentaho.org)
 * @version 1.0
 */
public class UnivariateStatsData extends BaseStepData implements StepDataInterface {

  // this class contains intermediate results,
  // info about the input format, derived output
  // format etc.

  // the input data format
  protected RowMetaInterface m_inputRowMeta;

  // the output data format
  protected RowMetaInterface m_outputRowMeta;

  /**
   * contains the FieldIndexs - one for each UnivariateStatsMetaFunction
   */
  protected FieldIndex[] m_indexes;

  /**
   * Creates a new <code>UnivariateStatsData</code> instance.
   */
  public UnivariateStatsData() {
    super();
  }

  /**
   * Set the FieldIndexes
   *
   * @param fis
   *          a <code>FieldIndex[]</code> value
   */
  public void setFieldIndexes( FieldIndex[] fis ) {
    m_indexes = fis;
  }

  /**
   * Get the fieldIndexes
   *
   * @return a <code>FieldIndex[]</code> value
   */
  public FieldIndex[] getFieldIndexes() {
    return m_indexes;
  }

  /**
   * Get the meta data for the input format
   *
   * @return a <code>RowMetaInterface</code> value
   */
  public RowMetaInterface getInputRowMeta() {
    return m_inputRowMeta;
  }

  /**
   * Save the meta data for the input format. (I'm not sure that this is really needed)
   *
   * @param rmi
   *          a <code>RowMetaInterface</code> value
   */
  public void setInputRowMeta( RowMetaInterface rmi ) {
    m_inputRowMeta = rmi;
  }

  /**
   * Get the meta data for the output format
   *
   * @return a <code>RowMetaInterface</code> value
   */
  public RowMetaInterface getOutputRowMeta() {
    return m_outputRowMeta;
  }

  /**
   * Set the meta data for the output format
   *
   * @param rmi
   *          a <code>RowMetaInterface</code> value
   */
  public void setOutputRowMeta( RowMetaInterface rmi ) {
    m_outputRowMeta = rmi;
  }
}
