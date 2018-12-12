package cn.yuntk.radio.ibook.common.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.bean.GlideInfo;
import cn.yuntk.radio.ibook.util.GlobalApp;

/**
 * 创建时间:2018/4/3
 * 创建人: yb
 * 描述:glide管理类
 */

public class GlideManager {
    public static void loadImage(GlideInfo glideInfo) {
        loadImage(GlobalApp.APP,glideInfo);
    }

    public static void loadImage(Context context, GlideInfo glideInfo) {
        if (glideInfo == null) {
            throw new RuntimeException("glide info cannot be null");
        }
        DrawableRequestBuilder request = null;
        if(glideInfo.isLocalImage()){
            request = Glide.with(context).load(glideInfo.getResourceId()).dontTransform().dontAnimate();
        }else{
            request = Glide.with(context).load(glideInfo.getUrl()).dontTransform().dontAnimate();
        }

        ImageView target = glideInfo.getTarget();
        if (target == null) throw new RuntimeException("target cannot be null");
        execute(context,request,glideInfo);
    }

    private static void execute(Context context, DrawableRequestBuilder builder, GlideInfo glideInfo) {
        if (glideInfo.isCircle()) {
            builder.placeholder(R.mipmap.icon_default_head)
                    .error(R.mipmap.icon_default_head);
            if (glideInfo.isCenterCrop()) {
                builder = builder.transform(new CenterCrop(context),new GlideCircleTransform(context));
            } else {
                builder = builder.transform(new GlideCircleTransform(context));
            }
        } else if (glideInfo.isRound()) {
            builder.placeholder(R.mipmap.bg_common_place_holder)
                    .error(R.mipmap.bg_common_place_holder);
            if (glideInfo.isCenterCrop()) {
                builder = builder.transform(new CenterCrop(context),new GlideRoundTransform(context,glideInfo.getRoundSize()));
            } else {
                builder = builder.transform(new GlideRoundTransform(context,glideInfo.getRoundSize()));
            }
        }  else {
            builder.placeholder(R.mipmap.bg_common_place_holder)
                    .error(R.mipmap.bg_common_place_holder);
            if (glideInfo.isCenterCrop()) {
                builder = builder.transform(new CenterCrop(context));
            }
        }
        builder.diskCacheStrategy(DiskCacheStrategy.ALL).into(glideInfo.getTarget());
    }
}
