package com.betaproduct.GeotagLuxury;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.MediaUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

@DesignerComponent(version = 2,
    description = "Geotag Watermark Luxury SKP Click. Oleh Mahrus.",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png")
@SimpleObject
@UsesPermissions(permissionNames = "android.permission.INTERNET, android.permission.ACCESS_FINE_LOCATION, android.permission.ACCESS_COARSE_LOCATION")
public class GeotagLuxury extends AndroidNonvisibleComponent {

    private final Context context;
    private final Form form; 

    public GeotagLuxury(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
        this.form = container.$form(); 
    }

    @SimpleFunction(description = "Proses foto luxury untuk pelaporan PKH.")
    public String ProcessImagePresisi(String imagePath, double latitude, double longitude, String date) {
        String address = "Koordinat: " + latitude + ", " + longitude;
        try {
            Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            // Tetap menggunakan koordinat jika geocoder gagal
        }

        try {
            Bitmap originalBitmap = MediaUtil.getBitmapDrawable(this.form, imagePath).getBitmap();
            Bitmap processedBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
            
            Canvas canvas = new Canvas(processedBitmap);
            int width = processedBitmap.getWidth();
            int height = processedBitmap.getHeight();

            int barHeight = height / 6;
            Paint p = new Paint();
            
            p.setColor(Color.parseColor("#BB000000"));
            canvas.drawRect(0, height - barHeight, width, height, p);

            int padding = width / 40;
            p.setColor(Color.WHITE);
            p.setAntiAlias(true);
            
            p.setFakeBoldText(true);
            p.setTextSize(barHeight * 0.25f);
            canvas.drawText(address, padding, height - barHeight + (barHeight * 0.35f), p);

            p.setFakeBoldText(false);
            p.setTextSize(barHeight * 0.15f);
            canvas.drawText(date, padding, height - barHeight + (barHeight * 0.65f), p);

            File outputFile = File.createTempFile("Geotag_SKP_", ".jpg", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(outputFile);
            processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
