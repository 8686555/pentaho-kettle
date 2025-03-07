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


package org.pentaho.di.ui.core.widget;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.gui.GUIResource;

/**
 * A Widget that combines a Text widget with a Variable button that will insert an Environment variable. The tool tip of
 * the text widget shows the content of the Text widget with expanded variables.
 *
 * @author Matt
 * @since 17-may-2006
 */
public class ComboVar extends Composite {
  private static Class<?> PKG = ComboVar.class; // for i18n purposes, needed by Translator2!!

  private String toolTipText;

  // private static final PropsUI props = PropsUI.getInstance();

  private ControlDecoration controlDecoration;

  private GetCaretPositionInterface getCaretPositionInterface;

  private InsertTextInterface insertTextInterface;

  private ControlSpaceKeyAdapter controlSpaceKeyAdapter;

  private VariableSpace variables;

  private CCombo wCombo;

  private ModifyListener modifyListenerTooltipText;

  public ComboVar( VariableSpace space, Composite composite, int flags ) {
    this( space, composite, flags, null, null, null );
  }

  public ComboVar( VariableSpace space, Composite composite, int flags, String toolTipText ) {
    this( space, composite, flags, toolTipText, null, null );
  }

  public ComboVar( VariableSpace space, Composite composite, int flags,
    GetCaretPositionInterface getCaretPositionInterface, InsertTextInterface insertTextInterface ) {
    this( space, composite, flags, null, getCaretPositionInterface, insertTextInterface );
  }

  public ComboVar( VariableSpace space, Composite composite, int flags, String toolTipText,
                   GetCaretPositionInterface getCaretPositionInterface, InsertTextInterface insertTextInterface ) {
    super( composite, SWT.NONE );
    this.toolTipText = toolTipText;
    this.getCaretPositionInterface = getCaretPositionInterface;
    this.insertTextInterface = insertTextInterface;
    this.variables = space;

    // props.setLook(this);

    // int margin = Const.MARGIN;
    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = 0;
    formLayout.marginHeight = 0;
    formLayout.marginTop = 0;
    formLayout.marginBottom = 0;

    this.setLayout( formLayout );

    // add a text field on it...
    wCombo = new CCombo( this, flags );

    Image image = GUIResource.getInstance().getImageVariable();
    controlDecoration = new ControlDecoration( wCombo, SWT.CENTER | SWT.RIGHT, this );
    controlDecoration.setImage( image );
    controlDecoration.setDescriptionText( BaseMessages.getString( PKG, "TextVar.tooltip.InsertVariable" ) );

    // props.setLook(wText);
    modifyListenerTooltipText = getModifyListenerTooltipText( wCombo );
    wCombo.addModifyListener( modifyListenerTooltipText );

    // SelectionAdapter lsVar = null;
    // VariableButtonListenerFactory.getSelectionAdapter(this, wText, getCaretPositionInterface,
    // insertTextInterface, variables);
    // wText.addKeyListener(getControlSpaceKeyListener(variables, wText, lsVar, getCaretPositionInterface,
    // insertTextInterface));

    controlSpaceKeyAdapter =
      new ControlSpaceKeyAdapter( variables, wCombo, getCaretPositionInterface, insertTextInterface );
    wCombo.addKeyListener( controlSpaceKeyAdapter );

    FormData fdText = new FormData();
    fdText.top = new FormAttachment( 0, 0 );
    fdText.left = new FormAttachment( 0, 0 );
    fdText.right = new FormAttachment( 100, -image.getBounds().width );
    wCombo.setLayoutData( fdText );
  }

  /**
   * @return the getCaretPositionInterface
   */
  public GetCaretPositionInterface getGetCaretPositionInterface() {
    return getCaretPositionInterface;
  }

  /**
   * @param getCaretPositionInterface
   *          the getCaretPositionInterface to set
   */
  public void setGetCaretPositionInterface( GetCaretPositionInterface getCaretPositionInterface ) {
    this.getCaretPositionInterface = getCaretPositionInterface;
  }

  /**
   * @return the insertTextInterface
   */
  public InsertTextInterface getInsertTextInterface() {
    return insertTextInterface;
  }

  /**
   * @param insertTextInterface
   *          the insertTextInterface to set
   */
  public void setInsertTextInterface( InsertTextInterface insertTextInterface ) {
    this.insertTextInterface = insertTextInterface;
  }

  private ModifyListener getModifyListenerTooltipText( final CCombo comboField ) {
    return new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        String tip = comboField.getText();
        if ( !Utils.isEmpty( tip ) && !Utils.isEmpty( toolTipText ) ) {
          tip += Const.CR + Const.CR + toolTipText;
        }

        if ( Utils.isEmpty( tip ) ) {
          tip = toolTipText;
        }
        comboField.setToolTipText( variables.environmentSubstitute( tip ) );
      }
    };
  }

  /**
   * @return the text in the Text widget
   */
  public String getText() {
    return wCombo.getText();
  }

  /**
   * @param text
   *          the text in the Text widget to set.
   */
  public void setText( String text ) {
    wCombo.setText( text );
    modifyListenerTooltipText.modifyText( null );
  }

  public CCombo getCComboWidget() {
    return wCombo;
  }

  /**
   * Add a modify listener to the text widget
   *
   * @param modifyListener
   */
  public void addModifyListener( ModifyListener modifyListener ) {
    wCombo.addModifyListener( modifyListener );
  }

  public void addSelectionListener( SelectionAdapter lsDef ) {
    wCombo.addSelectionListener( lsDef );
  }

  public void addKeyListener( KeyListener lsKey ) {
    wCombo.addKeyListener( lsKey );
  }

  public void addFocusListener( FocusListener lsFocus ) {
    wCombo.addFocusListener( lsFocus );
  }

  public void setEnabled( boolean flag ) {
    wCombo.setEnabled( flag );
  }

  public boolean setFocus() {
    return wCombo.setFocus();
  }

  public void addTraverseListener( TraverseListener tl ) {
    wCombo.addTraverseListener( tl );
  }

  public void setToolTipText( String toolTipText ) {
    this.toolTipText = toolTipText;
    wCombo.setToolTipText( toolTipText );
    modifyListenerTooltipText.modifyText( null );
  }

  public void setEditable( boolean editable ) {
    wCombo.setEditable( editable );
  }

  public void setVariables( VariableSpace vars ) {
    variables = vars;
    controlSpaceKeyAdapter.setVariables( variables );
    modifyListenerTooltipText.modifyText( null );
  }

  public void setItems( String[] items ) {
    wCombo.setItems( items );
  }

  public String[] getItems() {
    return wCombo.getItems();
  }

  public void add( String item ) {
    wCombo.add( item );
  }

  public int getItemCount() {
    return wCombo.getItemCount();
  }

  public void removeAll() {
    wCombo.removeAll();
  }

  public void remove( int index ) {
    wCombo.remove( index );
  }

  public void select( int index ) {
    wCombo.select( index );
    modifyListenerTooltipText.modifyText( null );
  }
}
