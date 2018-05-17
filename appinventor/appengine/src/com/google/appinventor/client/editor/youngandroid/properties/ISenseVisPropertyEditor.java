package com.google.appinventor.client.editor.youngandroid.properties;

import static com.google.appinventor.client.Ode.MESSAGES;
import com.google.appinventor.client.widgets.properties.ChoicePropertyEditor;

/**
 * Property editor for appvis vis types
 *
 * @author Christopher Odom christopher.r.odom@gmail.com
 */

public class ISenseVisPropertyEditor extends ChoicePropertyEditor{

  private static final Choice[] vis = new Choice[]{
    new Choice(MESSAGES.defaultISenseChoice(), "0"),
    new Choice(MESSAGES.mapISenseChoice(), "1"),
    new Choice(MESSAGES.timelineISenseChoice(), "2"),
    new Choice(MESSAGES.scatterISenseChoice(), "3"),
    new Choice(MESSAGES.barISenseChoice(), "4"),
    new Choice(MESSAGES.histogramISenseChoice(), "4"),
    new Choice(MESSAGES.boxISenseChoice(), "6"),
    new Choice(MESSAGES.pieISenseChoice(), "7"),
    new Choice(MESSAGES.tableISenseChoice(), "8"),
    new Choice(MESSAGES.summaryISenseChoice(), "9"),
    new Choice(MESSAGES.photosISenseChoice(), "10"),
  };

  public ISenseVisPropertyEditor(){
    super(vis);
  }
}
  
