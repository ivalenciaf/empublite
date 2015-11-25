package com.commonsware.empublite;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import de.greenrobot.event.EventBus;
import okio.BufferedSink;
import okio.Okio;
import retrofit.RestAdapter;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class DownloadCheckService extends IntentService {
    private static final String OUR_BOOK_DATE = "20120418";
    private static final String UPDATE_FILENAME = "book.zip";
    public static final String UPDATE_BASEDIR = "updates";


    public DownloadCheckService() {
        super("DownloadCheckService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String updateUrl = getUpdateUrl();

            if(updateUrl != null) {
                File book = download(updateUrl);
                File updateDir = new File(getFilesDir(), UPDATE_BASEDIR);

                updateDir.mkdirs();
                unzip(book, updateDir);

                book.delete();
                EventBus.getDefault().post(new BookUpdatedEvent());
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Exception downloading book update", e);
        }
    }

    private String getUpdateUrl() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("https://commonsware.com").build();

        BookUpdateInterface bookUpdate = adapter.create(BookUpdateInterface.class);
        BookUpdateInfo updateInfo = bookUpdate.update();

        if(updateInfo.updatedOn.compareTo(OUR_BOOK_DATE) > 0) {
            return updateInfo.updateUrl;
        }

        return null;
    }

    private File download(String url) throws IOException {
        File output = new File(getFilesDir(), UPDATE_FILENAME);

        if(output.exists()) {
            output.delete();
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        BufferedSink sink = Okio.buffer(Okio.sink(output));

        sink.writeAll(response.body().source());
        sink.close();

        return output;
    }

    private static void unzip(File src, File dest) throws IOException {
        InputStream is=new FileInputStream(src);
        ZipInputStream zis=new ZipInputStream(new BufferedInputStream(is));
        ZipEntry ze;

        dest.mkdirs();

        while ((ze=zis.getNextEntry()) != null) {
            byte[] buffer=new byte[16384];
            int count;
            FileOutputStream fos = new FileOutputStream(new File(dest, ze.getName()));
            BufferedOutputStream out=new BufferedOutputStream(fos);

            try {
                while ((count=zis.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }

                out.flush();
            }
            finally {
                fos.getFD().sync();
                out.close();
            }

            zis.closeEntry();
        }

        zis.close();
    }
}
