package com.kryptkode.cyberman.djtech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kryptkode.cyberman.djtech.views.ChangeThemeDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OptionsActivity extends AppCompatActivity implements ChangeThemeDialog.ChangeThemeDialogListener{
    private static final String TAG = "OptionsActivity" ;
    private ListView appearanceListview, assistanceListview;
    private ArrayAdapter appearanceArray, assistanceArray;
    SharedPreferences sharedPref;
    public static final String MYPRE = "MyPre";
    public static final String IMAGE_PATH = "Path";
    public static final String WALLPAPER_FLAGS = "WallpaperValue";
    public static final String THEME = "theme";
    private static final int REQUEST_CODE= 1000;
    Context context = this;
    Intent intent;
    Bitmap bitmap;
    SharedPreferences sharedPreferences;

    private ChangeThemeDialog changeThemeDialog;
    private int whichTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_layout);
        appearanceListview = (ListView) findViewById(R.id.appearance_listview);
        assistanceListview = (ListView) findViewById(R.id.assistance_listview);
        appearanceArray = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.appearance_options));
        assistanceArray = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.assistance_options));
        appearanceListview.setAdapter(appearanceArray);
        assistanceListview.setAdapter(assistanceArray);

        appearanceListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch(position){
                    case 0:
                        SharedPreferences sharedPreferences = getSharedPreferences(MYPRE, Context.MODE_PRIVATE);
                        int num = sharedPreferences.getInt(THEME, 0);
                        changeThemeDialog = new ChangeThemeDialog();
                        changeThemeDialog.setCheckedItem(num);
                        changeThemeDialog.show(getSupportFragmentManager(), "Theme");
                        break;
                    case 1:
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("Choose Background from");
                        View views = LayoutInflater.from(context).inflate(R.layout.wallpaper_dialog_layout, null);
                        dialog.setView(views);
                        dialog.show();
                        LinearLayout linearLayout1 = (LinearLayout) views.findViewById(R.id.gallery);
                        LinearLayout linearLayout2 = (LinearLayout) views.findViewById(R.id.solid_color);
                        LinearLayout linearLayout3 = (LinearLayout) views.findViewById(R.id.remove);
                        linearLayout1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sharedPref = context.getSharedPreferences(MYPRE,MODE_PRIVATE);
                                sharedPref.edit().putInt(WALLPAPER_FLAGS, 15).commit();
                                intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                Intent chooser = Intent.createChooser(intent, "Gallery");
                                startActivityForResult(chooser, REQUEST_CODE);
                            }
                        });
                        linearLayout2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                intent = new Intent(OptionsActivity.this, SolidColorActivity.class);
                                startActivity(intent);
                            }
                        });
                        linearLayout3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sharedPref = context.getSharedPreferences(MYPRE,MODE_PRIVATE);
                                sharedPref.edit().putInt(WALLPAPER_FLAGS, 10).commit();
                                //WALLPAPER_FLAG = 10;
                                Toast.makeText(context, "Wallpaper Removed", Toast.LENGTH_LONG).show();
                                dialog.setCancelable(true);
                            }
                        });
                        //WALLPAPER_FLAG = 0;
                        break;
                }
            }
        });

        assistanceListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        intent = new Intent(OptionsActivity.this, FAQsActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(OptionsActivity.this, InformationListActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode==RESULT_OK)&&(requestCode == REQUEST_CODE)){
            Uri selectedImagePath = data.getData();
            Log.d("TAG", selectedImagePath.getPath());
            try {
                View view = LayoutInflater.from(this).inflate(R.layout.settings_layout,null);
                LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.options_layout);
                InputStream fileInputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(fileInputStream);
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, relativeLayout.getWidth(), relativeLayout.getHeight(),true);

                //relativeLayout.setBackground(new BitmapDrawable(getResources(), bitmap1));
                sharedPreferences = getSharedPreferences(MYPRE, MODE_PRIVATE);
                sharedPreferences.edit().putString(IMAGE_PATH,BitMapToString(bitmap1)).commit();
                fileInputStream.close();
                Toast.makeText(this, "Image set as Background", Toast.LENGTH_SHORT).show();

            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("TAG", "encoded string" + temp);
        return temp;

    }

    public static Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onDialogPositiveButtonClicked(DialogFragment dialog) {
        toggleTheme(whichTheme);
        changeThemeDialog.dismiss();
        SharedPreferences sharedPreferences = getSharedPreferences(MYPRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(THEME, whichTheme);
        editor.apply();

        Toast.makeText(this, R.string.theme_change_toast_texrt, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRadioButtonClicked(int which) {
        whichTheme = which;
        Log.i(TAG, "onRadioButtonClicked: " + whichTheme);
    }

    @Override
    public void onDialogNegativeButtonClicked(DialogFragment dialog) {
        changeThemeDialog.dismiss();
    }

    private void toggleTheme(int themeSelect) {
        switch (themeSelect) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
}
