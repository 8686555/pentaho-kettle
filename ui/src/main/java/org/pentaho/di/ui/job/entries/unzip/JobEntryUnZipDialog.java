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


package org.pentaho.di.ui.job.entries.unzip;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.unzip.JobEntryUnZip;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.core.events.dialog.FilterType;
import org.pentaho.di.ui.core.events.dialog.SelectionAdapterFileDialogTextVar;
import org.pentaho.di.ui.core.events.dialog.SelectionAdapterOptions;
import org.pentaho.di.ui.core.events.dialog.SelectionOperation;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.job.dialog.JobDialog;
import org.pentaho.di.ui.job.entry.JobEntryDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * This dialog allows you to edit the Unzip job entry settings.
 *
 * @author Samatar Hassan
 * @since 25-09-2007
 */

public class JobEntryUnZipDialog extends JobEntryDialog implements JobEntryDialogInterface {
  private static Class<?> PKG = JobEntryUnZip.class; // for i18n purposes, needed by Translator2!!

  private static final String[] FILETYPES = new String[] {
    BaseMessages.getString( PKG, "JobUnZip.Filetype.Zip" ),
    BaseMessages.getString( PKG, "JobUnZip.Filetype.Jar" ),
    BaseMessages.getString( PKG, "JobUnZip.Filetype.All" ) };

  private Label wlName;
  private Text wName;
  private FormData fdlName, fdName;

  private Label wlZipFilename;
  private Button wbZipFilename, wbSourceDirectory;
  private TextVar wZipFilename;
  private FormData fdlZipFilename, fdbZipFilename, fdZipFilename, fdbSourceDirectory;

  private Button wOK, wCancel;
  private Listener lsOK, lsCancel;

  private JobEntryUnZip jobEntry;
  private Shell shell;

  private Label wlTargetDirectory;
  private TextVar wTargetDirectory;
  private FormData fdlTargetDirectory, fdTargetDirectory;

  private Label wlMovetoDirectory;
  private TextVar wMovetoDirectory;
  private FormData fdlMovetoDirectory, fdMovetoDirectory;

  private Label wlcreateMoveToDirectory;
  private Button wcreateMoveToDirectory;
  private FormData fdlcreateMoveToDirectory, fdcreateMoveToDirectory;

  private Label wlWildcard;
  private TextVar wWildcard;
  private FormData fdlWildcard, fdWildcard;

  private Label wlWildcardExclude;
  private TextVar wWildcardExclude;
  private FormData fdlWildcardExclude, fdWildcardExclude;

  private Label wlAfterUnZip;
  private CCombo wAfterUnZip;
  private FormData fdlAfterUnZip, fdAfterUnZip;

  private SelectionAdapter lsDef;

  private Group wFileResult;
  private FormData fdFileResult;

  private Group wSource;
  private FormData fdSource;

  // Add File to result
  private Label wlAddFileToResult;
  private Button wAddFileToResult;
  private FormData fdlAddFileToResult, fdAddFileToResult;

  private Button wbTargetDirectory;
  private FormData fdbTargetDirectory;

  private Button wbMovetoDirectory;
  private FormData fdbMovetoDirectory;

  private Label wlSetModificationDateToOriginal;
  private Button wSetModificationDateToOriginal;
  private FormData fdlSetModificationDateToOriginal, fdSetModificationDateToOriginal;

  private Label wlWildcardSource;
  private TextVar wWildcardSource;
  private FormData fdlWildcardSource, fdWildcardSource;

  // Get args from previous
  private Label wlArgsPrevious;
  private Button wArgsPrevious;
  private FormData fdlArgsPrevious, fdArgsPrevious;

  // Use zipfile name as root directory
  private Label wlRootZip;
  private Button wRootZip;
  private FormData fdlRootZip, fdRootZip;

  private Label wlIfFileExists;
  private CCombo wIfFileExists;
  private FormData fdlIfFileExists, fdIfFileExists;

  private Group wSuccessOn;
  private FormData fdSuccessOn;

  private Label wlSuccessCondition;
  private CCombo wSuccessCondition;
  private FormData fdlSuccessCondition, fdSuccessCondition;

  private Label wlNrErrorsLessThan;
  private TextVar wNrErrorsLessThan;
  private FormData fdlNrErrorsLessThan, fdNrErrorsLessThan;

  private Label wlAddDate;
  private Button wAddDate;
  private FormData fdlAddDate, fdAddDate;

  private Label wlAddOriginalTimestamp;
  private Button wAddOriginalTimestamp;
  private FormData fdlAddOriginalTimestamp, fdAddOriginalTimestamp;

  private Label wlAddTime;
  private Button wAddTime;
  private FormData fdlAddTime, fdAddTime;

  private Label wlSpecifyFormat;
  private Button wSpecifyFormat;
  private FormData fdlSpecifyFormat, fdSpecifyFormat;

  private Label wlDateTimeFormat;
  private CCombo wDateTimeFormat;
  private FormData fdlDateTimeFormat, fdDateTimeFormat;

  private Group wUnzippedFiles;
  private FormData fdUnzippedFiles;

  private Label wlCreateFolder;
  private FormData fdlCreateFolder, fdCreateFolder;
  private Button wCreateFolder;

  private CTabFolder wTabFolder;
  private Composite wAdvancedComp, wGeneralComp;
  private CTabItem wAdvancedTab, wGeneralTab;
  private FormData fdAdvancedComp, fdGeneralComp;
  private FormData fdTabFolder;

  private boolean changed;

  public JobEntryUnZipDialog( Shell parent, JobEntryInterface jobEntryInt, Repository rep, JobMeta jobMeta ) {
    super( parent, jobEntryInt, rep, jobMeta );
    jobEntry = (JobEntryUnZip) jobEntryInt;
    if ( this.jobEntry.getName() == null ) {
      this.jobEntry.setName( BaseMessages.getString( PKG, "JobUnZip.Name.Default" ) );
    }
  }

