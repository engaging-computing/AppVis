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


@DesignerComponent(version = iSENSEPublisher.VERSION,
    description = "A component that provides a high-level interface to iSENSEProject.org",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    //iconName = "images/extension.png")
    iconName = "https://raw.githubusercontent.com/codom/appinventor-sources/master/appinventor/appengine/src/com/google/appinventor/images/isense.png")
@SimpleObject(external = true)
@UsesPermissions(permissionNames = "android.permission.INTERNET,android.permission.ACCESS_NETWORK_STATE")
@UsesLibraries(libraries = "isense.jar")

public final class iSENSEPublisher extends AndroidNonvisibleComponent implements Component {

  public static final int VERSION = 1; 
  private static final String CONTRIBUTORNAME = "AppVis"; 
  private static final int QUEUEDEPTH = 30;

  protected static final int MAP_VIS = 0;
  protected static final int TIMELINE_VIS = 1;
  protected static final int SCATTER_VIS = 2;
  protected static final int BAR_VIS = 3;
  protected static final int HISTOGRAM_VIS = 4;
  protected static final int BOX_VIS = 5;
  protected static final int PIE_VIS = 6;
  protected static final int TABLE_VIS = 7;
  protected static final int SUMMARY_VIS = 8;
  protected static final int PHOTOS_VIS = 9;

  private int ProjectID;
  private String ContributorKey;
  private String VisType;
  private String LiveURL = "http://isenseproject.org";
  private String DevURL = "http://dev.isenseproject.org";
  private boolean UseDev;
  private LinkedList<DataObject> pending; 
  private RProject project;
  private ArrayList <RProjectField> fields;
  private final API api;
  private static Activity activity; 
  private int numPending;

  public iSENSEPublisher(ComponentContainer container) {
    super(container.$form());
    Log.i("iSENSE", "Starting? " + container.toString());
    api = API.getInstance();
    ProjectID(-1); 
    ContributorKey(""); 
    VisType("");
    UseDev = false;
    if(UseDev) {
      api.useDev(UseDev);
    }
    project = api.getProject(ProjectID);
    fields = api.getProjectFields(ProjectID);
    pending = new LinkedList<DataObject>(); 
    activity = container.$context(); 
    numPending = 0;
  }

  // Block Properties
  //vis type constants
  @SimpleProperty(description = "VisType for map", category = PropertyCategory.BEHAVIOR)
    public int Map() {
      return MAP_VIS;
    }

  @SimpleProperty(description = "VisType for the timeline", category = PropertyCategory.BEHAVIOR)
    public int Timeline() {
      return TIMELINE_VIS;
    }

  @SimpleProperty(description = "VisType for the scatter plot", category = PropertyCategory.BEHAVIOR)
    public int Scatter() {
      return SCATTER_VIS;
    }

  @SimpleProperty(description = "VisType for bar graph", category = PropertyCategory.BEHAVIOR)
    public int Bar() {
      return BAR_VIS;
    }

  @SimpleProperty(description = "VisType for the histogram", category = PropertyCategory.BEHAVIOR)
    public int Histogram() {
      return HISTOGRAM_VIS;
    }

  @SimpleProperty(description = "VisType for the box", category = PropertyCategory.BEHAVIOR)
    public int Box() {
      return BOX_VIS;
    }

  @SimpleProperty(description = "VisType for the pie chart", category = PropertyCategory.BEHAVIOR)
    public int Pie() {
      return PIE_VIS;
    }

  @SimpleProperty(description = "VisType for a table", category = PropertyCategory.BEHAVIOR)
    public int Table() {
      return TABLE_VIS;
    }

  @SimpleProperty(description = "VisType for a summary", category = PropertyCategory.BEHAVIOR)
    public int Summary() {
      return SUMMARY_VIS;
    }

  @SimpleProperty(description = "VisType for photos", category = PropertyCategory.BEHAVIOR)
    public int Photos() {
      return PHOTOS_VIS;
    }
  // ProjectID
  @SimpleProperty(description = "iSENSE Project ID", category = PropertyCategory.BEHAVIOR)
    public int ProjectID() {
      return ProjectID;
    }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
    @SimpleProperty(description = "iSENSE Project ID", category = PropertyCategory.BEHAVIOR)
    public void ProjectID(int ProjectID) {
      this.ProjectID = ProjectID;
      //TODO: Thread this 
      this.project = api.getProject(ProjectID);
    }

    /*
  //ISense get fields list
  @SimpleFunction(description = "Get the fields in the projects as a list")
    public String GetFieldsList() {
      String retFields = "";
      return retFields;
    }
    */
  
