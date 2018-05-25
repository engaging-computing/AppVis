package edu.uml.cs.isense;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.Component; 
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent; 
import com.google.appinventor.components.runtime.ComponentContainer; 
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.PropertyCategory;


@DesignerComponent(version = iSENSEPublisher.VERSION,
    description = "A component that provides a high-level interface to iSENSEProject.org",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    //iconName = "images/extension.png")
    iconName = "https://raw.githubusercontent.com/codom/appinventor-sources/master/appinventor/appengine/src/com/google/appinventor/images/isense.png")
@SimpleObject(external = true)
@UsesPermissions(permissionNames = "android.permission.INTERNET,android.permission.ACCESS_NETWORK_STATE")
@UsesLibraries(libraries = "isense.jar")

public final class iSENSEVisType extends AndroidNonvisibleComponent implements Component {

  public static final int MAP_VIS = 0;
  public static final int TIMELINE_VIS = 1;
  public static final int SCATTER_VIS = 2;
  public static final int BAR_VIS = 3;
  public static final int HISTOGRAM_VIS = 4;
  public static final int BOX_VIS = 5;
  public static final int PIE_VIS = 6;
  public static final int TABLE_VIS = 7;
  public static final int SUMMARY_VIS = 8;
  public static final int PHOTOS_VIS = 9;

  public iSENSEVisType(ComponentContainer container) {
    super(container.$form());
  }

  //vis type constants
  @SimpleProperty(description = "VisType for map", category = PropertyCategory.BEHAVIOR)
    public int MAP_VIS() {
      return MAP_VIS;
    }

  @SimpleProperty(description = "VisType for the timeline", category = PropertyCategory.BEHAVIOR)
    public int TIMELINE_VIS() {
      return TIMELINE_VIS;
    }

  @SimpleProperty(description = "VisType for the scatter plot", category = PropertyCategory.BEHAVIOR)
    public int SCATTER_VIS() {
      return SCATTER_VIS;
    }

  @SimpleProperty(description = "VisType for bar graph", category = PropertyCategory.BEHAVIOR)
    public int BAR_VIS() {
      return BAR_VIS;
    }

  @SimpleProperty(description = "VisType for the histogram", category = PropertyCategory.BEHAVIOR)
    public int HISTOGRAM_VIS() {
      return HISTOGRAM_VIS;
    }

  @SimpleProperty(description = "VisType for the box", category = PropertyCategory.BEHAVIOR)
    public int BOX_VIS() {
      return BOX_VIS;
    }

  @SimpleProperty(description = "VisType for the pie chart", category = PropertyCategory.BEHAVIOR)
    public int PIE_VIS() {
      return PIE_VIS;
    }

  @SimpleProperty(description = "VisType for a table", category = PropertyCategory.BEHAVIOR)
    public int TABLE_VIS() {
      return TABLE_VIS;
    }

  @SimpleProperty(description = "VisType for a summary", category = PropertyCategory.BEHAVIOR)
    public int SUMMARY_VIS() {
      return SUMMARY_VIS;
    }

  @SimpleProperty(description = "VisType for photos", category = PropertyCategory.BEHAVIOR)
    public int PHOTOS_VIS() {
      return PHOTOS_VIS;
    }

}
