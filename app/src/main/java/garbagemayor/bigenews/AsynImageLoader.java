package garbagemayor.bigenews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 异步图片加载器
 */
public class AsynImageLoader {

    private static final String TAG = "AsynImageLoaderTag";

    //单例
    private static AsynImageLoader instance = new AsynImageLoader();

    private Map<String, SoftReference<Bitmap>> caches;
    private List<Task> taskQueue;
    private boolean isRunning = false;

    private static class Task {
        public String path;
        public Bitmap bitmap;
        public ImageCallback callback;

        @Override
        public boolean equals(Object o) {
            Task task = (Task) o;
            return task.path.equals(this.path);
        }
    }

    private AsynImageLoader() {
        //初始化变量
        caches = new HashMap<String, SoftReference<Bitmap>>();
        taskQueue = new ArrayList<Task>();
        //启动图片下载线程
        isRunning = true;
        new Thread(runnable).start();
    }

    public static AsynImageLoader getnstance() {
        return instance;
    }

    /**
     * @param imageView 需要延迟加载图片的对象
     * @param url 图片的URL地址
     * @param resId 图片加载过程中显示的图片资源
     */
    public void showImageAsyn(ImageView imageView, String url, int resId) {
        imageView.setTag(url);
        Bitmap bitmap = loadImageAsyn(url, getImageCallback(imageView, resId));
        if (bitmap == null) {
            imageView.setImageResource(resId);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public Bitmap loadImageAsyn(String path, ImageCallback callback) {
        //判断缓存中是否已经存在该图片
        if (caches.containsKey(path)) {
            //取出软引用
            SoftReference<Bitmap> rf = caches.get(path);
            //通过软引用，获取图片
            Bitmap bitmap = rf.get();
            //如果该图片已经被释放，则将该path对应的键从Map中移除掉
            if (bitmap == null) {
                caches.remove(path);
            } else {
                // 如果图片未被释放，直接返回该图片
                Log.d(TAG, "return image in cache" + path);
                return bitmap;
            }
        } else {
            // 如果缓存中不常在该图片，则创建图片下载任务
            Task task = new Task();
            task.path = path;
            task.callback = callback;
            Log.d(TAG, "new Task ," + path);
            if (!taskQueue.contains(task)) {
                taskQueue.add(task);
                // 唤醒任务下载队列
                synchronized (runnable) {
                    runnable.notify();
                }
            }
        }
        // 缓存中没有图片则返回null
        return null;
    }

    /**
     * @param imageView
     * @param resId     图片加载完成前显示的图片资源ID
     * @return
     */
    private ImageCallback getImageCallback(final ImageView imageView, final int resId) {
        return new ImageCallback() {
            @Override
            public void loadImage(String path, Bitmap bitmap) {
                if (path.equals(imageView.getTag().toString())) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(resId);
                }
            }
        };
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 子线程中返回的下载完成的任务
            Task task = (Task) msg.obj;
            // 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
            task.callback.loadImage(task.path, task.bitmap);
        }
    };

    public static Bitmap getBitmapFromUrl(String urlStr) {
        if (instance.caches.containsKey(urlStr)) {
            return instance.caches.get(urlStr).get();
        }
        Bitmap bitmap = null;
        try {
            URL imgUrl = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(5000);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                // 当队列中还有未处理的任务时，执行下载任务
                while (taskQueue.size() > 0) {
                    // 获取第一个任务，并将之从任务队列中删除
                    Task task = taskQueue.remove(0);
                    // 将下载的图片添加到缓存
                    task.bitmap = getBitmapFromUrl(task.path);
                    caches.put(task.path, new SoftReference<Bitmap>(task.bitmap));
                    if (handler != null) {
                        // 创建消息对象，并将完成的任务添加到消息对象中
                        Message msg = handler.obtainMessage();
                        msg.obj = task;
                        // 发送消息回主线程
                        handler.sendMessage(msg);
                    }
                }
                //如果队列为空,则令线程等待
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    //回调接口
    public interface ImageCallback {
        void loadImage(String path, Bitmap bitmap);
    }
}