  //ISense project name
  @SimpleProperty(description = "iSENSE Project Name", category = PropertyCategory.BEHAVIOR)
    public String ProjectName() {
      return project.name;
    }
  
  //ISense like count
  @SimpleProperty(description = "iSENSE Project Like Count", category = PropertyCategory.BEHAVIOR)
    public int ProjectLikeCount() {
      return project.like_count;
    }

  //ISense project author
  @SimpleProperty(description = "iSENSE Project Author", category = PropertyCategory.BEHAVIOR)
    public String ProjectAuthor() {
      return project.owner_name;
    }

  //ISense project creation date
  @SimpleProperty(description = "iSENSE Project Creation Date", category = PropertyCategory.BEHAVIOR)
    public String ProjectDateCreated() {
      return project.timecreated;
    }
 
  //ISense project owner url
  @SimpleProperty(description = "iSENSE Project Account URL", category = PropertyCategory.BEHAVIOR)
    public String ProjectOwnerURL() {
      return project.owner_url;
    }
  
  //ISense project isHidden?
  @SimpleProperty(description = "iSENSE Project isHidden. Returns true if hidden, flase if visible", category = PropertyCategory.BEHAVIOR)
    public boolean ProjecctIsHidden() {
      return project.hidden;
    }
 
  //ISense project isFeatured?
  @SimpleProperty(description = "iSENSE Project isFeatured. Returns true if featured, flase if normal", category = PropertyCategory.BEHAVIOR)
    public boolean ProjecctIsFeatured() {
      return project.featured;
    }
 
  // Contributor Key
  @SimpleProperty(description = "iSENSE Contributor Key", category = PropertyCategory.BEHAVIOR)
    public String ContributorKey() {
      return ContributorKey;
    }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
    @SimpleProperty(description = "iSENSE Contributor Key", category = PropertyCategory.BEHAVIOR)
    public void ContributorKey(String ContributorKey) {
      this.ContributorKey = ContributorKey;
    }

    // Vis Type
  @SimpleProperty(description = "Visualization Type", category = PropertyCategory.BEHAVIOR)
    public String VisType() {
      return VisType;
    }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
    @SimpleProperty(description = "Visualization Type", category = PropertyCategory.BEHAVIOR)
    public void VisType(String VisType) {
      this.VisType = VisType;
    }

  // Block Functions
  // Upload Data Set in Background
  @SimpleFunction(description = "Upload Data Set to iSENSE")
    public void UploadDataSet(final String DataSetName, final YailList Fields, final YailList Data) {
      // Create new "DataObject" and add to upload queue
      DataObject dob = new DataObject(DataSetName, Fields, Data);
      if (pending.size() >= QUEUEDEPTH) {
        UploadDataSetFailed();
        return;
      }
      pending.add(dob);
      numPending++;  
      new UploadTask().execute(); 
    }

  // Upload Dataset With Photo
  @SimpleFunction(description = "Uploads a dataset and a photo")
    public void UploadDataSetWithPhoto(final String DataSetName, final YailList Fields, final YailList Data, final String Photo) {

      if (pending.size() >= QUEUEDEPTH) {
        UploadDataSetFailed();
        return;
      }
      // Validate photo
      String path = ""; 
      String[] pathtokens = Photo.split("/"); 
      // If camera photo 
      if (pathtokens[0].equals("file:")) {
        try {
          path = new File(new URL(Photo).toURI()).getAbsolutePath(); 
        } catch (Exception e) {
          Log.e("iSENSE", "Malformed URL or URI!"); 
          UploadDataSetFailed(); 
          return;
        }
      } else { // Assets photo
        path = "/sdcard/AppInventor/assets/" + Photo; 
      }

      // Ensure photo exists 
      File pic = new File(path); 
      if (!pic.exists()) {
        Log.e("iSENSE", "picture does not exist!"); 
        UploadDataSetFailed(); 
        return;
      }

      // Create new "DataObject" and add it to the upload queue
      DataObject dob = new DataObject(DataSetName, Fields, Data, path); 
      pending.add(dob); 
      numPending++;
      new UploadTask().execute(); 
    }

    // Append to existing data set
  @SimpleFunction(description = "Append new row of data to existing data set.")
    public void AppendToDataSet(final int DataSetID, final YailList Fields, final YailList Data) {
      // Create new "DataObject" and add to upload queue
      DataObject dob = new DataObject(DataSetID, Fields, Data);
      if (pending.size() >= QUEUEDEPTH) {
        UploadDataSetFailed();
        return;
      }
      pending.add(dob);
      numPending++;  
      new UploadTask().execute(); 
    } 

