package com.kaze.jailbreakpong;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerAsyncTask extends AsyncTask {

    private Context context;

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();
            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                    + ".jpg");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            InputStream inputstream = client.getInputStream();
            copyFile(inputstream, new FileOutputStream(f));
            serverSocket.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    private void copyFile(InputStream inputstream, FileOutputStream fileOutputStream) {
        try{
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputstream.read(buf)) > 0) {
                fileOutputStream.write(buf, 0, len);
            }
            inputstream.close();
            fileOutputStream.close();
        }
        catch (Exception e){


        }
    }

    public ServerAsyncTask(Context context) {
        this.context = context;
    }
}
