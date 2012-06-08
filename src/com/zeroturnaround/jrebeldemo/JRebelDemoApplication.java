package com.zeroturnaround.jrebeldemo;

import java.io.File;

import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.gwt.ace.AceMode;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.data.util.TextFileProperty;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

public class JRebelDemoApplication extends SpringContextApplication {
  private static final String DEMO_ROOT = System.getProperty("demo.root", ".");

  private static Label DIRECTORY_LABEL = new Label("No content, sorry, this is a directory", Label.CONTENT_XHTML);

  private final AbsoluteLayout mainLayout = new AbsoluteLayout();
  private final Window mainWindow = new Window("JRebel Demo Application", mainLayout);

  @Autowired
  private AceEditor editor;

  /**
   * This is the init method of application! We needed spring integration to demo JRebel framework support.
   * 
   * In order to start this demo successfully, add following options to server startup:
   * #path to root dir of this project (used by TreeTable to show files)
   * -Ddemo.root=/home/shelajev/workspace/JRebelDemo
   * 
   * #path to log file for server log to work
   * -Ddemo.logfile=path/to/server's/log/file
   * 
   * You can configure log file location in eclipse from:
   * Double-click on server (I use tomcat) -> Open launch configuration -> Common tab
   * Configure a file where standard output will go and use that file in the option above. 
   * 
   * Oleg.
   * 
   */
  @Override
  protected void initSpringApplication(ConfigurableWebApplicationContext context) {
    // showSplash(mainWindow);
    buildLayout(mainLayout);
    setMainWindow(mainWindow);
  }

  private void showSplash(Window mainWindow) {
    VerticalLayout splash = new VerticalLayout();
    splash.setSizeFull();

    Embedded jrebelLogo = new Embedded("", new ExternalResource("VAADIN/images/jrebel_logo.png"));
    splash.addComponent(jrebelLogo);
    splash.setComponentAlignment(jrebelLogo, Alignment.MIDDLE_CENTER);

    Embedded ztLogo = new Embedded("", new ExternalResource("VAADIN/images/zt_logo.png"));
    splash.addComponent(ztLogo);
    splash.setComponentAlignment(ztLogo, Alignment.MIDDLE_CENTER);

    mainWindow.addComponent(splash);
  }

  private void buildLayout(AbsoluteLayout mainLayout) {
    Button alertButton = new Button("alert me");
    HorizontalSplitPanel panel = new HorizontalSplitPanel();
    panel.setSplitPosition(25, Sizeable.UNITS_PERCENTAGE);
    Table table = createTable();
    panel.addComponent(table);
    //
    initEmptyEditor(editor);
    wireComponents(table, editor, alertButton);

    VerticalSplitPanel right = new VerticalSplitPanel();
    right.setSplitPosition(60, Sizeable.UNITS_PERCENTAGE);

    // right.addComponent(new Label("Editor", Label.CONTENT_XHTML));
    right.addComponent(editor);

    Label console = new Label("", Label.CONTENT_XHTML);
    // Label console = new Label("", Label.CONTENT_PREFORMATTED);
    String logPath = System.getProperty("demo.logfile", DEMO_ROOT + "/logs/demo.log");
    console.setPropertyDataSource(new JRebelLogFileProperty(new File(logPath)));
    Panel consolePanel = new Panel("Server log:");
    consolePanel.addComponent(console);
    Label scrollToMe = new Label("", Label.CONTENT_XHTML);
    consolePanel.addComponent(scrollToMe);
    consolePanel.setSizeFull();
    consolePanel.setScrollable(true);
    consolePanel.setScrollTop(100000);
    right.addComponent(consolePanel);

    panel.addComponent(right);
    panel.setSizeFull();
    mainLayout.addComponent(panel);
    mainLayout.addComponent(alertButton, "top: 20px; right: 20px; ");
  }

  private void wireComponents(final Table table, final AceEditor editor, Button alertButton) {
    table.addListener(new ValueChangeListener() {
      public void valueChange(ValueChangeEvent event) {
        File selectedFile = (File) table.getValue();
        if (selectedFile == null) {
          return;
        }
        if (selectedFile.isDirectory()) {
          editor.setPropertyDataSource(DIRECTORY_LABEL);
        }
        else {
          editor.setPropertyDataSource(new TextFileProperty(selectedFile));
          if (selectedFile.getName().endsWith(".java")) {
            editor.setMode(AceMode.java);
          }
        }
      }
    });
    table.setImmediate(true);
    table.setSelectable(true);

    alertButton.addListener(new ClickListener() {
      public void buttonClick(ClickEvent event) {
        mainWindow.showNotification("Who's awesome? JRebel is awesome! :) ", Window.Notification.TYPE_ERROR_MESSAGE);
      }
    });
  }

  private Table createTable() {
    String path = DEMO_ROOT;
    FilesystemContainer docs = new FilesystemContainer(new File(path));
    Table table = new TreeTable(null, docs);
    table.setVisibleColumns(new String[] { "Name" });
    table.setSizeFull();
    return table;
  }

  private void initEmptyEditor(AceEditor editor) {
    editor.setValue("");
    editor.setMode(AceMode.html);
    editor.setSizeFull();
  }

}