  // Private class that gives us a data structure with info for uploading a dataset
  class DataObject {

    String name; 
    YailList fields; 
    YailList data; 
    String path; 
    int datasetid;

    // Normal dataset 
    DataObject(String name, YailList fields, YailList data) {
      this.name = name; 
      this.fields = fields;
      this.data = data; 
      this.path = ""; 
      this.datasetid = -1;
    }

    // Dataset with photo
    DataObject(String name, YailList fields, YailList data, String path) {
      this.name = name; 
      this.fields = fields;
      this.data = data; 
      this.path = path; 
      this.datasetid = -1;
    }

    // Append to existing data setReadable
    DataObject(int datasetid, YailList fields, YailList data) {
      this.name = "";
      this.fields = fields;
      this.data = data;
      this.path = "";
      this.datasetid = datasetid;
    }
  }

  // Private asynchronous task class that allows background uploads
  private class UploadTask extends AsyncTask<Void, Void, Integer> {

    // This is what actually runs in the background thread, so it's safe to block
    protected Integer doInBackground(Void... v) {

      DataObject dob = pending.remove(); 
      // ensure that the lists are the same size 
      if (dob.fields.size() != dob.data.size()) {
        Log.e("iSENSE", "Input lists are not the same size!"); 
        return -1; 
      } 

      // A simple throttle if too much data is being thrown at the upload queue 
      if (pending.size() > QUEUEDEPTH) {
        Log.e("iSENSE", "Too many items in upload queue!"); 
        return -1;  
      }

      // Sleep while we don't have a wifi connection or a mobile connection
      ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE); 

      boolean wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected(); 
      boolean mobi = false; 

      if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
        mobi = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected(); 
      }

