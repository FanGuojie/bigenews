package garbagemayor.bigenews;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bm.library.PhotoView;

import java.util.Random;

public class BigPicturePopupWindow {

    public static String TAG = "BigPicturePopupWindowTag";

    private Context mContext;
    private PopupWindow mPopupWindow;
    private PhotoView mPhotoView;
    private View mBaseView;

    public BigPicturePopupWindow(Context context, View baseView) {
        mContext = context;
        mBaseView = baseView;
        Log.d(TAG, "设置点击图片查看大图的popupWindow的属性");
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.view_big_picture_layout, null);
        mPopupWindow = new PopupWindow(contentView, GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xDF000000));
        mPopupWindow.setAnimationStyle(R.style.BigPictureTranslateAnimation);
        //设置大图PhotoView的特性
        Log.d(TAG, "设置大图PhotoView的特性");
        mPhotoView = (PhotoView) contentView.findViewById(R.id.view_big_picture);
        mPhotoView.enable();
        //设置打开大图之后的点击事件：关闭大图
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity =  (MainActivity)mContext;
                mainActivity.closeInputMethodAnyaway();
                mPopupWindow.dismiss();
            }
        });
        //设置打开大图之后的长按事件：存图
        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Random random = new Random();
                String fileName = "";
                for (int i = 0; i < 16; i ++) {
                    fileName = fileName + random.nextInt(10);
                }
                fileName = fileName + ".png";
                Toast.makeText(mContext, "图片以保存到" + "sdcard/BigeNews/Download/image/" + fileName, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void show(ImageView sourceImageView) {
        MainActivity mainActivity =  (MainActivity)mContext;
        mainActivity.closeInputMethodAnyaway();
        mPhotoView.setImageDrawable(sourceImageView.getDrawable());
        mPhotoView.animaFrom(PhotoView.getImageViewInfo(sourceImageView));
        mPhotoView.setAnimaDuring(400);
        mPopupWindow.showAtLocation(mBaseView, Gravity.CENTER, 0, 0);
    }
}
