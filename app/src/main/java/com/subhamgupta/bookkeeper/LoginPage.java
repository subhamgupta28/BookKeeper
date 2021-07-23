package com.subhamgupta.bookkeeper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoginPage extends AppCompatActivity {
    EditText etemail, etpass;
    Button btlogin, btcreate, btforgot;
    TextView btgsignin;
    TextView textView;
    MaterialCardView lockedcard;
    TextInputLayout tilemail;
    TextInputLayout tilpass;
    CoordinatorLayout relativeLayout;
    AnimationDrawable animationDrawable;
    FirebaseUser user;
    String api_key = "71180029614-i6cra1s4tmdoe9sun23lhku3ggafpcfn.apps.googleusercontent.com";
    boolean writePermissionGranted = false;
    boolean readPermissionGranted = false;
    MaterialCardView materialCardView;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    SharedSession sharedSession;
    int requestCode = 13;
    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        etemail = findViewById(R.id.tvemail);
        etpass = findViewById(R.id.tvpass);
        btcreate = findViewById(R.id.btcreate);
        btgsignin = findViewById(R.id.sign_in_button);
        btlogin = findViewById(R.id.btlogin);
        tilemail = findViewById(R.id.textemail);
        textView = findViewById(R.id.tv00);
        lockedcard = findViewById(R.id.lockedcard);
        btforgot = findViewById(R.id.forgotpass);
        relativeLayout = findViewById(R.id.relativecolor);
        materialCardView = findViewById(R.id.materialcardlog);
        linearLayout = findViewById(R.id.linearlog);
        progressBar = findViewById(R.id.logprogress);
        tilpass = findViewById(R.id.textpass);

//        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
//        animationDrawable.setEnterFadeDuration(2000);
//        animationDrawable.setExitFadeDuration(4000);
//        animationDrawable.start();


        sharedSession = new SharedSession(getApplicationContext());
        String uniqueID = UUID.randomUUID().toString();
        Log.e("uuid",uniqueID);
        sharedSession.setUuid(uniqueID);
        progressBar.setVisibility(View.GONE);


        


        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                showPermissionDialog();
            }
        }

        //materialCardView.setCardBackgroundColor(Color.TRANSPARENT);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser()!=null && !mAuth.getCurrentUser().isEmailVerified()){
            lockedcard.setVisibility(View.VISIBLE);
            etemail.setText(sharedSession.getEmail());
        }

        else
            lockedcard.setVisibility(View.GONE);
        btgsignin.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            buttonDisable();
            sharedSession.setData(etpass.getText().toString(), etemail.getText().toString(), "google");
            signInGoogle();


        });
        btlogin.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            buttonDisable();
            sharedSession.setData(etpass.getText().toString(), etemail.getText().toString(), "email");
            emailLogIn();

        });
        btcreate.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            buttonDisable();
            sharedSession.setData(etpass.getText().toString(), etemail.getText().toString(), "email");
            signInEmail();
        });
        etemail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (isValidEmail(editable.toString()))
                    tilemail.setError(null);
                else
                    tilemail.setError("Enter Valid Email");
            }
        });
        etpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty())
                    tilpass.setError(null);
                else
                    tilpass.setError("Password cannot be empty");
            }
        });


        btforgot.setOnClickListener(view -> {
            resetPassword(etemail.getText().toString());
        });

    }




    public void resetPassword(String email){
        if (!email.isEmpty()) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("LOGIN", "Email sent.");
                            Toast.makeText(this, "Password Change Email is sent to " + email, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("permission "+permissions+"\ngrant result "+ grantResults+"\nrequest code"+requestCode);
        if (requestCode == 13) {
            //requestPermission();
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission was granted. Now you can call your method to open camera, fetch contact or whatever

            } else {
                // Permission was denied.......
                // You can again ask for permission from here
                //ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                Toast.makeText(this, "Without this permission app may not work properly", Toast.LENGTH_SHORT).show();
            }

        }


    }
    public void showPermissionDialog(){

            MaterialAlertDialogBuilder madb = new MaterialAlertDialogBuilder(this);
            madb.setTitle("Allow Permission");
            madb.setMessage("Storage permission is needed for the better functionality of the app.");
            madb.setNegativeButton("Accept", (dialogInterface, i) -> d());
            madb.show();

    }
    public void d(){
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
    }


    private  boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task;
                task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.e("Gsign in", "Approved");
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Toast.makeText(this,"Sign in failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e("err",e.getMessage());
                    buttonEnable();
                    // ...
                }
            }

    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential;
        credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(getApplicationContext(), user.getEmail()+user.getDisplayName(), Toast.LENGTH_SHORT).show();

                        nextActivity();


                        //updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
                        //Log.e("Failed",task.getException().toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        //updateUI(null);
                        buttonEnable();
                    }


                });
    }
    public void nextActivity(){
        String email = etemail.getText().toString();
        user = mAuth.getCurrentUser();
        progressBar.setVisibility(View.INVISIBLE);
        //Log.e("user",user.toString());
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);

        finish();
        buttonEnable();
    }
    public void emailLogIn(){
        String email = etemail.getText().toString(), pass = etpass.getText().toString();
        if (email.isEmpty() || pass.isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter Credentials", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            buttonEnable();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()){


                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    if(!user.isEmailVerified()){
                        user.sendEmailVerification();
                        buttonEnable();
                        Toast.makeText(getApplicationContext(), "Verification link is sent to "+email+" verify to login",Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Login Successful",Toast.LENGTH_LONG).show();

                        nextActivity();

                    }



                }
                else {
                    Toast.makeText(this, "Wrong email or password", Toast.LENGTH_LONG).show();
                    buttonEnable();
                }


            }).addOnFailureListener(e -> {
                Log.e("onfail", e.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
                buttonEnable();
                //tilpass.setError("Wrong Password or Email");

                //Toast.makeText(getApplicationContext(), "No Account Attached With This Email", Toast.LENGTH_LONG).show();
            });
        }
    }
    public void signInEmail(){
        String email = etemail.getText().toString(), pass = etpass.getText().toString();
        if (email.isEmpty() || pass.isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter Credentials", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            buttonEnable();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, task -> {
                if (task.isComplete()){
                    Toast.makeText(getApplicationContext(), "Succecfully Account Created", Toast.LENGTH_LONG).show();
                    sharedSession.setEmail(email);
                    emailLogIn();

                }
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                    buttonEnable();
                    Toast.makeText(getApplicationContext(), "Something Went Wrong..", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void buttonEnable(){
        btcreate.setEnabled(true);
        btlogin.setEnabled(true);
        btgsignin.setEnabled(true);
        btforgot.setEnabled(true);
    }
    public void buttonDisable(){
        btlogin.setEnabled(false);
        btgsignin.setEnabled(false);
        btcreate.setEnabled(false);
        btforgot.setEnabled(false);
    }
}