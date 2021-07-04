package com.subhamgupta.bookkeeper;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText btitle, bauthor;
    MaterialButton btnimage, btnshow, btnupload;
    ImageView setBookImg;
    ProgressBar progressBar;
    TextView imgtext;
    private ViewPager viewPager2;
    private TabLayout tabLayout;
    TextInputLayout textinputtitle, textinputauthor;
    private MyBooksFragment myBooksFragment;
    private ExploreFrangments exploreFrangments;
    private FavouritesBookFragment favouritesBookFragment;
    long myc = 0, exc = 0;
    private FirebaseAuth mAuth;
    private BottomAppBar bottomAppBar;
    private SharedSession ss;
    MaterialAlertDialogBuilder logoutpopup;
    private GoogleSignInAccount mGoogleSignInAccount;
    FloatingActionButton floatingaddbook;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    static StorageReference storageRef;
    static String title , author,bookref ;


    private final int REQUEST_CODE = 1;
    static UploadTask uploadTask ;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mReference = mDatabase.getReference("BOOKDATA").child(Objects.requireNonNull(mAuth.getUid()));
        storageRef = FirebaseStorage.getInstance().getReference();
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        bottomAppBar = findViewById(R.id.bottombar);
        setSupportActionBar(bottomAppBar);
        floatingaddbook = findViewById(R.id.floatingbtn);
        //Log.e("user",mAuth.getUid());
        viewPager2 = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);


        exploreFrangments = new ExploreFrangments();
        myBooksFragment = new MyBooksFragment();
        favouritesBookFragment = new FavouritesBookFragment();

        tabLayout.setupWithViewPager(viewPager2);
        MainActivity.ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        //viewPagerAdapter.addFragments(favouritesBookFragment, "current");
        viewPagerAdapter.addFragments(myBooksFragment, "My books");
        viewPagerAdapter.addFragments(exploreFrangments, "Explore");
        viewPager2.setAdapter(viewPagerAdapter);

        bottomAppBar.setOnMenuItemClickListener(item -> {
            Log.e("item",item.toString());
            return false;
        });


        floatingaddbook.setOnClickListener(view -> Show());
        bottomAppBar.setNavigationOnClickListener(view -> {
            logoutPop();
            /*mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginPage.class));
            finish();*/
        });
        viewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                Log.e("fragment", String.valueOf(position));
                pos = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ss = new SharedSession(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ss.isDemoMain())
                    showdemo();
            }
        },3000);


    }
    public void showdemo(){
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(floatingaddbook, "Add Books From Here", "You can choose any image for the book!")
                        // All options below are optional
                        .outerCircleColor(R.color.colorRed)  // Specify a color for the outer circle
                        //.outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.colorSecondary)  // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        //.titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                       // .descriptionTextColor(R.color.colorRed)  // Specify the color of the description text
                        .textColor(R.color.colorOnSecondary)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.colorPrimary)         // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(false)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        //.icon(Drawable)                     // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        //doSomething();
                        Show();
                        ss.setDemoMain(false);
                    }
                });



    }
    public void logoutPop(){
        logoutpopup = new MaterialAlertDialogBuilder(this);


        logoutpopup.setTitle("Warning");
        logoutpopup.setMessage("Backup all the books before logging out");
        logoutpopup.setNegativeButton("Logout", (dialogInterface, d) -> {
            mAuth.signOut();
            ss.logOut();
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/BookKeeper/");

            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    Log.e("files", children[i]);
                    new File(file, children[i]).delete();
                }
            }
            startActivity(new Intent(getApplicationContext(), LoginPage.class));
            finish();
        });
        logoutpopup.setPositiveButton("Cancel", (dialogInterface, i) -> {

        });
        logoutpopup.show();
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentstitle = new ArrayList<>();
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            Log.e("frag",fm.toString());
        }
        public void addFragments(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentstitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.e("getitem", String.valueOf(position));
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            Log.e("getpagetitle",fragmentstitle.get(position));
            return fragmentstitle.get(position);
        }
    }

    public void addBook(){
        Intent intent = new Intent();
        //intent.setType("image/*");
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image Here..."), REQUEST_CODE);

    }
    @Override
    public void onActivityResult(int requestcode, int resultcode, @Nullable Intent data){
        super.onActivityResult(requestcode, resultcode, data);
        if (requestcode == REQUEST_CODE && resultcode == RESULT_OK && data != null && data.getData() != null){
           //file ready to upload
            btnimage.setEnabled(true);
            btnupload.setEnabled(true);
            setBookImg.setImageURI(data.getData());

            btnupload.setOnClickListener(view -> uploadingfile(data));

        }
        else {
            Toast.makeText(getApplicationContext(), "No file choosen", Toast.LENGTH_SHORT).show();

        }
    }
    public void uploadingfile(@Nullable Intent data){

        //progressDialog.show();
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        Toast.makeText(getApplicationContext(), "Please wait for book uploading", Toast.LENGTH_LONG).show();

        String filename = getFileName(data.getData());
        StorageReference imagesRef = storageRef.child(filename);
        Uri file = data.getData();
        imagesRef.child("file/" + file.getLastPathSegment());
        View contextView = findViewById(android.R.id.content);
        uploadTask = imagesRef.putFile(file);
        uploadTask.addOnFailureListener(exception -> {

            progressBar.setVisibility(View.GONE);
            btnupload.setActivated(false);
            btnimage.setActivated(true);
            setBookImg.setImageResource(R.drawable.icon);
            Snackbar.make(contextView, exception.getMessage(), Snackbar.LENGTH_LONG)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(floatingaddbook)
                    .show();
        }).addOnSuccessListener(taskSnapshot -> {

            progressBar.setVisibility(View.GONE);
            btnupload.setEnabled(false);
            btnimage.setEnabled(true);

            setBookImg.setImageResource(R.drawable.icon);
            getFileUrl(filename);
            Snackbar.make(contextView, "Book uploaded", Snackbar.LENGTH_LONG)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(floatingaddbook)
                    .show();


        }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            if (progress>2){
                progressBar.setIndeterminate(false);
                progressBar.setProgress((int) progress, true);
            }

            int p = (int) progress;
            imgtext.setText("Wait for uploading "+p+" %");

        });
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void getFileUrl(String imagename) {

        storageRef.child(imagename).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Uri url = Uri.parse(uri.toString());
                    //String name = filename.replaceAll("[^a-zA-Z0-9]", "");
                    String id = String.valueOf((System.currentTimeMillis()/1000));

                        bookref = "Book Management";
                        mReference.child(id).child("TITLE").setValue(title);
                        mReference.child(id).child("KEY").setValue(id);
                        mReference.child(id).child("PUBLISHED").setValue(false);
                        mReference.child(id).child("AUTHOR").setValue(author);
                        mReference.child(id).child("BOOKREF").setValue(bookref);
                        mReference.child(id).child("IMAGENAME").setValue(imagename);
                        mReference.child(id).child("IMAGELINK").setValue(url.toString())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getApplicationContext(), "Book Succecfully Created", Toast.LENGTH_LONG).show();
                                    btitle.setText("");
                                    imgtext.setText("");
                                    bauthor.setText("");
                                    textinputauthor.setError(null);
                                    textinputtitle.setError(null);
                                    btnupload.setEnabled(false);
                                    btnimage.setEnabled(true);
                                })
                                .addOnFailureListener(e -> Log.d("Saved", e.toString()));

                }).addOnFailureListener(e -> {

                });
    }

    MaterialAlertDialogBuilder materialAlertDialogBuilder;

    public void Show() {
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);

        final View contactPopupView = getLayoutInflater().inflate(R.layout.popup, null);
        btnimage = contactPopupView.findViewById(R.id.btimage);
        btitle = contactPopupView.findViewById(R.id.tvtitle);
        bauthor = contactPopupView.findViewById(R.id.tvauthor);
        btnupload = contactPopupView.findViewById(R.id.btupload);
        setBookImg = contactPopupView.findViewById(R.id.bookimg);
        progressBar = contactPopupView.findViewById(R.id.progressBar);
        imgtext = contactPopupView.findViewById(R.id.imgtext);
        textinputauthor = contactPopupView.findViewById(R.id.textinputauthor);
        textinputtitle = contactPopupView.findViewById(R.id.textinputtitle);
        btnupload.setEnabled(false);
        progressBar.setVisibility(View.GONE);
        setError();
        btnimage.setOnClickListener(view -> {
            title = btitle.getText().toString();
            author = bauthor.getText().toString();

            if (title.isEmpty() && author.isEmpty() || title.isEmpty() || author.isEmpty()){
                Toast.makeText(getApplicationContext(),"Enter Title and Author Name",Toast.LENGTH_LONG).show();
            }
            else {
                addBook();
            }

        });

        materialAlertDialogBuilder.setBackground(new ColorDrawable(Color.TRANSPARENT));
        materialAlertDialogBuilder.setView(contactPopupView).show();






    }
    public void setError(){
        btitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty())
                    textinputtitle.setError("This can't be set empty");
                else
                    textinputtitle.setError(null);
            }
        });
        bauthor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty())
                    textinputauthor.setError("This can't be set empty");
                else
                    textinputauthor.setError(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottomappbar, menu);

        MenuItem menuItem = menu.findItem(R.id.myinfo);
        menuItem.setOnMenuItemClickListener(menuItem1 -> {
            myInfo();
            return false;
        });
        MenuItem item = menu.findItem(R.id.appsearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchBook(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchBook(s);
                return false;
            }
        });
        Log.e("menu_selected",menu.toString());
        return super.onCreateOptionsMenu(menu);
    }
    public void  myInfo(){
        Log.e("my","info");
        startActivity(new Intent(getApplicationContext(), Settings.class));
    }

    int pos = 0;
    private void searchBook(String s) {
        if (pos==0){
            if (!s.isEmpty())
                myBooksFragment.search(s);
            else
                myBooksFragment.getBook();
        }
        else{
            if (!s.isEmpty())
                exploreFrangments.search(s);
            else
                exploreFrangments.getBooks();
        }



        //myBooksFragment.search(s);
    }



}