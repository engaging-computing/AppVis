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
    iconName = "https://raw.githubusercontent.com/farxinu/appinventor-sources/master/appinventor/appengine/src/com/google/appinventor/images/isense.png")
@SimpleObject(external = true)
@UsesPermissions(permissionNames = "android.permission.INTERNET,android.permission.ACCESS_NETWORK_STATE")
@UsesLibraries(libraries = "isense.jar")

public final class iSENSEPublisher extends AndroidNonvisibleComponent implements Component {

  public static final int VERSION = 1; 
  private static final String CONTRIBUTORNAME = "AppVis"; 
  private static final int QUEUEDEPTH = 30;

  private int ProjectID;
  private String ContributorKey;
  private String VisType;
  private String LiveURL = "http://isenseproject.org";
  private String DevURL = "http://dev.isenseproject.org";
  private boolean UseDev;
  private boolean newProjectID;
  private LinkedList<DataObject> pending; 
  private RProject project;
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
    newProjectID = true;
    if(UseDev) {
      api.useDev(UseDev);
    }
    project = api.getProject(ProjectID);
    pending = new LinkedList<DataObject>(); 
    activity = container.$context(); 
    numPending = 0;
  }

  // Block Properties
  // ProjectID
  @SimpleProperty(description = "iSENSE Project ID", category = PropertyCategory.BEHAVIOR)
    public int ProjectID() {
      return ProjectID;
    }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
    @SimpleProperty(description = "iSENSE Project ID", category = PropertyCategory.BEHAVIOR)
    public void ProjectID(int ProjectID) {
      this.ProjectID = ProjectID;
    }
  
  //ISense project name
  @SimpleProperty(description = "iSENSE Project Name", category = PropertyCategory.BEHAVIOR)
    public String ProjectName() {
      if(newProjectID) {
        project = api.getProject(ProjectID);
        newProjectID = false;
      }
      return project.name;
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

  // Get visualization url with controls for this project
  @SimpleFunction(description = "Gets URL for project visualization with controls onscreen.")
    public String GetVisWithControlsURL() {
      if (UseDev) {
        return DevURL + "/projects/" + ProjectID + "/data_sets?embed=true&vis=" + VisType;
      } else {
        return LiveURL + "/projects/" + ProjectID + "/data_sets?embed=true&vis=" + VisType;
      } 
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
