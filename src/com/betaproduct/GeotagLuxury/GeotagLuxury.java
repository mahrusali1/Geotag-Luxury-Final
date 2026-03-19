package com.betaproduct.GeotagLuxury;

import android.content.Context;
import android.util.Log;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.MediaUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

@DesignerComponent(version = 2,
    description = "Geotag Watermark Luxury Otomatis. Oleh Mahrus.",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png")
@SimpleObject
@UsesPermissions(permissionNames = "android.permission.INTERNET, android.permission.ACCESS_FINE_LOCATION, android.permission.ACCESS_COARSE_LOCATION")
public class GeotagLuxury extends AndroidNonvisibleComponent {

    private final Context context;

    public GeotagLuxury(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
    }

    @SimpleFunction(description = "Proses foto dengan watermark luxury. Alamat dicari otomatis dari Lat/Long.")
    public String ProcessImagePresisi(String imagePath, double latitude, double longitude, String date) {
        String address = "";
        try {
            android.location.Geocoder geocoder = new android.location.Geocoder(this.context, Locale.getDefault());
            List<android.location.Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0).getAddressLine(0);
            } else {
                address = "Lokasi: " + latitude + ", " + longitude;
            }
        } catch (Exception e) {
            address = "Koordinat: " + latitude + ", " + longitude;
        }

        try {
            android.graphics.Bitmap originalBitmap = MediaUtil.getBitmapDrawable(form, imagePath).getBitmap();
            android.graphics.Bitmap processedBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
            
            android.graphics.Canvas canvas = new android.graphics.Canvas(processedBitmap);
            int width = processedBitmap.getWidth();
            int height = processedBitmap.getHeight();

            int barHeight = height / 6;
            android.graphics.Paint p = new android.graphics.Paint();
            
            // Bar Hitam
            p.setColor(android.graphics.Color.parseColor("#BB000000"));
            canvas.drawRect(0, height - barHeight, width, height, p);

            int padding = width / 40;
            p.setColor(android.graphics.Color.WHITE);
            p.setAntiAlias(true);
            
            // Teks Alamat
            p.setFakeBoldText(true);
            p.setTextSize(barHeight * 0.25f);
            float addressY = height - barHeight + (barHeight * 0.35f);
            canvas.drawText(address, padding, addressY, p);

            // Teks Tanggal
            p.setFakeBoldText(false);
            p.setTextSize(barHeight * 0.15f);
            float dateY = addressY + (barHeight * 0.25f);
            canvas.drawText(date, padding, dateY, p);

            // Bendera
            int flagW = barHeight / 3;
            int flagH = (int)(flagW * 0.66f);
            int flagX = width - padding - flagW;
            int flagY = height - padding - flagH;
            
            p.setColor(android.graphics.Color.RED);
            canvas.drawRect(flagX, flagY, flagX + flagW, flagY + (flagH / 2), p);
            p.setColor(android.graphics.Color.WHITE);
            canvas.drawRect(flagX, flagY + (flagH / 2), flagX + flagW, flagY + flagH, p);

            File outputFile = File.createTempFile("Geotag_", ".jpg", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(outputFile);
            processedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
