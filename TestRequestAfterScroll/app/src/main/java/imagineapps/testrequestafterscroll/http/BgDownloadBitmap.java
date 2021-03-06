package imagineapps.testrequestafterscroll.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import imagineapps.testrequestafterscroll.MainActivity;
import imagineapps.testrequestafterscroll.utils.SimpleDownloadBitmap;
import imagineapps.testrequestafterscroll.entitiies.Info;
import imagineapps.testrequestafterscroll.utils.UtilsBitmap;
import imagineapps.uptolv.utils.http.ModelHTTPRequest;

/**
 * Created by r028367 on 04/07/2017.
 */

public class BgDownloadBitmap extends ModelHTTPRequest {

    private Handler handler;
    private List<Info> listInfo;
    private Context context;

    public BgDownloadBitmap(List<Info> listInfo, Handler handler) {
        this.listInfo = listInfo;
        this.handler  = handler;
    }

    public BgDownloadBitmap(List<Info> listInfo, Handler handler, Context context) {
        this.listInfo = listInfo;
        this.handler  = handler;
        this.context  = context;
    }

    public BgDownloadBitmap(String url, List<Info> listInfo, Handler handler) {
        super(url);
    }

    public BgDownloadBitmap(String url, Map<String, String> parameters) {
        super(url, parameters);
    }

    @Override
    public List<? extends  Parcelable> execute() {
        executeDownload();
        return listInfo;
    }

    private void executeSimpleDownload() {
        for(Info info : listInfo) {
            String urlImage = info.getUrlImage();
            Bitmap bitmap = SimpleDownloadBitmap.download(urlImage);
            if( bitmap != null) {
                byte [] bitmapBuffer = UtilsBitmap.compress(bitmap);
                info.setImageBuffer(bitmapBuffer);
            }
        }
        return;
    }

    private void executeDownload() {
        for(Info info : listInfo) {
            RequestCreator requestCreator = Picasso.with(context).load(info.getUrlImage());
            try {
                Bitmap bitmap = requestCreator.get();
                if( bitmap != null) {
                    byte [] bitmapBuffer = UtilsBitmap.compress(bitmap);
                    info.setImageBuffer(bitmapBuffer);
                }
            } catch (IOException e) {
                Log.e("EXCP_DOWNLOAD_BITMAP", e.getMessage());
            }

        }
        return;
    }

    @Override
    public void afterExecution(List<Parcelable> data) {
        if(handler != null) {
            Message message = new Message();
            message.what    = MainActivity.HANDLER_MSG_DOWNLOAD_BITMAP;
            Bundle bundle   = new Bundle();
            ArrayList list  = (ArrayList<? extends Parcelable>) data;
            bundle.putParcelableArrayList(MainActivity.BUNDLE_DATA_ARRAYLIST_API, list);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
}
