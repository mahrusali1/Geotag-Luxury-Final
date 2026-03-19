package com.betaproduct.GeotagLuxury;

import android.content.Context;
import android.graphics.*;
import android.location.Address;      // INI YANG TADI KURANG
import android.location.Geocoder;     // INI YANG TADI KURANG
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
    description = "Geotag Watermark Luxury Otomatis. Oleh Mahrus.",
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
        // Ambil alamat otomatis
        String address = getAddressFromCoords(latitude, longitude);

        try {
            Bitmap originalBitmap = MediaUtil.getBitmapDrawable(form, imagePath).getBitmap();
            Bitmap.Config config = originalBitmap.getConfig();
            if (config == null) config = Bitmap.Config.ARGB_8888;
            Bitmap processedBitmap = originalBitmap.copy(config, true);
            
            android.graphics.Canvas canvas = new android.graphics.Canvas(processedBitmap);

            int width = processedBitmap.getWidth();
            int height = processedBitmap.getHeight();

            // Gambar Bar Hitam Transparan
            int barHeight = height / 6;
            Paint barPaint = new Paint();
            barPaint.setColor(Color.parseColor("#99000000"));
            canvas.drawRect(0, height - barHeight, width, height, barPaint);

            int padding = width / 40;
            
            // Teks Alamat (Bold)
            Paint addressPaint = new Paint();
            addressPaint.setColor(Color.WHITE);
            addressPaint.setAntiAlias(true);
            addressPaint.setFakeBoldText(true);
            addressPaint.setTextSize(barHeight * 0.25f);

            // Teks Tanggal & Koordinat
            Paint smallPaint = new Paint();
            smallPaint.setColor(Color.WHITE);
            smallPaint.setAntiAlias(true);
            smallPaint.setTextSize(barHeight * 0.15f);

            float textX = padding;
            float addressY = height - barHeight + padding + (barHeight * 0.2f);
            canvas.drawText(address, textX, addressY, addressPaint);

            float dateY = addressY + (barHeight * 0.25f);
            canvas.drawText(date, textX, dateY, smallPaint);

            float coordY = dateY + (barHeight * 0.2f);
            String coordText = "Lat: " + String.format("%.6f", latitude) + " | Long: " + String.format("%.6f", longitude);
            canvas.drawText(coordText, textX, coordY, smallPaint);

            // Gambar Bendera Merah Putih
            int flagWidth = barHeight / 3;
            int flagHeight = flagWidth * 2 / 3;
            int flagX = width - padding - flagWidth;
            int flagY = (int) (height - barHeight + padding);

            Paint redPaint = new Paint();
            redPaint.setColor(Color.RED);
            canvas.drawRect(flagX, flagY, flagX + flagWidth, flagY + (flagHeight / 2), redPaint);

            Paint whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);
            canvas.drawRect(flagX, flagY + (flagHeight / 2), flagX + flagWidth, flagY + flagHeight, whitePaint);

            // Simpan File
            File outputFile = File.createTempFile("Geotag_", ".jpg", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(outputFile);
            processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String getAddressFromCoords(double lat, double lon) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addr = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= addr.getMaxAddressLineIndex(); i++) {
                    sb.append(addr.getAddressLine(i)).append(i < addr.getMaxAddressLineIndex() ? ", " : "");
                }
                return sb.toString();
            }
            return "Alamat tidak ditemukan.";
        } catch (Exception e) {
            return "Koordinat: " + lat + ", " + lon;
        }
    }
}
