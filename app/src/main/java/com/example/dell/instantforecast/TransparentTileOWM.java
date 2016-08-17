package com.example.dell.instantforecast;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.google.gson.internal.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by TONITUAN on 8/16/2016.
 */
public class TransparentTileOWM implements TileProvider {

    private Paint opacityPaint = new Paint();
    private String tileType;

    public static final String OWM_TILE_URL = "http://tile.openweathermap.org/map/%s/%d/%d/%d.png";

    public TransparentTileOWM(String tileType){
        this.tileType = tileType;
        setOpacity(50);
    }

    public void setOpacity(int value){
        int alpha = (int)Math.round(value * 2.55);
        opacityPaint.setAlpha(alpha);
    }

    @Override
    public Tile getTile(int i, int i1, int i2) {
        URL url = getTileURL(i, i1, i2);
        Tile tile = null;
        ByteArrayOutputStream stream = null;
        try{
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            bitmap = adjustOpacity(bitmap);
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            tile = new Tile(256, 256, byteArray);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if(stream != null){
                try{
                    stream.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return tile;
    }

    //assume the URL string provided in the constructor contains
    private URL getTileURL(int x, int y, int zoom){
        String tileURL = String.format(OWM_TILE_URL, tileType, zoom, x, y);
        try{
            return new URL(tileURL);
        }
        catch (MalformedURLException e){
            throw new AssertionError(e);
        }
    }

    private Bitmap adjustOpacity(Bitmap bitmap){
        Bitmap bitmap1 = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap1);
        canvas.drawBitmap(bitmap, 0, 0, opacityPaint);
        return bitmap1;
    }
}
