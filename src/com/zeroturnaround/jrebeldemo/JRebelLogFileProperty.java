package com.zeroturnaround.jrebeldemo;

import java.io.File;

import com.vaadin.data.util.TextFileProperty;

public class JRebelLogFileProperty extends TextFileProperty{

  public JRebelLogFileProperty(File file) {
    super(file);
  }
  
  @Override
  public Object getValue() {
    String value = (String) super.getValue();
    value = prettify(value);
    return value;
  }

  private String prettify(String value) {
    value = value.replaceAll(" JRebel", "<font size=\"3\" color=\"#8AB31D\"><b> JRebel</b></font>");
    value = value.replaceAll("-Spring", "<font size=\"3\" color=\"#9DC631\"><b>-Spring</b></font>");
    value = value.replaceAll("\n", "<br/>");
    return value;
  }

}
