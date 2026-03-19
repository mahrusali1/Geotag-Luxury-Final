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
        // Ambil alamat otomatis secara aman
        String address = "";
        try {
            Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0).getAddressLine(0);
            } else {
                address = "Lokasi: " + latitude + ", " + longitude;
            }
        } catch (Exception e) {
            address = "Koordinat: " + latitude + ", " + longitude;
        }

        try {
            Bitmap originalBitmap = MediaUtil.getBitmapDrawable(form, imagePath).getBitmap();
            Bitmap.Config config = originalBitmap.getConfig();
            if (config == null) config = Bitmap.Config.ARGB_8888;
            Bitmap processedBitmap = originalBitmap.copy(config, true);
            
            android.graphics.Canvas canvas = new android.graphics.Canvas(processedBitmap);
            int width = processedBitmap.getWidth();
            int height = processedBitmap.getHeight();

            // Gambar Bar Hitam Transparan (Luxury Style)
            int barHeight = height / 6;
            Paint barPaint = new Paint();
            barPaint.setColor(Color.parseColor("#BB000000")); // Hitam agak pekat
            canvas.drawRect(0, height - barHeight, width, height, barPaint);

            int padding = width / 40;
            
            // Teks Alamat (Putih & Bold)
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setAntiAlias(true);
            textPaint.setFakeBoldText(true);
            textPaint.setTextSize(barHeight * 0.25f);

            float textX = padding;
            float addressY = height - barHeight + (barHeight * 0.35f);
            canvas.drawText(address, textX, addressY, textPaint);

            // Teks Tanggal & Jam (Lebih Kecil)
            textPaint.setFakeBoldText(false);
            textPaint.setTextSize(barHeight * 0.15f);
            float dateY = addressY + (barHeight * 0.25f);
            canvas.drawText(date, textX, dateY, textPaint);

            // Gambar Bendera Merah Putih (Pojok Kanan Bawah)
            int flagW = barHeight / 3;
            int flagH = flagW * 2 / 3;
            int flagX = width - padding - flagW;
            int flagY = height - padding - flagH;
            
            Paint flagPaint = new Paint();
            flagPaint.setColor(Color.RED);
            canvas.drawRect(flagX, flagY, flagX + flagW, flagY + (flagH / 2), flagPaint);
            flagPaint.setColor(Color.WHITE);
            canvas.drawRect(flagX, flagY + (flagH / 2), flagX + flagW, flagY + flagH, flagPaint);

            // Simpan ke Cache
            File outputFile = File.createTempFile("Geotag_", ".jpg", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(outputFile);
            processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
