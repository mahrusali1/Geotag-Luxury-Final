package com.betaproduct.GeotagLuxury;

import android.graphics.*;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import java.io.FileOutputStream;
import java.io.File;
import java.util.UUID;
import java.io.InputStream;

@DesignerComponent(version = 1, description = "Geotag Watermark Presisi - GPS Map Camera Style", category = ComponentCategory.EXTENSION, nonVisible = true)
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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath, options);
            if (originalBitmap == null) return "Error: File tidak ditemukan";

            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();
            int tableHeight = height / 4;
            
            // PERBAIKAN: Menggunakan nama lengkap android.graphics.Canvas agar tidak bentrok
            android.graphics.Canvas canvas = new android.graphics.Canvas(originalBitmap);

            // 1. Background Hitam Transparan
            Paint bgPaint = new Paint();
            bgPaint.setColor(Color.parseColor("#CC000000"));
            canvas.drawRect(0, height - tableHeight, width, height, bgPaint);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setAntiAlias(true);
            float textSizeAlamat = tableHeight / 9;
            float textSizeDetail = tableHeight / 13;
            int marginKiri = width / 25;
            int startY = (height - tableHeight) + (tableHeight / 4);

            // 2. Teks Alamat & Bendera
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            textPaint.setTextSize(textSizeAlamat);
            canvas.drawText(address + " Indonesia ", marginKiri, startY, textPaint);

            // Gambar Bendera Merah Putih
            float benderaX = marginKiri + textPaint.measureText(address + " Indonesia ");
            float bSize = textSizeAlamat;
            Paint bPaint = new Paint();
            bPaint.setColor(Color.RED);
            canvas.drawRect(benderaX, startY - bSize, benderaX + (bSize * 1.5f), startY - (bSize/2), bPaint);
            bPaint.setColor(Color.WHITE);
            canvas.drawRect(benderaX, startY - (bSize/2), benderaX + (bSize * 1.5f), startY, bPaint);

            // 3. Logo GPS Map Camera
            try {
                InputStream is = container.$form().openAsset("gps_logo.png");
                Bitmap logo = BitmapFactory.decodeStream(is);
                int lSize = tableHeight / 4;
                Bitmap sLogo = Bitmap.createScaledBitmap(logo, lSize, lSize, true);
                canvas.drawBitmap(sLogo, width - lSize - marginKiri, height - tableHeight + 20, null);
            } catch (Exception e) {
                // Abaikan jika logo tidak ada
            }

            // 4. Detail Koordinat & Waktu
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            textPaint.setTextSize(textSizeDetail);
            int yDetail = startY + (int)(textSizeAlamat * 1.8f);
            canvas.drawText("Lat " + latitude + " Long " + longitude, marginKiri, yDetail, textPaint);
            canvas.drawText(date + " GMT +07:00", marginKiri, yDetail + (int)(textSizeDetail * 1.6f), textPaint);

            // 5. Simpan File Baru
            String outPath = imagePath.replace(".jpg", "_geotag.jpg");
            if (outPath.equals(imagePath)) outPath = imagePath + "_new.jpg"; // Jaga-jaga jika format bukan jpg
            
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
