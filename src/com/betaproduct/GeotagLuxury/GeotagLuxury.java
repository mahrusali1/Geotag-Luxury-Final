package com.betaproduct.GeotagLuxury;

import android.content.Context;
import android.graphics.*;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.MediaUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@DesignerComponent(version = 2,
    description = "Geotag Watermark Presisi Luxury dengan Alamat Otomatis. Oleh Mahrus.",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png")
@SimpleObject
@UsesPermissions(permissionNames = "android.permission.INTERNET, android.permission.ACCESS_FINE_LOCATION, android.permission.ACCESS_COARSE_LOCATION")
public class GeotagLuxury extends AndroidNonvisibleComponent {

    private static final String LOG_TAG = "GeotagLuxury";
    private final Context context;

    public GeotagLuxury(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
    }

    @SimpleFunction(description = "Proses foto dengan watermark luxury. Alamat dicari otomatis dari Lat/Long.")
    public String ProcessImagePresisi(String imagePath, double latitude, double longitude, String date) {
        String address = getAddressFromCoords(latitude, longitude);

        try {
            Bitmap originalBitmap = MediaUtil.getBitmapDrawable(form, imagePath).getBitmap();
            Bitmap.Config config = originalBitmap.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            Bitmap processedBitmap = originalBitmap.copy(config, true);
            
            // Perbaikan: Gunakan nama lengkap android.graphics.Canvas agar tidak tertukar
            android.graphics.Canvas canvas = new android.graphics.Canvas(processedBitmap);

            int width = processedBitmap.getWidth();
            int height = processedBitmap.getHeight();

            int barHeight = height / 6;
            Paint barPaint = new Paint();
            barPaint.setColor(Color.parseColor("#99000000"));
            barPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, height - barHeight, width, height, barPaint);

            int padding = width / 40;
            int textColor = Color.WHITE;
            
            Paint addressPaint = new Paint();
            addressPaint.setColor(textColor);
            addressPaint.setAntiAlias(true);
            addressPaint.setFakeBoldText(true);
            addressPaint.setTextSize(barHeight * 0.3f);

            Paint datePaint = new Paint();
            datePaint.setColor(textColor);
            datePaint.setAntiAlias(true);
            datePaint.setTextSize(barHeight * 0.2f);

            Paint coordPaint = new Paint();
            coordPaint.setColor(textColor);
            coordPaint.setAntiAlias(true);
            coordPaint.setTextSize(barHeight * 0.15f);

            float textX = padding;
            float addressY = height - barHeight + padding + (barHeight * 0.25f);
            canvas.drawText(address, textX, addressY, addressPaint);

            float dateY = addressY + (barHeight * 0.25f);
            canvas.drawText(date, textX, dateY, datePaint);

            float coordY = dateY + (barHeight * 0.2f);
            String coordText = "Lat: " + String.format("%.6f", latitude) + " | Long: " + String.format("%.6f", longitude);
            canvas.drawText(coordText, textX, coordY, coordPaint);

            // Perbaikan: Konversi hasil perkalian float ke int
            int flagWidth = barHeight / 3;
            int flagHeight = flagWidth * 2 / 3;
            int flagX = width - padding - flagWidth;
            int flagY = (int) (height - barHeight + padding + (barHeight * 0.15f));

            Paint redPaint = new Paint();
            redPaint.setColor(Color.parseColor("#FF0000"));
            canvas.drawRect(flagX, flagY, flagX + flagWidth, flagY + (flagHeight / 2), redPaint);

            Paint whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);
            canvas.drawRect(flagX, flagY + (flagHeight / 2), flagX + flagWidth, flagY + flagHeight, whitePaint);

            try {
                Bitmap logoBitmap = MediaUtil.getBitmapDrawable(form, "gps_logo.png").getBitmap();
                int logoSize = barHeight * 3 / 4;
                int logoX = width - padding - logoSize;
                int logoY = height - logoSize - padding;
                Rect destRect = new Rect(logoX, logoY, logoX + logoSize, logoY + logoSize);
                canvas.drawBitmap(logoBitmap, null, destRect, null);
            } catch (Exception e) {
                Log.e(LOG_TAG, "File 'gps_logo.png' tidak ditemukan.");
            }

            File outputDir = context.getCacheDir();
            File outputFile = File.createTempFile("Geotag_Luxury_", ".jpg", outputDir);
            FileOutputStream fos = new FileOutputStream(outputFile);
            processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    private String getAddressFromCoords(double lat, double lon) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
                    if (i < returnedAddress.getMaxAddressLineIndex()) {
                        strReturnedAddress.append(", ");
                    }
                }
                return strReturnedAddress.toString();
            } else {
                return "Alamat tidak ditemukan.";
            }
        } catch (IOException e) {
            return "Error: " + lat + ", " + lon + " (Cek Internet)";
        }
    }
}
