package edu.uml.cs.isense;

@DefaultMessage("default")  ￼
 @Description("Use the default vis defined on the iSense project");￼
 String defaultISenseChoice();

@DefaultMessage("Map")  ￼
 @Description("Use the Map vis");￼
 String mapISenseChoice();

@DefaultMessage("Timeline")  ￼
 @Description("Use the Timeline");￼
 String timelineISenseChoice();

@DefaultMessage("Scatter")  ￼
 @Description("Use the Scatter");￼
 String scatterISenseChoice();

@DefaultMessage("Bar")  ￼
 @Description("Use the Bar");￼
 String barISenseChoice();

@DefaultMessage("Histogram")  ￼
 @Description("Use the Histogram");￼
 String histogramISenseChoice();

@DefaultMessage("Box")  ￼
 @Description("Use the Box");￼
 String boxISenseChoice();

@DefaultMessage("Pie")  ￼
 @Description("Use the Pie");￼
 String pieISenseChoice();

@DefaultMessage("Table")  ￼
 @Description("Use the Table");￼
 String tableISenseChoice();

@DefaultMessage("Summary")  ￼
 @Description("Use the Summary");￼
 String summaryISenseChoice();

@DefaultMessage("Photos")  ￼
 @Description("Use the Photos");￼
 String photosISenseChoice();

import com.google.appinventor.client.Ode.MESSAGES;
import com.google.appinventor.client.widgets.properties.ChoicePropertyEditor;

public class ISenseVisPropertyEditor{
  
