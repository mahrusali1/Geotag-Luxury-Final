package com.betaproduct.GeotagLuxury;

import android.graphics.*;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import java.io.FileOutputStream;
import java.io.File;
import java.util.UUID;
import java.io.InputStream;

@DesignerComponent(version = 1, 
                   description = "Geotag Watermark Presisi - GPS Map Camera Style oleh Mahrus", 
                   category = ComponentCategory.EXTENSION, 
                   nonVisible = true)
@SimpleObject(external = true)
public class GeotagLuxury extends AndroidNonvisibleComponent {
    private ComponentContainer container;

    public GeotagLuxury(ComponentContainer container) {
        super(container.$form());
        this.container = container;
    }

    @SimpleFunction(description = "Proses Geotag Mewah. Input: Path Foto, Alamat, Lintang, Bujur, Tanggal.")
    public String ProcessImagePresisi(String imagePath, String address, String latitude, String longitude, String date) {
        try {
            // 1. Load Foto Asli
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath, options);
            if (originalBitmap == null) return "Error: File tidak ditemukan";

            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();
            
            // Tinggi tabel sekitar 22% dari tinggi foto
            int tableHeight = height / 4;
            
            // Gunakan Nama Lengkap Class agar tidak bentrok dengan Canvas AI2
            android.graphics.Canvas canvas = new android.graphics.Canvas(originalBitmap);

            // 2. Gambar Background Hitam Transparan (Luxury Look)
            Paint bgPaint = new Paint();
            bgPaint.setColor(Color.parseColor("#CC000000")); // 80% Transparansi
            canvas.drawRect(0, height - tableHeight, width, height, bgPaint);

            // 3. Pengaturan Teks
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setAntiAlias(true);
            
            float textSizeAlamat = tableHeight / 9;
            float textSizeDetail = tableHeight / 13;
            int marginKiri = width / 25;
            int startY = (height - tableHeight) + (tableHeight / 4);

            // --- BARIS 1: ALAMAT & BENDERA ---
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            textPaint.setTextSize(textSizeAlamat);
            String fullAddress = address + " Indonesia ";
            canvas.drawText(fullAddress, marginKiri, startY, textPaint);

            // Gambar Bendera Merah Putih Otomatis
            float benderaX = marginKiri + textPaint.measureText(fullAddress);
            float bHeight = textSizeAlamat;
            float bWidth = bHeight * 1.5f;
            float bY = startY - (bHeight * 0.8f);
            
            Paint bPaint = new Paint();
            bPaint.setColor(Color.RED);
            canvas.drawRect(benderaX, bY, benderaX + bWidth, bY + (bHeight/2), bPaint);
            bPaint.setColor(Color.WHITE);
            canvas.drawRect(benderaX, bY + (bHeight/2), benderaX + bWidth, bY + bHeight, bPaint);

            // --- BARIS 2: LOGO GPS MAP CAMERA ---
            try {
                // Pastikan ada file gps_logo.png di Media MIT App Inventor
                InputStream is = container.$form().openAsset("gps_logo.png");
                Bitmap logo = BitmapFactory.decodeStream(is);
                int lSize = tableHeight / 4;
                Bitmap sLogo = Bitmap.createScaledBitmap(logo, lSize, lSize, true);
                canvas.drawBitmap(sLogo, width - lSize - marginKiri, height - tableHeight + 20, null);
                
                // Teks Label Logo
                textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                textPaint.setTextSize(textSizeDetail);
                canvas.drawText("GPS Map Camera", width - (lSize * 3.5f) - marginKiri, height - tableHeight + (lSize/2) + 20, textPaint);
            } catch (Exception e) {
                // Tetap lanjut jika logo tidak ada
            }

            // --- BARIS 3 & 4: KOORDINAT & WAKTU ---
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            textPaint.setTextSize(textSizeDetail);
            int yDetail = startY + (int)(textSizeAlamat * 1.8f);
            
            canvas.drawText("Lat " + latitude + " Long " + longitude, marginKiri, yDetail, textPaint);
            canvas.drawText(date + " GMT +07:00", marginKiri, yDetail + (int)(textSizeDetail * 1.6f), textPaint);

            // 4. Simpan ke File Baru
            String outPath = imagePath.substring(0, imagePath.lastIndexOf(".")) + "_geotag.jpg";
            FileOutputStream out = new FileOutputStream(new File(outPath));
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
            out.flush();
            out.close();

            return outPath;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
