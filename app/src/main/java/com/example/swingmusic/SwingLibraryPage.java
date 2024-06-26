package com.example.swingmusic;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SwingLibraryPage extends AppCompatActivity implements FetchMusicFilesTask.MusicFilesCallback {
    BottomNavigationView bottomNavigationView;

    private EditText searchEditText;
    public static final int REQUEST_CODE = 1;
    ArrayList<MusicFiles> musicFiles;

    public FavouritesFragment favoritesFragment;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swing_library_page);
        permission();

        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(SwingLibraryPage.this, R.color.home_scr));

        favoritesFragment = (FavouritesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_favourites);

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id._library);
        searchEditText = findViewById(R.id.searchEditText);


        final Drawable clearButton = ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24);
        final Drawable[] drawables = searchEditText.getCompoundDrawables();
        searchEditText.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], clearButton, drawables[3]);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showClearButton(s.length() > 0); // Show cancel button when text is entered
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable[] drawables = searchEditText.getCompoundDrawables();
                    Drawable cancelButton = drawables[2]; // Get the right drawable (cancel button)

                    if (cancelButton != null && event.getX() >= (searchEditText.getRight() - cancelButton.getBounds().width())) {
                        searchEditText.setText("");
                        showClearButton(false);
                        return true;
                    }
                }
                return false;
            }
        });

        showClearButton(searchEditText.getText().length() > 0); // Initially manage the clear button visibility

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    processYouTubeLink();
                    return true; // Consume the event
                }
                return false;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id._home:
                        startActivity(new Intent(getApplicationContext(), SwingHomePage.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id._library:
                        return true;
                    case R.id._account:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id._about_us:
                        startActivity(new Intent(getApplicationContext(), SwingAboutUsPage.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }

    private void showClearButton(boolean show) {
        final Drawable clearButton = show ? ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24) : null;
        final Drawable[] drawables = searchEditText.getCompoundDrawables();
        searchEditText.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], clearButton, drawables[3]);
    }

    private void hideClearButton() {

        searchEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    private void processYouTubeLink() {
        String youtubeLink = searchEditText.getText().toString();

        // 1. Validate the YouTube link
        if (isValidYouTubeUrl(youtubeLink)) {
            // 2. Construct the Intent
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ravbug.com/yt-audio/?v=" + extractYouTubeVideoId(youtubeLink)));
            startActivity(intent);
        } else {
            // 3. Display error message
            Toast.makeText(this, "Invalid YouTube link", Toast.LENGTH_SHORT).show();
        }
    }

    public static String extractYouTubeVideoId(String youtubeUrl) {
        // Regular expression to match YouTube video URLs
        String regex = "(?<=youtu.be/|v=)([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(youtubeUrl);

        if (matcher.find()) {
            return matcher.group(1); // Return the captured video ID
        } else {
            return null; // No video ID found
        }
    }

    private boolean isValidYouTubeUrl(String youtubeLink) {
        String regex = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be)/(watch\\?v=|embed/|v/|.+\\?v=)?([^\\s&]+)$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(youtubeLink);
        return matcher.matches();
    }


    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SwingLibraryPage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        } else {
            //  musicFiles = getAllAudio(this);
            // initViewPager();
            new FetchMusicFilesTask(this, this).execute();
        }
    }


    public void onMusicFilesFetched(ArrayList<MusicFiles> musicFiles) {
        this.musicFiles = musicFiles;
        initViewPager();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                musicFiles = getAllAudio(this);
                initViewPager();
            } else {
                ActivityCompat.requestPermissions(SwingLibraryPage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }

    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPagerAdapter viewPagerAdapter = new viewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragments(new FavouritesFragment(), "Favourites");
        viewPagerAdapter.addFragments(new OnlineSongsFragment(), "Online");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static class viewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public viewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override

        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }


    }

    public static ArrayList<MusicFiles> getAllAudio(Context context) {
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
        };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration);
                Log.e("Path: " + path, "Album: " + album);
                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
        return tempAudioList;
    }

}