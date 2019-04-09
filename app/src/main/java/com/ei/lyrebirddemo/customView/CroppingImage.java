package com.ei.lyrebirddemo.customView;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.ei.lyrebirddemo.R;
import com.ei.lyrebirddemo.databinding.CroppingImageBinding;

import timber.log.Timber;

public class CroppingImage extends RelativeLayout {

    Context context;
    ImageView croppingImage ;
    public CroppingImage(Context context) {
        super(context);
        init(context);
    }

    public CroppingImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CroppingImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CroppingImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cropping_image, this, true);
        croppingImage = view.findViewById(R.id.croppingImage);
    }

    public void setImageResource(Uri uri ) {
        //TODO deneme
        Glide.with(context)
                .load(uri)
                .into(croppingImage);

        croppingImage.setImageResource(R.drawable.ic_launcher_foreground);
    }

}
