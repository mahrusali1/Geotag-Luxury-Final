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

@DesignerComponent(version = 2, // Versi dinaikkan karena ada fitur alamat otomatis
    description = "Geotag Watermark Presisi Luxury dengan Alamat Otomatis (Reverse Geocoding). Oleh Mahrus.",
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

    /**
     * Fungsi Utama: Memproses foto dengan watermark presisi dan alamat otomatis.
     * Cukup masukkan path foto, lat, long, dan tanggal. Alamat akan dicari otomatis.
     */
    @SimpleFunction(description = "Proses foto dengan watermark luxury. Alamat dicari otomatis dari Lat/Long.")
    public String ProcessImagePresisi(String imagePath, double latitude, double longitude, String date) {
        // 1. Ambil Alamat Otomatis dari Koordinat
        String address = getAddressFromCoords(latitude, longitude);

        try {
            // 2. Load Foto Asli
            Bitmap originalBitmap = MediaUtil.getBitmapDrawable(form, imagePath).getBitmap();
            Bitmap.Config config = originalBitmap.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            Bitmap processedBitmap = originalBitmap.copy(config, true);
            Canvas canvas = new Canvas(processedBitmap);

            int width = processedBitmap.getWidth();
            int height = processedBitmap.getHeight();

            // 3. Gambar Bar Hitam Transparan Presisi di Bawah
            int barHeight = height / 6; // Tinggi bar proporsional (sekitar 16% tinggi foto)
            Paint barPaint = new Paint();
            barPaint.setColor(Color.parseColor("#99000000")); // Hitam transparan (Alpha 153/255)
            barPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, height - barHeight, width, height, barPaint);

            // 4. Pengaturan Teks
            int padding = width / 40; // Padding teks agar tidak mepet
            int textColor = Color.WHITE;
            
            // Pengaturan Font Alamat (Tebal/Bold)
            Paint addressPaint = new Paint();
            addressPaint.setColor(textColor);
            addressPaint.setAntiAlias(true);
            addressPaint.setFakeBoldText(true); // Teks Alamat Dibuat Bold
            addressPaint.setTextSize(barHeight * 0.3f); // Ukuran teks alamat (30% tinggi bar)

            // Pengaturan Font Tanggal/Waktu (Normal, Lebih Kecil)
            Paint datePaint = new Paint();
            datePaint.setColor(textColor);
            datePaint.setAntiAlias(true);
            datePaint.setTextSize(barHeight * 0.2f); // Ukuran teks tanggal (20% tinggi bar)

            // Pengaturan Font Koordinat (Normal, Paling Kecil)
            Paint coordPaint = new Paint();
            coordPaint.setColor(textColor);
            coordPaint.setAntiAlias(true);
            coordPaint.setTextSize(barHeight * 0.15f); // Ukuran teks koordinat (15% tinggi bar)

            // 5. Gambar Teks pada Posisi Presisi
            float textX = padding;
            
            // Posisi Y Teks Alamat (Paling Atas di bar)
            float addressY = height - barHeight + padding + (barHeight * 0.25f);
            canvas.drawText(address, textX, addressY, addressPaint);

            // Posisi Y Teks Tanggal (Di bawah alamat)
            float dateY = addressY + (barHeight * 0.25f);
            canvas.drawText(date, textX, dateY, datePaint);

            // Posisi Y Teks Koordinat (Di paling bawah bar)
            float coordY = dateY + (barHeight * 0.2f);
            String coordText = "Lat: " + String.format("%.6f", latitude) + " | Long: " + String.format("%.6f", longitude);
            canvas.drawText(coordText, textX, coordY, coordPaint);

            // 6. Gambar Bendera Indonesia Presisi di Pojok Kanan Atas Bar Alamat
            int flagWidth = barHeight / 3; // Ukuran bendera proporsional
            int flagHeight = flagWidth * 2 / 3; // Rasio bendera 3:2
            int flagX = width - padding - flagWidth;
            int flagY = height - barHeight + padding + (barHeight * 0.15f); // Sejajar dengan teks alamat

            // Gambar Merah (Atas)
            Paint redPaint = new Paint();
            redPaint.setColor(Color.parseColor("#FF0000")); // Merah Merdeka
            canvas.drawRect(flagX, flagY, flagX + flagWidth, flagY + (flagHeight / 2), redPaint);

            // Gambar Putih (Bawah)
            Paint whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);
            canvas.drawRect(flagX, flagY + (flagHeight / 2), flagX + flagWidth, flagY + flagHeight, whitePaint);

            // 7. Gambar Logo GPS Map Camera (Harus ada file media: gps_logo.png)
            try {
                Bitmap logoBitmap = MediaUtil.getBitmapDrawable(form, "gps_logo.png").getBitmap();
                int logoSize = barHeight * 3 / 4; // Logo berukuran 75% tinggi bar
                int logoX = width - padding - logoSize;
                int logoY = height - logoSize - padding;
                Rect destRect = new Rect(logoX, logoY, logoX + logoSize, logoY + logoSize);
                canvas.drawBitmap(logoBitmap, null, destRect, null);
            } catch (Exception e) {
                // Jika file logo tidak ada, abaikan tanpa error
                Log.e(LOG_TAG, "File 'gps_logo.png' tidak ditemukan di Media.", e);
            }

            // 8. Simpan Foto Hasil Jadi
            File outputDir = context.getCacheDir();
            File outputFile = File.createTempFile("Geotag_Luxury_", ".jpg", outputDir);
            FileOutputStream fos = new FileOutputStream(outputFile);
            processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos); // Kompresi JPEG 90%
            fos.close();

            // Kembalikan jalur (path) file foto baru
            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error saat memproses foto Geotag.", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Fungsi Internal: Mengubah koordinat menjadi alamat lengkap (Reverse Geocoding).
     */
    private String getAddressFromCoords(double lat, double lon) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1); // Ambil 1 hasil terbaik
            if (addresses != null && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                // Susun alamat lengkap (Nama Jalan, Desa, Kec, Kab, Prov)
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
            Log.e(LOG_TAG, "Gagal mendapatkan alamat dari koordinat (mungkin masalah internet).", e);
            return "Error: " + lat + ", " + lon + " (Cek Internet)";
        }
    }
}