      while (!(wifi||mobi)) {
        try {
          Log.i("iSENSE", "No internet connection; sleeping for one second"); 
          Thread.sleep(1000); 
        } catch (InterruptedException e) {
          Log.e("iSENSE", "Thread Interrupted!"); 
        }
        wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected(); 
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) { 
          mobi = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected(); 
        }
      } 

      // Active internet connection detected; proceed with upload 
      UploadInfo uInfo = new UploadInfo(); 

      // Get fields from project
      ArrayList<RProjectField> projectFields = api.getProjectFields(ProjectID);
      JSONObject jData = new JSONObject();
      for (int i = 0; i < dob.fields.size(); i++) {
        for (int j = 0; j < projectFields.size(); j++) {
          if (dob.fields.get(i + 1).equals(projectFields.get(j).name)) {
            try {
              String sdata = dob.data.get(i + 1).toString();
              jData.put("" + projectFields.get(j).field_id, new JSONArray().put(sdata));
            } catch (JSONException e) {
              UploadDataSetFailed();
              e.printStackTrace();
              return -1;
            }
          }
        }
      }

      int dataSetId = -1;
      // are we uploading a new data set?
      if (!dob.name.equals("")) {
        // login with contributor key
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss aaa");
        String date = " - " + sdf.format(cal.getTime()).toString();
        uInfo = api.uploadDataSet(ProjectID, jData, dob.name + date, ContributorKey, CONTRIBUTORNAME); 

        dataSetId = uInfo.dataSetId; 
        Log.i("iSENSE", "JSON Upload: " + jData.toString()); 
        Log.i("iSENSE", "Dataset ID: " + dataSetId); 
        if (dataSetId == -1) {
          Log.e("iSENSE", "Append failed! Check your contributor key and project ID."); 
          return -1; 
        }
      }

      // are we appending to existing data set?
      if (dob.datasetid != -1) {
        uInfo = api.appendDataSetData(dob.datasetid, jData, ContributorKey);
        dataSetId = uInfo.dataSetId; 
        Log.i("iSENSE", "JSON Upload: " + jData.toString()); 
        Log.i("iSENSE", "Dataset ID: " + dataSetId); 
        if (dataSetId == -1) {
          Log.e("iSENSE", "Append failed! Check your contributor key and project ID."); 
          return -1; 
        }
      }
    

      // do we have a photo to upload? 
      if (!dob.path.equals("")) {
        File pic = new File(dob.path); 
        pic.setReadable(true);
        Log.i("iSENSE", "Trying to upload: " + dob.path); 
        uInfo = api.uploadMedia(dataSetId, pic, API.TargetType.DATA_SET, ContributorKey, CONTRIBUTORNAME);
        int mediaID = uInfo.mediaId;
        Log.i("iSENSE", "MediaID: " + mediaID);
        if (mediaID == -1) {
          Log.e("iSENSE", "Media upload failed. Is it a valid picture?"); 
          return -1; 
        } 
      }
      return dataSetId; 
    } 

    // After background thread execution, UI handler runs this 
    protected void onPostExecute(Integer result) {
      numPending--;
      if (result == -1) {
        UploadDataSetFailed(); 
      } else {
        UploadDataSetSucceeded(result); 
      }
    }
  }

  // Get Dataset By Field
  @SimpleFunction(description = "Get the Data Sets for the current project")
    public YailList GetDataSetsByField(final String Field) {
      ArrayList<String> result = api.getDataSetsByField(ProjectID, Field);
      return YailList.makeList(result); 
    }

  // Get Time (formatted for iSENSE Upload)
  @SimpleFunction(description = "Gets the current time. It is formatted correctly for iSENSE")
    public String GetTime() {
      Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      return sdf.format(cal.getTime()).toString();
    }

  // Get Number of Pending Uploads (Advanced Feature)
  @SimpleFunction(description = "Gets number of pending background uploads. Advanced feature.")
    public int GetNumberPendingUploads() {
      return numPending; 
    }

  // Get visualization url for this project
  @SimpleFunction(description = "Gets URL for project visualization in simple fullscreen format.")
    public String GetVisURL() {
      if (UseDev) {
        return DevURL + "/projects/" + ProjectID + "/data_sets?presentation=true&vis=" + VisType; 
      } else {
        return LiveURL + "/projects/" + ProjectID + "/data_sets?presentation=true&vis=" + VisType;
      }
    }

  @SimpleFunction(description = "Gets URL for project visualization in simple fullscreen format with an overloaded vistype")
    public String GetCustomVisURL(int VisType) {
      String url;
      if (UseDev) {
        url = DevURL + "/projects/" + ProjectID + "/data_sets?presentation=true&vis=";
      } else {
        url = LiveURL + "/projects/" + ProjectID + "/data_sets?presentation=true&vis=";
      }
      switch(VisType){
        case MAP_VIS:
          url += "Map";
          break;
        case TIMELINE_VIS:
          url += "Timeline";
          break;
        case SCATTER_VIS:
          url += "Scatter";
          break;
        case BAR_VIS:
          url += "Bar";
          break;
        case HISTOGRAM_VIS:
          url += "Histogram";
          break;
        case BOX_VIS:
          url += "Box";
          break;
        case PIE_VIS:
          url += "Pie";
          break;
        case TABLE_VIS:
          url += "Table";
          break;
        case SUMMARY_VIS:
          url += "Summary";
          break;
        case PHOTOS_VIS:
          url += "Photos";
          break;
        default: break;
      }
      return url;
    }

  // Get visualization url with controls for this project
  @SimpleFunction(description = "Gets URL for project visualization with controls onscreen.")
    public String GetVisWithControlsURL() {
      if (UseDev) {
        return DevURL + "/projects/" + ProjectID + "/data_sets?embed=true&vis=" + VisType;
      } else {
        return LiveURL + "/projects/" + ProjectID + "/data_sets?embed=true&vis=" + VisType;
      } 
    }

  // Get visualization url with controls for this project
  @SimpleFunction(description = "Gets URL for project visualization with controls onscreen.")
    public String GetCustomVisWithControlsURL(int VisType) {
      String url;
      if (UseDev) {
        url = DevURL + "/projects/" + ProjectID + "/data_sets?embed=true&vis=";
      } else {
        url = LiveURL + "/projects/" + ProjectID + "/data_sets?embed=true&vis=";
      } 
      switch(VisType){
        case MAP_VIS:
          url += "Map";
          break;
        case TIMELINE_VIS:
          url += "Timeline";
          break;
        case SCATTER_VIS:
          url += "Scatter";
          break;
        case BAR_VIS:
          url += "Bar";
          break;
        case HISTOGRAM_VIS:
          url += "Histogram";
          break;
        case BOX_VIS:
          url += "Box";
          break;
        case PIE_VIS:
          url += "Pie";
          break;
        case TABLE_VIS:
          url += "Table";
          break;
        case SUMMARY_VIS:
          url += "Summary";
          break;
        case PHOTOS_VIS:
          url += "Photos";
          break;
        default: break;
      }
      return url;
    }

  @SimpleEvent(description = "iSENSE Upload Data Set Succeeded")
    public void UploadDataSetSucceeded(int DataSetID) {
      EventDispatcher.dispatchEvent(this, "UploadDataSetSucceeded", DataSetID);
    }

  @SimpleEvent(description = "iSENSE Upload Data Set Failed")
    public void UploadDataSetFailed() {
      EventDispatcher.dispatchEvent(this, "UploadDataSetFailed");
    }

  
}
