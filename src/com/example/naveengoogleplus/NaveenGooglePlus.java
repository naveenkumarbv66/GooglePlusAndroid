package com.example.naveengoogleplus;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;
import com.google.android.gms.plus.model.people.PersonBuffer;

public class NaveenGooglePlus extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<LoadPeopleResult>, OnClickListener {
	/* Request code used to invoke sign in user interactions. */
	  private static final int RC_SIGN_IN = 0;

	  /* Client used to interact with Google APIs. */
	  private GoogleApiClient mGoogleApiClient;

	  /* A flag indicating that a PendingIntent is in progress and prevents
	   * us from starting further intents.
	   */
	  private boolean mIntentInProgress;
	  private boolean mSignInClicked;
	  
	  TextView UserMEssage;

	  /* Store the connection result from onConnectionFailed callbacks so that we can
	   * resolve them when the user clicks sign-in.
	   */
	  private ConnectionResult mConnectionResult;
	  
	  /* A helper method to resolve the current ConnectionResult error. */
	  private void resolveSignInError() {
		  if (mConnectionResult.hasResolution()) {
	      try {
	        mIntentInProgress = true;
	        mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
	      } catch (SendIntentException e) {
	        // The intent was canceled before it was sent.  Return to the default
	        // state and attempt to connect to get an updated ConnectionResult.
	        mIntentInProgress = false;
	        mGoogleApiClient.connect();
	      }
	    }
	  }

	  public void onConnectionFailed(ConnectionResult result) {
	    if (!mIntentInProgress) {
	      // Store the ConnectionResult so that we can use it later when the user clicks
	      // 'sign-in'.
	      mConnectionResult = result;

	      if (mSignInClicked) {
	        // The user has already clicked 'sign-in' so we attempt to resolve all
	        // errors until the user is signed in, or they cancel.
	        resolveSignInError();
	      }
	    }
	  }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.sign_in_button).setOnClickListener(this);
		
		UserMEssage=(TextView)findViewById(R.id.textView1);
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this).addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}
	
	 protected void onStart() {
		    super.onStart();
		    mGoogleApiClient.connect();
		  }
	 protected void onStop() {
		    super.onStop();

		    if (mGoogleApiClient.isConnected()) {
		      mGoogleApiClient.disconnect();
		    }
	}
	 
	 public void onConnectionSuspended(int cause) {
		  mGoogleApiClient.connect();
		}

		public void onConnected(Bundle connectionHint) {
		  // We've resolved any connection errors.  mGoogleApiClient can be used to
		  // access Google APIs on behalf of the user.
			 mSignInClicked = false;
			//  Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
			// Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
			 if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				    Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				    String personName = currentPerson.getNickname();
				    Image personPhoto = currentPerson.getImage();
				    String personGooglePlusProfile = currentPerson.getUrl();
				    String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				    UserMEssage.setText("U have logged in with "+currentPerson.getDisplayName());
				    Log.d("Naveen", "Name "+personName);
				    Log.d("Naveen", "url "+personGooglePlusProfile);
				    Log.d("Naveen", "email "+email);
				    Log.d("Naveen", "ID "+currentPerson.getId());
				    Log.d("Naveen", "Full Name "+currentPerson.getDisplayName());
				    
				    
				    /*if (mGoogleApiClient.isConnected()) {
						 Log.d("Naveen", "Logged Out");
						 UserMEssage.setText("You have been loged out.");
					      Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
					      mGoogleApiClient.disconnect();
					      mGoogleApiClient.connect();
					 }*/
				    
				  }
		}
		protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
			  if (requestCode == RC_SIGN_IN) {
			    mIntentInProgress = false;

			    if (!mGoogleApiClient.isConnecting()) {
			      mGoogleApiClient.connect();
			    }
			  }
			}
		
		

		@Override
		public void onResult(LoadPeopleResult peopleData) {
			// TODO Auto-generated method stub
			if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
			    PersonBuffer personBuffer = peopleData.getPersonBuffer();
			    try {
			      int count = personBuffer.getCount();
			      for (int i = 0; i < count; i++) {
			        Log.d("Naveen", "Display name: " + personBuffer.get(i).getDisplayName());
			        UserMEssage.setText("Display name: " + personBuffer.get(i).getDisplayName());
			      }
			    } finally {
			      personBuffer.close();
			    }
			  } else {
			    Log.e("Naveen", "Error requesting visible circles: " + peopleData.getStatus());
			  }
			
		}
	

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			 if (mGoogleApiClient.isConnected()) {
				 Log.d("Naveen", "Logged Out");
				 UserMEssage.setText( "Logged Out");
			      Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			      mGoogleApiClient.disconnect();
			      mGoogleApiClient.connect();
			 }else 	 
			if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) {
				Log.d("Naveen", "Logged In");
				 UserMEssage.setText( "Logged In");
				    mSignInClicked = true;
				    resolveSignInError();
			}
		}

}