  public JobEntryInterface open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, props.getJobsDialogStyle() );
    props.setLook( shell );
    JobDialog.setShellImage( shell, jobEntry );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        jobEntry.setChanged();
      }
    };
    changed = jobEntry.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "JobUnZip.Title" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // ZipFilename line
    wlName = new Label( shell, SWT.RIGHT );
    wlName.setText( BaseMessages.getString( PKG, "JobUnZip.Name.Label" ) );
    props.setLook( wlName );
    fdlName = new FormData();
    fdlName.left = new FormAttachment( 0, 0 );
    fdlName.right = new FormAttachment( middle, -margin );
    fdlName.top = new FormAttachment( 0, margin );
    wlName.setLayoutData( fdlName );
    wName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wName );
    wName.addModifyListener( lsMod );
    fdName = new FormData();
    fdName.left = new FormAttachment( middle, 0 );
    fdName.top = new FormAttachment( 0, margin );
    fdName.right = new FormAttachment( 100, 0 );
    wName.setLayoutData( fdName );

    wTabFolder = new CTabFolder( shell, SWT.BORDER );
    props.setLook( wTabFolder, Props.WIDGET_STYLE_TAB );

    // ////////////////////////
    // START OF GENERAL TAB ///
    // ////////////////////////

    wGeneralTab = new CTabItem( wTabFolder, SWT.NONE );
    wGeneralTab.setText( BaseMessages.getString( PKG, "JobUnZip.Tab.General.Label" ) );

    wGeneralComp = new Composite( wTabFolder, SWT.NONE );
    props.setLook( wGeneralComp );

    FormLayout generalLayout = new FormLayout();
    generalLayout.marginWidth = 3;
    generalLayout.marginHeight = 3;
    wGeneralComp.setLayout( generalLayout );

    // file source grouping?
    // ////////////////////////
    // START OF file source GROUP///
    // /
    wSource = new Group( wGeneralComp, SWT.SHADOW_NONE );
    props.setLook( wSource );
    wSource.setText( BaseMessages.getString( PKG, "JobUnZip.Source.Group.Label" ) );

    FormLayout groupSourceLayout = new FormLayout();
    groupSourceLayout.marginWidth = 10;
    groupSourceLayout.marginHeight = 10;

    wSource.setLayout( groupSourceLayout );

    // Args from previous
    wlArgsPrevious = new Label( wSource, SWT.RIGHT );
    wlArgsPrevious.setText( BaseMessages.getString( PKG, "JobUnZip.ArgsPrevious.Label" ) );
    props.setLook( wlArgsPrevious );
    fdlArgsPrevious = new FormData();
    fdlArgsPrevious.left = new FormAttachment( 0, 0 );
    fdlArgsPrevious.top = new FormAttachment( 0, margin );
    fdlArgsPrevious.right = new FormAttachment( middle, -margin );
    wlArgsPrevious.setLayoutData( fdlArgsPrevious );
    wArgsPrevious = new Button( wSource, SWT.CHECK );
    props.setLook( wArgsPrevious );
    wArgsPrevious.setToolTipText( BaseMessages.getString( PKG, "JobUnZip.ArgsPrevious.Tooltip" ) );
    fdArgsPrevious = new FormData();
    fdArgsPrevious.left = new FormAttachment( middle, 0 );
    fdArgsPrevious.top = new FormAttachment( 0, margin );
    fdArgsPrevious.right = new FormAttachment( 100, 0 );
    wArgsPrevious.setLayoutData( fdArgsPrevious );
    wArgsPrevious.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        setArgdPrevious();
        jobEntry.setChanged();
      }
    } );

    // ZipFilename line
    wlZipFilename = new Label( wSource, SWT.RIGHT );
    wlZipFilename.setText( BaseMessages.getString( PKG, "JobUnZip.ZipFilename.Label" ) );
    props.setLook( wlZipFilename );
    fdlZipFilename = new FormData();
    fdlZipFilename.left = new FormAttachment( 0, 0 );
    fdlZipFilename.top = new FormAttachment( wArgsPrevious, margin );
    fdlZipFilename.right = new FormAttachment( middle, -margin );
    wlZipFilename.setLayoutData( fdlZipFilename );

    // Browse Source folders button ...
    wbSourceDirectory = new Button( wSource, SWT.PUSH | SWT.CENTER );
    props.setLook( wbSourceDirectory );
    wbSourceDirectory.setText( BaseMessages.getString( PKG, "JobUnZip.BrowseFolders.Label" ) );
    fdbSourceDirectory = new FormData();
    fdbSourceDirectory.right = new FormAttachment( 100, 0 );
    fdbSourceDirectory.top = new FormAttachment( wArgsPrevious, margin );
    wbSourceDirectory.setLayoutData( fdbSourceDirectory );

    // Browse files...
    wbZipFilename = new Button( wSource, SWT.PUSH | SWT.CENTER );
    props.setLook( wbZipFilename );
    wbZipFilename.setText( BaseMessages.getString( PKG, "JobUnZip.BrowseFiles.Label" ) );
    fdbZipFilename = new FormData();
    fdbZipFilename.right = new FormAttachment( wbSourceDirectory, -margin );
    fdbZipFilename.top = new FormAttachment( wArgsPrevious, margin );
    wbZipFilename.setLayoutData( fdbZipFilename );

    wZipFilename = new TextVar( jobMeta, wSource, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wZipFilename );
    wZipFilename.addModifyListener( lsMod );
    fdZipFilename = new FormData();
    fdZipFilename.left = new FormAttachment( middle, 0 );
    fdZipFilename.top = new FormAttachment( wArgsPrevious, margin );

    fdZipFilename.right = new FormAttachment( wbZipFilename, -margin );
    wZipFilename.setLayoutData( fdZipFilename );

    // Whenever something changes, set the tooltip to the expanded version:
    wZipFilename.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        wZipFilename.setToolTipText( jobMeta.environmentSubstitute( wZipFilename.getText() ) );
      }
    } );

    wbZipFilename.addSelectionListener( new SelectionAdapterFileDialogTextVar( jobMeta.getLogChannel(), wZipFilename, jobMeta,
            new SelectionAdapterOptions( jobMeta.getBowl(), SelectionOperation.FILE,
                    new FilterType[] { FilterType.ZIP, FilterType.JAR, FilterType.ALL }, FilterType.ZIP  ) ) );

    wbSourceDirectory.addSelectionListener( new SelectionAdapterFileDialogTextVar( jobMeta.getLogChannel(), wZipFilename, jobMeta,
            new SelectionAdapterOptions( jobMeta.getBowl(), SelectionOperation.FOLDER ) ) );


    // WildcardSource line
    wlWildcardSource = new Label( wSource, SWT.RIGHT );
    wlWildcardSource.setText( BaseMessages.getString( PKG, "JobUnZip.WildcardSource.Label" ) );
    props.setLook( wlWildcardSource );
    fdlWildcardSource = new FormData();
    fdlWildcardSource.left = new FormAttachment( 0, 0 );
    fdlWildcardSource.top = new FormAttachment( wZipFilename, margin );
    fdlWildcardSource.right = new FormAttachment( middle, -margin );
    wlWildcardSource.setLayoutData( fdlWildcardSource );
    wWildcardSource =
      new TextVar( jobMeta, wSource, SWT.SINGLE | SWT.LEFT | SWT.BORDER, BaseMessages.getString(
        PKG, "JobUnZip.WildcardSource.Tooltip" ) );
    props.setLook( wWildcardSource );
    wWildcardSource.addModifyListener( lsMod );
    fdWildcardSource = new FormData();
    fdWildcardSource.left = new FormAttachment( middle, 0 );
    fdWildcardSource.top = new FormAttachment( wZipFilename, margin );
    fdWildcardSource.right = new FormAttachment( 100, 0 );
    wWildcardSource.setLayoutData( fdWildcardSource );

    fdSource = new FormData();
    fdSource.left = new FormAttachment( 0, margin );
    fdSource.top = new FormAttachment( wName, margin );
    fdSource.right = new FormAttachment( 100, -margin );
    wSource.setLayoutData( fdSource );
    // ///////////////////////////////////////////////////////////
    // / END OF FILE SOURCE
    // ///////////////////////////////////////////////////////////

    // ////////////////////////
    // START OF UNZIPPED FILES GROUP///
    // /
    wUnzippedFiles = new Group( wGeneralComp, SWT.SHADOW_NONE );
    props.setLook( wUnzippedFiles );
    wUnzippedFiles.setText( BaseMessages.getString( PKG, "JobUnZip.UnzippedFiles.Group.Label" ) );

    FormLayout groupLayoutUnzipped = new FormLayout();
    groupLayoutUnzipped.marginWidth = 10;
    groupLayoutUnzipped.marginHeight = 10;

    wUnzippedFiles.setLayout( groupLayoutUnzipped );

    // Use zipfile name as root directory
    wlRootZip = new Label( wUnzippedFiles, SWT.RIGHT );
    wlRootZip.setText( BaseMessages.getString( PKG, "JobUnZip.RootZip.Label" ) );
    props.setLook( wlRootZip );
    fdlRootZip = new FormData();
    fdlRootZip.left = new FormAttachment( 0, 0 );
    fdlRootZip.top = new FormAttachment( wSource, margin );
    fdlRootZip.right = new FormAttachment( middle, -margin );
    wlRootZip.setLayoutData( fdlRootZip );
    wRootZip = new Button( wUnzippedFiles, SWT.CHECK );
    props.setLook( wRootZip );
    wRootZip.setToolTipText( BaseMessages.getString( PKG, "JobUnZip.RootZip.Tooltip" ) );
    fdRootZip = new FormData();
    fdRootZip.left = new FormAttachment( middle, 0 );
    fdRootZip.top = new FormAttachment( wSource, margin );
    fdRootZip.right = new FormAttachment( 100, 0 );
    wRootZip.setLayoutData( fdRootZip );
    wRootZip.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        jobEntry.setChanged();
      }
    } );

    // TargetDirectory line
    wlTargetDirectory = new Label( wUnzippedFiles, SWT.RIGHT );
    wlTargetDirectory.setText( BaseMessages.getString( PKG, "JobUnZip.TargetDir.Label" ) );
    props.setLook( wlTargetDirectory );
    fdlTargetDirectory = new FormData();
    fdlTargetDirectory.left = new FormAttachment( 0, 0 );
    fdlTargetDirectory.top = new FormAttachment( wRootZip, margin );
    fdlTargetDirectory.right = new FormAttachment( middle, -margin );
    wlTargetDirectory.setLayoutData( fdlTargetDirectory );

    // Browse folders button ...
    wbTargetDirectory = new Button( wUnzippedFiles, SWT.PUSH | SWT.CENTER );
    props.setLook( wbTargetDirectory );
    wbTargetDirectory.setText( BaseMessages.getString( PKG, "JobUnZip.BrowseFolders.Label" ) );
    fdbTargetDirectory = new FormData();
    fdbTargetDirectory.right = new FormAttachment( 100, 0 );
    fdbTargetDirectory.top = new FormAttachment( wRootZip, margin );
    wbTargetDirectory.setLayoutData( fdbTargetDirectory );

    wTargetDirectory =
      new TextVar( jobMeta, wUnzippedFiles, SWT.SINGLE | SWT.LEFT | SWT.BORDER, BaseMessages.getString(
        PKG, "JobUnZip.TargetDir.Tooltip" ) );
    props.setLook( wTargetDirectory );
    wTargetDirectory.addModifyListener( lsMod );
    fdTargetDirectory = new FormData();
    fdTargetDirectory.left = new FormAttachment( middle, 0 );
    fdTargetDirectory.top = new FormAttachment( wRootZip, margin );
    fdTargetDirectory.right = new FormAttachment( wbTargetDirectory, -margin );
    wTargetDirectory.setLayoutData( fdTargetDirectory );

    // Create Folder
    wlCreateFolder = new Label( wUnzippedFiles, SWT.RIGHT );
    wlCreateFolder.setText( BaseMessages.getString( PKG, "JobUnZip.CreateFolder.Label" ) );
    props.setLook( wlCreateFolder );
    fdlCreateFolder = new FormData();
    fdlCreateFolder.left = new FormAttachment( 0, 0 );
    fdlCreateFolder.top = new FormAttachment( wTargetDirectory, margin );
    fdlCreateFolder.right = new FormAttachment( middle, -margin );
    wlCreateFolder.setLayoutData( fdlCreateFolder );
    wCreateFolder = new Button( wUnzippedFiles, SWT.CHECK );
    wCreateFolder.setToolTipText( BaseMessages.getString( PKG, "JobUnZip.CreateFolder.Tooltip" ) );
    props.setLook( wCreateFolder );
    fdCreateFolder = new FormData();
    fdCreateFolder.left = new FormAttachment( middle, 0 );
    fdCreateFolder.top = new FormAttachment( wTargetDirectory, margin );
    fdCreateFolder.right = new FormAttachment( 100, 0 );
    wCreateFolder.setLayoutData( fdCreateFolder );
    wCreateFolder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        jobEntry.setChanged();
      }
    } );

    // Wildcard line
    wlWildcard = new Label( wUnzippedFiles, SWT.RIGHT );
    wlWildcard.setText( BaseMessages.getString( PKG, "JobUnZip.Wildcard.Label" ) );
    props.setLook( wlWildcard );
    fdlWildcard = new FormData();
    fdlWildcard.left = new FormAttachment( 0, 0 );
    fdlWildcard.top = new FormAttachment( wCreateFolder, margin );
    fdlWildcard.right = new FormAttachment( middle, -margin );
    wlWildcard.setLayoutData( fdlWildcard );
    wWildcard =
      new TextVar( jobMeta, wUnzippedFiles, SWT.SINGLE | SWT.LEFT | SWT.BORDER, BaseMessages.getString(
        PKG, "JobUnZip.Wildcard.Tooltip" ) );
    props.setLook( wWildcard );
    wWildcard.addModifyListener( lsMod );
    fdWildcard = new FormData();
    fdWildcard.left = new FormAttachment( middle, 0 );
    fdWildcard.top = new FormAttachment( wCreateFolder, margin );
    fdWildcard.right = new FormAttachment( 100, 0 );
    wWildcard.setLayoutData( fdWildcard );

    // Wildcard to exclude
    wlWildcardExclude = new Label( wUnzippedFiles, SWT.RIGHT );
    wlWildcardExclude.setText( BaseMessages.getString( PKG, "JobUnZip.WildcardExclude.Label" ) );
    props.setLook( wlWildcardExclude );
    fdlWildcardExclude = new FormData();
    fdlWildcardExclude.left = new FormAttachment( 0, 0 );
    fdlWildcardExclude.top = new FormAttachment( wWildcard, margin );
    fdlWildcardExclude.right = new FormAttachment( middle, -margin );
    wlWildcardExclude.setLayoutData( fdlWildcardExclude );
    wWildcardExclude =
      new TextVar( jobMeta, wUnzippedFiles, SWT.SINGLE | SWT.LEFT | SWT.BORDER, BaseMessages.getString(
        PKG, "JobUnZip.WildcardExclude.Tooltip" ) );
    props.setLook( wWildcardExclude );
    wWildcardExclude.addModifyListener( lsMod );
    fdWildcardExclude = new FormData();
    fdWildcardExclude.left = new FormAttachment( middle, 0 );
    fdWildcardExclude.top = new FormAttachment( wWildcard, margin );
    fdWildcardExclude.right = new FormAttachment( 100, 0 );
    wWildcardExclude.setLayoutData( fdWildcardExclude );

    // Create multi-part file?
    wlAddDate = new Label( wUnzippedFiles, SWT.RIGHT );
    wlAddDate.setText( BaseMessages.getString( PKG, "JobUnZip.AddDate.Label" ) );
    props.setLook( wlAddDate );
    fdlAddDate = new FormData();
    fdlAddDate.left = new FormAttachment( 0, 0 );
    fdlAddDate.top = new FormAttachment( wWildcardExclude, margin );
    fdlAddDate.right = new FormAttachment( middle, -margin );
    wlAddDate.setLayoutData( fdlAddDate );
    wAddDate = new Button( wUnzippedFiles, SWT.CHECK );
    props.setLook( wAddDate );
    wAddDate.setToolTipText( BaseMessages.getString( PKG, "JobUnZip.AddDate.Tooltip" ) );
    fdAddDate = new FormData();
    fdAddDate.left = new FormAttachment( middle, 0 );
    fdAddDate.top = new FormAttachment( wWildcardExclude, margin );
    fdAddDate.right = new FormAttachment( 100, 0 );
    wAddDate.setLayoutData( fdAddDate );
    wAddDate.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        jobEntry.setChanged();
        setDateTime();
      }
    } );
    // Create multi-part file?
    wlAddTime = new Label( wUnzippedFiles, SWT.RIGHT );
    wlAddTime.setText( BaseMessages.getString( PKG, "JobUnZip.AddTime.Label" ) );
    props.setLook( wlAddTime );
    fdlAddTime = new FormData();
    fdlAddTime.left = new FormAttachment( 0, 0 );
    fdlAddTime.top = new FormAttachment( wAddDate, margin );
    fdlAddTime.right = new FormAttachment( middle, -margin );
    wlAddTime.setLayoutData( fdlAddTime );
    wAddTime = new Button( wUnzippedFiles, SWT.CHECK );
    props.setLook( wAddTime );
    wAddTime.setToolTipText( BaseMessages.getString( PKG, "JobUnZip.AddTime.Tooltip" ) );
    fdAddTime = new FormData();
    fdAddTime.left = new FormAttachment( middle, 0 );
    fdAddTime.top = new FormAttachment( wAddDate, margin );
    fdAddTime.right = new FormAttachment( 100, 0 );
    wAddTime.setLayoutData( fdAddTime );
    wAddTime.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        jobEntry.setChanged();
        setDateTime();
      }
    } );

    // Specify date time format?
    wlSpecifyFormat = new Label( wUnzippedFiles, SWT.RIGHT );
    wlSpecifyFormat.setText( BaseMessages.getString( PKG, "JobUnZip.SpecifyFormat.Label" ) );
    props.setLook( wlSpecifyFormat );
    fdlSpecifyFormat = new FormData();
    fdlSpecifyFormat.left = new FormAttachment( 0, 0 );
    fdlSpecifyFormat.top = new FormAttachment( wAddTime, margin );
    fdlSpecifyFormat.right = new FormAttachment( middle, -margin );
    wlSpecifyFormat.setLayoutData( fdlSpecifyFormat );
    wSpecifyFormat = new Button( wUnzippedFiles, SWT.CHECK );
    props.setLook( wSpecifyFormat );
    wSpecifyFormat.setToolTipText( BaseMessages.getString( PKG, "JobUnZip.SpecifyFormat.Tooltip" ) );
    fdSpecifyFormat = new FormData();
    fdSpecifyFormat.left = new FormAttachment( middle, 0 );
    fdSpecifyFormat.top = new FormAttachment( wAddTime, margin );
    fdSpecifyFormat.right = new FormAttachment( 100, 0 );
    wSpecifyFormat.setLayoutData( fdSpecifyFormat );
    wSpecifyFormat.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        jobEntry.setChanged();
        setDateTimeFormat();
      }
    } );

    // Prepare a list of possible DateTimeFormats...
    String[] dats = Const.getDateFormats();

    // DateTimeFormat
    wlDateTimeFormat = new Label( wUnzippedFiles, SWT.RIGHT );
    wlDateTimeFormat.setText( BaseMessages.getString( PKG, "JobUnZip.DateTimeFormat.Label" ) );
    props.setLook( wlDateTimeFormat );
    fdlDateTimeFormat = new FormData();
    fdlDateTimeFormat.left = new FormAttachment( 0, 0 );
    fdlDateTimeFormat.top = new FormAttachment( wSpecifyFormat, margin );
    fdlDateTimeFormat.right = new FormAttachment( middle, -margin );
    wlDateTimeFormat.setLayoutData( fdlDateTimeFormat );
    wDateTimeFormat = new CCombo( wUnzippedFiles, SWT.BORDER | SWT.READ_ONLY );
    wDateTimeFormat.setEditable( true );
    props.setLook( wDateTimeFormat );
    wDateTimeFormat.addModifyListener( lsMod );
    fdDateTimeFormat = new FormData();
    fdDateTimeFormat.left = new FormAttachment( middle, 0 );
    fdDateTimeFormat.top = new FormAttachment( wSpecifyFormat, margin );
    fdDateTimeFormat.right = new FormAttachment( 100, 0 );
    wDateTimeFormat.setLayoutData( fdDateTimeFormat );
    for ( int x = 0; x < dats.length; x++ ) {
      wDateTimeFormat.add( dats[x] );
    }

    wlAddOriginalTimestamp = new Label( wUnzippedFiles, SWT.RIGHT );
    wlAddOriginalTimestamp.setText( BaseMessages.getString( PKG, "JobUnZip.AddOriginalTimestamp.Label" ) );
    props.setLook( wlAddOriginalTimestamp );
    fdlAddOriginalTimestamp = new FormData();
    fdlAddOriginalTimestamp.left = new FormAttachment( 0, 0 );
    fdlAddOriginalTimestamp.top = new FormAttachment( wDateTimeFormat, margin );
    fdlAddOriginalTimestamp.right = new FormAttachment( middle, -margin );
    wlAddOriginalTimestamp.setLayoutData( fdlAddOriginalTimestamp );
    wAddOriginalTimestamp = new Button( wUnzippedFiles, SWT.CHECK );
    props.setLook( wAddOriginalTimestamp );
    wAddOriginalTimestamp.setToolTipText( BaseMessages.getString( PKG, "JobUnZip.AddOriginalTimestamp.Tooltip" ) );
    fdAddOriginalTimestamp = new FormData();
    fdAddOriginalTimestamp.left = new FormAttachment( middle, 0 );
    fdAddOriginalTimestamp.top = new FormAttachment( wDateTimeFormat, margin );
    fdAddOriginalTimestamp.right = new FormAttachment( 100, 0 );
    wAddOriginalTimestamp.setLayoutData( fdAddOriginalTimestamp );
    wAddOriginalTimestamp.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        jobEntry.setChanged();
      }
    } );

    // Create multi-part file?
    wlSetModificationDateToOriginal = new Label( wUnzippedFiles, SWT.RIGHT );
    wlSetModificationDateToOriginal.setText( BaseMessages.getString(
      PKG, "JobUnZip.SetModificationDateToOriginal.Label" ) );
    props.setLook( wlSetModificationDateToOriginal );
    fdlSetModificationDateToOriginal = new FormData();
    fdlSetModificationDateToOriginal.left = new FormAttachment( 0, 0 );
    fdlSetModificationDateToOriginal.top = new FormAttachment( wAddOriginalTimestamp, margin );
    fdlSetModificationDateToOriginal.right = new FormAttachment( middle, -margin );
    wlSetModificationDateToOriginal.setLayoutData( fdlSetModificationDateToOriginal );
    wSetModificationDateToOriginal = new Button( wUnzippedFiles, SWT.CHECK );
    props.setLook( wSetModificationDateToOriginal );
    wSetModificationDateToOriginal.setToolTipText( BaseMessages.getString(
      PKG, "JobUnZip.SetModificationDateToOriginal.Tooltip" ) );
    fdSetModificationDateToOriginal = new FormData();
    fdSetModificationDateToOriginal.left = new FormAttachment( middle, 0 );
    fdSetModificationDateToOriginal.top = new FormAttachment( wAddOriginalTimestamp, margin );
    fdSetModificationDateToOriginal.right = new FormAttachment( 100, 0 );
    wSetModificationDateToOriginal.setLayoutData( fdSetModificationDateToOriginal );
    wSetModificationDateToOriginal.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        jobEntry.setChanged();
      }
    } );
    // If File Exists
    wlIfFileExists = new Label( wUnzippedFiles, SWT.RIGHT );
    wlIfFileExists.setText( BaseMessages.getString( PKG, "JobUnZip.IfFileExists.Label" ) );
    props.setLook( wlIfFileExists );
    fdlIfFileExists = new FormData();
    fdlIfFileExists.left = new FormAttachment( 0, 0 );
    fdlIfFileExists.right = new FormAttachment( middle, -margin );
    fdlIfFileExists.top = new FormAttachment( wSetModificationDateToOriginal, margin );
    wlIfFileExists.setLayoutData( fdlIfFileExists );
    wIfFileExists = new CCombo( wUnzippedFiles, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
    wIfFileExists.setItems( JobEntryUnZip.typeIfFileExistsDesc );
    wIfFileExists.select( 0 ); // +1: starts at -1
    props.setLook( wIfFileExists );

    fdIfFileExists = new FormData();
    fdIfFileExists.left = new FormAttachment( middle, 0 );
    fdIfFileExists.top = new FormAttachment( wSetModificationDateToOriginal, margin );
    fdIfFileExists.right = new FormAttachment( 100, 0 );
    wIfFileExists.setLayoutData( fdIfFileExists );

    wIfFileExists.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {

      }
    } );

    // After Zipping
    wlAfterUnZip = new Label( wUnzippedFiles, SWT.RIGHT );
    wlAfterUnZip.setText( BaseMessages.getString( PKG, "JobUnZip.AfterUnZip.Label" ) );
    props.setLook( wlAfterUnZip );
    fdlAfterUnZip = new FormData();
    fdlAfterUnZip.left = new FormAttachment( 0, 0 );
    fdlAfterUnZip.right = new FormAttachment( middle, 0 );
    fdlAfterUnZip.top = new FormAttachment( wIfFileExists, margin );
    wlAfterUnZip.setLayoutData( fdlAfterUnZip );
    wAfterUnZip = new CCombo( wUnzippedFiles, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
    wAfterUnZip.add( BaseMessages.getString( PKG, "JobUnZip.Do_Nothing_AfterUnZip.Label" ) );
    wAfterUnZip.add( BaseMessages.getString( PKG, "JobUnZip.Delete_Files_AfterUnZip.Label" ) );
    wAfterUnZip.add( BaseMessages.getString( PKG, "JobUnZip.Move_Files_AfterUnZip.Label" ) );

    wAfterUnZip.select( 0 ); // +1: starts at -1

    props.setLook( wAfterUnZip );
    fdAfterUnZip = new FormData();
    fdAfterUnZip.left = new FormAttachment( middle, 0 );
    fdAfterUnZip.top = new FormAttachment( wIfFileExists, margin );
    fdAfterUnZip.right = new FormAttachment( 100, 0 );
    wAfterUnZip.setLayoutData( fdAfterUnZip );

    wAfterUnZip.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        AfterUnZipActivate();

      }
    } );

    // moveTo Directory
    wlMovetoDirectory = new Label( wUnzippedFiles, SWT.RIGHT );
    wlMovetoDirectory.setText( BaseMessages.getString( PKG, "JobUnZip.MovetoDirectory.Label" ) );
    props.setLook( wlMovetoDirectory );
    fdlMovetoDirectory = new FormData();
    fdlMovetoDirectory.left = new FormAttachment( 0, 0 );
    fdlMovetoDirectory.top = new FormAttachment( wAfterUnZip, margin );
    fdlMovetoDirectory.right = new FormAttachment( middle, -margin );
    wlMovetoDirectory.setLayoutData( fdlMovetoDirectory );
    wMovetoDirectory =
      new TextVar( jobMeta, wUnzippedFiles, SWT.SINGLE | SWT.LEFT | SWT.BORDER, BaseMessages.getString(
        PKG, "JobUnZip.MovetoDirectory.Tooltip" ) );
    props.setLook( wMovetoDirectory );

    // Browse folders button ...
    wbMovetoDirectory = new Button( wUnzippedFiles, SWT.PUSH | SWT.CENTER );
    props.setLook( wbMovetoDirectory );
    wbMovetoDirectory.setText( BaseMessages.getString( PKG, "JobUnZip.BrowseFolders.Label" ) );
    fdbMovetoDirectory = new FormData();
    fdbMovetoDirectory.right = new FormAttachment( 100, 0 );
    fdbMovetoDirectory.top = new FormAttachment( wAfterUnZip, margin );
    wbMovetoDirectory.setLayoutData( fdbMovetoDirectory );

    wMovetoDirectory.addModifyListener( lsMod );
    fdMovetoDirectory = new FormData();
    fdMovetoDirectory.left = new FormAttachment( middle, 0 );
    fdMovetoDirectory.top = new FormAttachment( wAfterUnZip, margin );
    fdMovetoDirectory.right = new FormAttachment( wbMovetoDirectory, -margin );
    wMovetoDirectory.setLayoutData( fdMovetoDirectory );

    // create move to folder
    wlcreateMoveToDirectory = new Label( wUnzippedFiles, SWT.RIGHT );
    wlcreateMoveToDirectory.setText( BaseMessages.getString( PKG, "JobUnZip.createMoveToFolder.Label" ) );
    props.setLook( wlcreateMoveToDirectory );
    fdlcreateMoveToDirectory = new FormData();
    fdlcreateMoveToDirectory.left = new FormAttachment( 0, 0 );
    fdlcreateMoveToDirectory.top = new FormAttachment( wMovetoDirectory, margin );
    fdlcreateMoveToDirectory.right = new FormAttachment( middle, -margin );
    wlcreateMoveToDirectory.setLayoutData( fdlcreateMoveToDirectory );
    wcreateMoveToDirectory = new Button( wUnzippedFiles, SWT.CHECK );
    props.setLook( wcreateMoveToDirectory );
    wcreateMoveToDirectory.setToolTipText( BaseMessages.getString( PKG, "JobUnZip.createMoveToFolder.Tooltip" ) );
    fdcreateMoveToDirectory = new FormData();
    fdcreateMoveToDirectory.left = new FormAttachment( middle, 0 );
    fdcreateMoveToDirectory.top = new FormAttachment( wMovetoDirectory, margin );
    fdcreateMoveToDirectory.right = new FormAttachment( 100, 0 );
    wcreateMoveToDirectory.setLayoutData( fdcreateMoveToDirectory );
    wcreateMoveToDirectory.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        jobEntry.setChanged();
      }
    } );

    fdUnzippedFiles = new FormData();
    fdUnzippedFiles.left = new FormAttachment( 0, margin );
    fdUnzippedFiles.top = new FormAttachment( wSource, margin );
    fdUnzippedFiles.right = new FormAttachment( 100, -margin );
    wUnzippedFiles.setLayoutData( fdUnzippedFiles );
    // ///////////////////////////////////////////////////////////
    // / END OF UNZIPPED FILES
    // ///////////////////////////////////////////////////////////

    fdGeneralComp = new FormData();
    fdGeneralComp.left = new FormAttachment( 0, 0 );
    fdGeneralComp.top = new FormAttachment( 0, 0 );
    fdGeneralComp.right = new FormAttachment( 100, 0 );
    fdGeneralComp.bottom = new FormAttachment( 500, -margin );
    wGeneralComp.setLayoutData( fdGeneralComp );

    wGeneralComp.layout();
    wGeneralTab.setControl( wGeneralComp );
    props.setLook( wGeneralComp );
    // ///////////////////////////////////////////////////////////
    // / END OF GENERAL TAB
    // ///////////////////////////////////////////////////////////

    // ////////////////////////
    // START OF ADVANCED TAB ///
    // ////////////////////////

    wAdvancedTab = new CTabItem( wTabFolder, SWT.NONE );
    wAdvancedTab.setText( BaseMessages.getString( PKG, "JobUnZip.Tab.Advanced.Label" ) );

    wAdvancedComp = new Composite( wTabFolder, SWT.NONE );
    props.setLook( wAdvancedComp );

    FormLayout advancedLayout = new FormLayout();
    advancedLayout.marginWidth = 3;
    advancedLayout.marginHeight = 3;
    wAdvancedComp.setLayout( advancedLayout );

    // file result grouping?
    // ////////////////////////
    // START OF LOGGING GROUP///
    // /
    wFileResult = new Group( wAdvancedComp, SWT.SHADOW_NONE );
    props.setLook( wFileResult );
    wFileResult.setText( BaseMessages.getString( PKG, "JobUnZip.FileResult.Group.Label" ) );

    FormLayout groupLayout = new FormLayout();
    groupLayout.marginWidth = 10;
    groupLayout.marginHeight = 10;

    wFileResult.setLayout( groupLayout );

    // Add file to result
    wlAddFileToResult = new Label( wFileResult, SWT.RIGHT );
    wlAddFileToResult.setText( BaseMessages.getString( PKG, "JobUnZip.AddFileToResult.Label" ) );
    props.setLook( wlAddFileToResult );
    fdlAddFileToResult = new FormData();
    fdlAddFileToResult.left = new FormAttachment( 0, 0 );
    fdlAddFileToResult.top = new FormAttachment( wSource, margin );
    fdlAddFileToResult.right = new FormAttachment( middle, -margin );
    wlAddFileToResult.setLayoutData( fdlAddFileToResult );
    wAddFileToResult = new Button( wFileResult, SWT.CHECK );
    props.setLook( wAddFileToResult );
    wAddFileToResult.setToolTipText( BaseMessages.getString( PKG, "JobUnZip.AddFileToResult.Tooltip" ) );
    fdAddFileToResult = new FormData();
    fdAddFileToResult.left = new FormAttachment( middle, 0 );
    fdAddFileToResult.top = new FormAttachment( wSource, margin );
    fdAddFileToResult.right = new FormAttachment( 100, 0 );
    wAddFileToResult.setLayoutData( fdAddFileToResult );
    wAddFileToResult.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        jobEntry.setChanged();
      }
    } );

    fdFileResult = new FormData();
    fdFileResult.left = new FormAttachment( 0, margin );
    fdFileResult.top = new FormAttachment( wUnzippedFiles, margin );
    fdFileResult.right = new FormAttachment( 100, -margin );
    wFileResult.setLayoutData( fdFileResult );
    // ///////////////////////////////////////////////////////////
    // / END OF FILE RESULT
    // ///////////////////////////////////////////////////////////

    // SuccessOngrouping?
    // ////////////////////////
    // START OF SUCCESS ON GROUP///
    // /
    wSuccessOn = new Group( wAdvancedComp, SWT.SHADOW_NONE );
    props.setLook( wSuccessOn );
    wSuccessOn.setText( BaseMessages.getString( PKG, "JobUnZip.SuccessOn.Group.Label" ) );

    FormLayout successongroupLayout = new FormLayout();
    successongroupLayout.marginWidth = 10;
    successongroupLayout.marginHeight = 10;

    wSuccessOn.setLayout( successongroupLayout );

    // Success Condition
    wlSuccessCondition = new Label( wSuccessOn, SWT.RIGHT );
    wlSuccessCondition.setText( BaseMessages.getString( PKG, "JobUnZip.SuccessCondition.Label" ) + " " );
    props.setLook( wlSuccessCondition );
    fdlSuccessCondition = new FormData();
    fdlSuccessCondition.left = new FormAttachment( 0, 0 );
    fdlSuccessCondition.right = new FormAttachment( middle, 0 );
    fdlSuccessCondition.top = new FormAttachment( wFileResult, margin );
    wlSuccessCondition.setLayoutData( fdlSuccessCondition );
    wSuccessCondition = new CCombo( wSuccessOn, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
    wSuccessCondition.add( BaseMessages.getString( PKG, "JobUnZip.SuccessWhenAllWorksFine.Label" ) );
    wSuccessCondition.add( BaseMessages.getString( PKG, "JobUnZip.SuccessWhenAtLeat.Label" ) );
    wSuccessCondition.add( BaseMessages.getString( PKG, "JobUnZip.SuccessWhenNrErrorsLessThan.Label" ) );
    wSuccessCondition.select( 0 ); // +1: starts at -1

    props.setLook( wSuccessCondition );
    fdSuccessCondition = new FormData();
    fdSuccessCondition.left = new FormAttachment( middle, 0 );
    fdSuccessCondition.top = new FormAttachment( wFileResult, margin );
    fdSuccessCondition.right = new FormAttachment( 100, 0 );
    wSuccessCondition.setLayoutData( fdSuccessCondition );
    wSuccessCondition.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        activeSuccessCondition();

      }
    } );

    // Success when number of errors less than
    wlNrErrorsLessThan = new Label( wSuccessOn, SWT.RIGHT );
    wlNrErrorsLessThan.setText( BaseMessages.getString( PKG, "JobUnZip.NrBadFormedLessThan.Label" ) + " " );
    props.setLook( wlNrErrorsLessThan );
    fdlNrErrorsLessThan = new FormData();
    fdlNrErrorsLessThan.left = new FormAttachment( 0, 0 );
    fdlNrErrorsLessThan.top = new FormAttachment( wSuccessCondition, margin );
    fdlNrErrorsLessThan.right = new FormAttachment( middle, -margin );
    wlNrErrorsLessThan.setLayoutData( fdlNrErrorsLessThan );

    wNrErrorsLessThan =
      new TextVar( jobMeta, wSuccessOn, SWT.SINGLE | SWT.LEFT | SWT.BORDER, BaseMessages.getString(
        PKG, "JobUnZip.NrBadFormedLessThan.Tooltip" ) );
    props.setLook( wNrErrorsLessThan );
    wNrErrorsLessThan.addModifyListener( lsMod );
    fdNrErrorsLessThan = new FormData();
    fdNrErrorsLessThan.left = new FormAttachment( middle, 0 );
    fdNrErrorsLessThan.top = new FormAttachment( wSuccessCondition, margin );
    fdNrErrorsLessThan.right = new FormAttachment( 100, -margin );
    wNrErrorsLessThan.setLayoutData( fdNrErrorsLessThan );

    fdSuccessOn = new FormData();
    fdSuccessOn.left = new FormAttachment( 0, margin );
    fdSuccessOn.top = new FormAttachment( wFileResult, margin );
    fdSuccessOn.right = new FormAttachment( 100, -margin );
    wSuccessOn.setLayoutData( fdSuccessOn );
    // ///////////////////////////////////////////////////////////
    // / END OF Success ON GROUP
    // ///////////////////////////////////////////////////////////

    fdAdvancedComp = new FormData();
    fdAdvancedComp.left = new FormAttachment( 0, 0 );
    fdAdvancedComp.top = new FormAttachment( 0, 0 );
    fdAdvancedComp.right = new FormAttachment( 100, 0 );
    fdAdvancedComp.bottom = new FormAttachment( 500, -margin );
    wAdvancedComp.setLayoutData( fdAdvancedComp );

    wAdvancedComp.layout();
    wAdvancedTab.setControl( wAdvancedComp );
    props.setLook( wAdvancedComp );

    // ///////////////////////////////////////////////////////////
    // / END OF Advanced TAB
    // ///////////////////////////////////////////////////////////

    fdTabFolder = new FormData();
    fdTabFolder.left = new FormAttachment( 0, 0 );
    fdTabFolder.top = new FormAttachment( wName, margin );
    fdTabFolder.right = new FormAttachment( 100, 0 );
    fdTabFolder.bottom = new FormAttachment( 100, -50 );
    wTabFolder.setLayoutData( fdTabFolder );

    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    BaseStepDialog.positionBottomButtons( shell, new Button[] { wOK, wCancel }, margin, wTabFolder );

    // Add listeners
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wCancel.addListener( SWT.Selection, lsCancel );
    wOK.addListener( SWT.Selection, lsOK );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wbTargetDirectory.addSelectionListener( new SelectionAdapterFileDialogTextVar( jobMeta.getLogChannel(), wTargetDirectory, jobMeta,
            new SelectionAdapterOptions( jobMeta.getBowl(), SelectionOperation.FOLDER ) ) );

    wbMovetoDirectory.addSelectionListener( new SelectionAdapterFileDialogTextVar( jobMeta.getLogChannel(), wMovetoDirectory, jobMeta,
            new SelectionAdapterOptions( jobMeta.getBowl(), SelectionOperation.FOLDER ) ) );

    wName.addSelectionListener( lsDef );
    wZipFilename.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    getData();
    setArgdPrevious();
    AfterUnZipActivate();
    setDateTimeFormat();
    activeSuccessCondition();
    wTabFolder.setSelection( 0 );
    BaseStepDialog.setSize( shell );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return jobEntry;
  }

  private void setDateTimeFormat() {
    if ( wSpecifyFormat.getSelection() ) {
      wAddDate.setSelection( false );
      wAddTime.setSelection( false );
    }

    wDateTimeFormat.setEnabled( wSpecifyFormat.getSelection() );
    wlDateTimeFormat.setEnabled( wSpecifyFormat.getSelection() );
    wAddDate.setEnabled( !wSpecifyFormat.getSelection() );
    wlAddDate.setEnabled( !wSpecifyFormat.getSelection() );
    wAddTime.setEnabled( !wSpecifyFormat.getSelection() );
    wlAddTime.setEnabled( !wSpecifyFormat.getSelection() );
    setDateTime();
  }

  private void setDateTime() {
    boolean enable = wAddDate.getSelection() || wAddTime.getSelection() || wSpecifyFormat.getSelection();
    wlAddOriginalTimestamp.setEnabled( enable );
    wAddOriginalTimestamp.setEnabled( enable );
  }

  public void AfterUnZipActivate() {

    jobEntry.setChanged();
    if ( wAfterUnZip.getSelectionIndex() == 2 ) {
      wMovetoDirectory.setEnabled( true );
      wlMovetoDirectory.setEnabled( true );
      wbMovetoDirectory.setEnabled( true );
      wcreateMoveToDirectory.setEnabled( true );
      wlcreateMoveToDirectory.setEnabled( true );
    } else {
      wMovetoDirectory.setEnabled( false );
      wlMovetoDirectory.setEnabled( false );
      wbMovetoDirectory.setEnabled( false );
      wcreateMoveToDirectory.setEnabled( false );
      wlcreateMoveToDirectory.setEnabled( false );
    }
  }

  private void activeSuccessCondition() {
    wlNrErrorsLessThan.setEnabled( wSuccessCondition.getSelectionIndex() != 0 );
    wNrErrorsLessThan.setEnabled( wSuccessCondition.getSelectionIndex() != 0 );
  }

  private void setArgdPrevious() {
    wlZipFilename.setEnabled( !wArgsPrevious.getSelection() );
    wZipFilename.setEnabled( !wArgsPrevious.getSelection() );
    wbZipFilename.setEnabled( !wArgsPrevious.getSelection() );
    wbSourceDirectory.setEnabled( !wArgsPrevious.getSelection() );
    wWildcardSource.setEnabled( !wArgsPrevious.getSelection() );
    wlWildcardSource.setEnabled( !wArgsPrevious.getSelection() );
  }

  public void dispose() {
    WindowProperty winprop = new WindowProperty( shell );
    props.setScreen( winprop );
    shell.dispose();
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    wName.setText( Const.nullToEmpty( jobEntry.getName() ) );
    wZipFilename.setText( Const.nullToEmpty( jobEntry.getZipFilename() ) );
    wWildcardSource.setText( Const.nullToEmpty( jobEntry.getWildcardSource() ) );

    wWildcard.setText( Const.nullToEmpty( jobEntry.getWildcard() ) );
    wWildcardExclude.setText( Const.nullToEmpty( jobEntry.getWildcardExclude() ) );
    wTargetDirectory.setText( Const.nullToEmpty( jobEntry.getSourceDirectory() ) );
    wMovetoDirectory.setText( Const.nullToEmpty( jobEntry.getMoveToDirectory() ) );

    if ( jobEntry.afterunzip >= 0 ) {
      wAfterUnZip.select( jobEntry.afterunzip );
    } else {
      wAfterUnZip.select( 0 ); // NOTHING
    }

    wAddFileToResult.setSelection( jobEntry.isAddFileToResult() );
    wArgsPrevious.setSelection( jobEntry.getDatafromprevious() );
    wAddDate.setSelection( jobEntry.isDateInFilename() );
    wAddTime.setSelection( jobEntry.isTimeInFilename() );

    wDateTimeFormat.setText( Const.nullToEmpty( jobEntry.getDateTimeFormat() ) );
    wSpecifyFormat.setSelection( jobEntry.isSpecifyFormat() );

    wRootZip.setSelection( jobEntry.isCreateRootFolder() );
    wCreateFolder.setSelection( jobEntry.isCreateFolder() );

    wNrErrorsLessThan.setText( Const.NVL( jobEntry.getLimit(), "10" ) );

    if ( jobEntry.getSuccessCondition() != null ) {
      if ( jobEntry.getSuccessCondition().equals( jobEntry.SUCCESS_IF_AT_LEAST_X_FILES_UN_ZIPPED ) ) {
        wSuccessCondition.select( 1 );
      } else if ( jobEntry.getSuccessCondition().equals( jobEntry.SUCCESS_IF_ERRORS_LESS ) ) {
        wSuccessCondition.select( 2 );
      } else {
        wSuccessCondition.select( 0 );
      }
    } else {
      wSuccessCondition.select( 0 );
    }

    wAddOriginalTimestamp.setSelection( jobEntry.isOriginalTimestamp() );
    wSetModificationDateToOriginal.setSelection( jobEntry.isOriginalModificationDate() );
    wIfFileExists.select( jobEntry.getIfFileExist() );
    wcreateMoveToDirectory.setSelection( jobEntry.isCreateMoveToDirectory() );

    wName.selectAll();
    wName.setFocus();
  }

  private void cancel() {
    jobEntry.setChanged( changed );
    jobEntry = null;
    dispose();
  }

  private void ok() {
    if ( Utils.isEmpty( wName.getText() ) ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setText( BaseMessages.getString( PKG, "System.StepJobEntryNameMissing.Title" ) );
      mb.setMessage( BaseMessages.getString( PKG, "System.JobEntryNameMissing.Msg" ) );
      mb.open();
      return;
    }
    jobEntry.setName( wName.getText() );
    jobEntry.setZipFilename( wZipFilename.getText() );
    jobEntry.setWildcardSource( wWildcardSource.getText() );

    jobEntry.setWildcard( wWildcard.getText() );
    jobEntry.setWildcardExclude( wWildcardExclude.getText() );
    jobEntry.setSourceDirectory( wTargetDirectory.getText() );

    jobEntry.setMoveToDirectory( wMovetoDirectory.getText() );

    jobEntry.afterunzip = wAfterUnZip.getSelectionIndex();

    jobEntry.setAddFileToResult( wAddFileToResult.getSelection() );

    jobEntry.setDatafromprevious( wArgsPrevious.getSelection() );
    jobEntry.setDateInFilename( wAddDate.getSelection() );
    jobEntry.setTimeInFilename( wAddTime.getSelection() );
    jobEntry.setSpecifyFormat( wSpecifyFormat.getSelection() );
    jobEntry.setDateTimeFormat( wDateTimeFormat.getText() );

    jobEntry.setCreateRootFolder( wRootZip.getSelection() );
    jobEntry.setCreateFolder( wCreateFolder.getSelection() );
    jobEntry.setLimit( wNrErrorsLessThan.getText() );

    if ( wSuccessCondition.getSelectionIndex() == 1 ) {
      jobEntry.setSuccessCondition( jobEntry.SUCCESS_IF_AT_LEAST_X_FILES_UN_ZIPPED );
    } else if ( wSuccessCondition.getSelectionIndex() == 2 ) {
      jobEntry.setSuccessCondition( jobEntry.SUCCESS_IF_ERRORS_LESS );
    } else {
      jobEntry.setSuccessCondition( jobEntry.SUCCESS_IF_NO_ERRORS );
    }

    jobEntry.setIfFileExists( wIfFileExists.getSelectionIndex() );
    jobEntry.setCreateMoveToDirectory( wcreateMoveToDirectory.getSelection() );
    jobEntry.setAddOriginalTimestamp( wAddOriginalTimestamp.getSelection() );
    jobEntry.setOriginalModificationDate( wSetModificationDateToOriginal.getSelection() );
    dispose();
  }

  public boolean evaluates() {
    return true;
  }

  public boolean isUnconditional() {
    return false;
  }
}
