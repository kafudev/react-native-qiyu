package com.reactnativeqiyu;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;


import androidx.annotation.Nullable;

import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.qiyukf.unicorn.api.UnicornImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Picasso异步加载经常不回调，因此只能用同步加载的方法。
 * Created by weilv on 16/5/20.
 */
public class PicassoImageLoader implements UnicornImageLoader {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private Context context;
    private ExecutorService threadPool;
    private Handler uiHandler;

    public PicassoImageLoader(Context context) {
        this.context = context.getApplicationContext();
        uiHandler = new Handler(Looper.getMainLooper());
        threadPool = Executors.newFixedThreadPool(CPU_COUNT + 1);
    }

    @Nullable
    @Override
    public Bitmap loadImageSync(String uri, int width, int height) {
        return null;
    }

    @Override
    public void loadImage(final String uri, final int width, final int height, final ImageLoaderListener listener) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                RequestCreator requestCreator = Picasso
                        .with(context)
                        .load(uri)
                        .config(Bitmap.Config.RGB_565);
                if (width > 0 && height > 0) {
                    requestCreator = requestCreator
                            .resize(width, height)
                            .centerCrop();
                }

                Bitmap bitmap = null;
                try {
                    bitmap = requestCreator.get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (listener == null) {
                    return;
                }

                if (bitmap != null && !bitmap.isRecycled()) {
                    final Bitmap finalBitmap = bitmap;
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onLoadComplete(finalBitmap);
                        }
                    });
                } else {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onLoadFailed(null);
                        }
                    });
                }
            }
        });
    }
}