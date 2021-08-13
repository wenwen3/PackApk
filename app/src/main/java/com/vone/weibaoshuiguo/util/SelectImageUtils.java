package com.vone.weibaoshuiguo.util;

import android.app.Activity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.vone.qrcode.R;

public class SelectImageUtils  {

    public static SelectImageUtils instance;

    public static SelectImageUtils getInstance(){
        if(instance == null ){
            instance = new SelectImageUtils();
        }

        return instance;
    }
    public static final int IMAGE_REQUESTION_CODE = 456;
    public void selectImage(Activity activity,int num){
        if(num > 1) {
            PictureSelector.create(activity)
                    .openGallery(PictureMimeType.ofImage())
                    .theme(R.style.picture_default_style)
                    .maxSelectNum(num)
                    .minSelectNum(1)
                    .imageSpanCount(3)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//                        .enableCrop(true)// 是否裁剪
//                        .compress(true)// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    .forResult(IMAGE_REQUESTION_CODE);//结果回调onActivityResult code
        }else if(num == 1){
            PictureSelector.create(activity)
                    .openGallery(PictureMimeType.ofImage())
                    .theme(R.style.picture_default_style)
                    .maxSelectNum(1)
                    .minSelectNum(1)
                    .imageSpanCount(3)// 每行显示个数
                    .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//                        .enableCrop(true)// 是否裁剪
//                        .compress(true)// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    .forResult(IMAGE_REQUESTION_CODE);//结果回调onActivityResult code
        }

    }
}
