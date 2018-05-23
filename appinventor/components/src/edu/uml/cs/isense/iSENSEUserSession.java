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
  private RProject project;
  private RPerson session;
  private final API api;
  
  public iSENSEUserSession(ComponentContainer container) {

    super(container.$form());
    Log.i("iSENSE", "Starting? " + container.toString());
    this.api = API.getInstance();
    this.username = "";
    this.password = "";
  }

  //Block Properties
  //username
  @SimpleProperty(description = "iSENSE Username", category = PropertyCategory.BEHAVIOR)
    public String Username() {
      return username;
    }
  
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
    @SimpleProperty(description = "iSENSE Username", category = PropertyCategory.BEHAVIOR)
    public void Username(String username) {
      this.username = username;
    }

  //password
  @SimpleProperty(description = "iSENSE Password", category = PropertyCategory.BEHAVIOR)
    public String Password() {
      return password;
    }
  
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
    @SimpleProperty(description = "iSENSE Password", category = PropertyCategory.BEHAVIOR)
    public void Password(String password) {
      this.password = password;
    }

  //Block functions
  //login
  @SimpleFunction(description = "Log the user in with the current credentials")
    public void LogInUser() {
      session = api.createSession(username, password);
    }
   
  //logout
  @SimpleFunction(description = "Log the current user out")
    public void LogOutUser() {
      api.deleteSession();
    }
  
  //ListProjects
  @SimpleFunction(description = "Lists all of the projects owned by the current user")
    public YailList ListProjects() {

      YailList result = new YailList();
      
      ArrayList<RProject> rProjects = api.getProjects(1, 50, true, 1, username);
      //if the username is equal to the project's username, add it to the list
      for(RProject p : rProjects) {
        if(p.owner_name == username) {
          result.add(p);
        }
      }
      return result;
    }
 
  //GetProject
  @SimpleFunction(description = "Gets a list of project ids that have the same name as projectName")
    public void GetProjectByName(String projectName) {

      YailList result = new YailList();
      
      //TODO: find a way of querying all projects
      ArrayList<RProject> rProjects = api.getProjects(1, -1, true, 1, "");
      //if the username is equal to the project's username, add it to the list
      for(RProject p : rProjects) {
        if(p.name == projectName) {
          result.add(p);
        }
      }
    }
 
  //AddKeyToProject
  @SimpleFunction(description = "Adds a contributor key to the project provided")
    public void AddKeyToProject() {

    }
 
  //AddFieldToProject
  @SimpleFunction(description = "Adds a field to the project provided")
    public void AddFieldToProject() {

    }
 
  //RemoveKey
  @SimpleFunction(description = "Removes a contributor key")
    public void RemoveKey() {
      
    }
 
  //RemoveField
  @SimpleFunction(description = "Removes a data field")
    public void RemoveField() {

    }
 
  //CreateProject
  @SimpleFunction(description = "Creates a new project that is owned by the user")
    public void CreateProject() {

    }
 
  //DeleteProject
  @SimpleFunction(description = "Delete a project owned by the user")
    public void DeleteProject() {

    }
 
}
