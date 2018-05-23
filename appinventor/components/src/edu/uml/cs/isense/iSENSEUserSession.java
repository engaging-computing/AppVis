package edu.uml.cs.isense; 

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList; 
import java.io.File; 
import java.net.URL; 

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity; 
import android.util.Log;
import android.os.Handler;
import android.content.Context; 
import android.net.ConnectivityManager; 
import android.net.NetworkInfo; 
import android.os.AsyncTask; 
import android.net.Uri; 

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.YailList;
import com.google.appinventor.components.runtime.Component; 
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent; 
import com.google.appinventor.components.runtime.ComponentContainer; 
import com.google.appinventor.components.runtime.EventDispatcher; 

import edu.uml.cs.isense.api.API;
import edu.uml.cs.isense.api.UploadInfo;
import edu.uml.cs.isense.objects.RDataSet;
import edu.uml.cs.isense.objects.RPerson;
import edu.uml.cs.isense.objects.RProjectField;
import edu.uml.cs.isense.objects.RProject;


@DesignerComponent(version = iSENSEUserSession.VERSION,
    description = "A component that provides a high-level interface to iSENSEProject.org",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    //iconName = "images/extension.png")
    iconName = "https://raw.githubusercontent.com/codom/appinventor-sources/master/appinventor/appengine/src/com/google/appinventor/images/isense.png")
@SimpleObject(external = true)
@UsesPermissions(permissionNames = "android.permission.INTERNET,android.permission.ACCESS_NETWORK_STATE")
@UsesLibraries(libraries = "isense.jar")

public final class iSENSEUserSession extends AndroidNonvisibleComponent implements Component {

  public static final int VERSION = 1; 

  private String username;
  private String password;
  private final API api;
  
  public iSENSEUserSession(ComponentContainer container) {

    super(container.$form());
    Log.i("iSENSE", "Starting? " + container.toString());
    this.api = API.getInstance();
    this.username = "";
    this.password = "";
  }

}
