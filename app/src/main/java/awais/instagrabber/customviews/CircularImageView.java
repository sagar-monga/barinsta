package awais.instagrabber.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchyInflater;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

public final class CircularImageView extends SimpleDraweeView {
    private final int borderSize = 8;
    private int color = Color.TRANSPARENT;
    private final Paint paint = new Paint();
    private final Paint paintBorder = new Paint();
    private BitmapShader shader;
    private Bitmap bitmap;

    public CircularImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context);
        setHierarchy(hierarchy);
        setup();
    }

    public CircularImageView(final Context context) {
        super(context);
        inflateHierarchy(context, null);
        setup();
    }

    public CircularImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        inflateHierarchy(context, attrs);
        setup();
    }

    public CircularImageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateHierarchy(context, attrs);
        setup();
    }

    protected void inflateHierarchy(Context context, @Nullable AttributeSet attrs) {
        Resources resources = context.getResources();
        final RoundingParams roundingParams = RoundingParams.asCircle();
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(resources)
                .setRoundingParams(roundingParams)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        GenericDraweeHierarchyInflater.updateBuilder(builder, context, attrs);
        setAspectRatio(builder.getDesiredAspectRatio());
        setHierarchy(builder.build());
    }

    private void setup() {
        // paint.setAntiAlias(true);
        // paintBorder.setColor(color);
        // paintBorder.setAntiAlias(true);
        //
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //     setOutlineProvider(new ViewOutlineProvider() {
        //         private int viewHeight;
        //         private int viewWidth;
        //
        //         @Override
        //         public void getOutline(final View view, final Outline outline) {
        //             if (viewHeight == 0) viewHeight = getHeight();
        //             if (viewWidth == 0) viewWidth = getWidth();
        //             outline.setRoundRect(borderSize, borderSize, viewWidth - borderSize, viewHeight - borderSize, viewHeight >> 1);
        //         }
        //     });
        // }
        // final GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
        //         .setRoundingParams(RoundingParams.)
        //         .build();
        // setHierarchy(hierarchy);
        // invalidate();
    }

    // @Override
    // public void onDraw(final Canvas canvas) {
    //     final BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
    //     if (bitmapDrawable != null) {
    //         final Bitmap prevBitmap = bitmap;
    //         bitmap = bitmapDrawable.getBitmap();
    //         final boolean changed = prevBitmap != bitmap;
    //         if (bitmap != null) {
    //             final int width = getWidth();
    //             final int height = getHeight();
    //
    //             if (shader == null || changed) {
    //                 shader = new BitmapShader(Bitmap.createScaledBitmap(bitmap, width, height, true), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    //                 paint.setShader(shader);
    //             }
    //
    //             if (changed) color = 0;
    //             paintBorder.setColor(color);
    //
    //             final int circleCenter = (width - borderSize) / 2;
    //             final int position = circleCenter + (borderSize / 2);
    //             canvas.drawCircle(position, position, position - 4.0f, paintBorder);
    //             canvas.drawCircle(position, position, circleCenter - 4.0f, paint);
    //         }
    //     }
    // }
    //
    // @Override
    // protected void onAttachedToWindow() {
    //     super.onAttachedToWindow();
    //     setLayerType(LAYER_TYPE_HARDWARE, null);
    // }
    //
    // @Override
    // protected void onDetachedFromWindow() {
    //     setLayerType(LAYER_TYPE_NONE, null);
    //     super.onDetachedFromWindow();
    // }

    public void setStoriesBorder() {
        this.color = Color.GREEN;
        // invalidate();
        // final RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        //
        RoundingParams roundingParams = getHierarchy().getRoundingParams();
        if (roundingParams == null) {
            roundingParams = RoundingParams.asCircle().setRoundingMethod(RoundingParams.RoundingMethod.BITMAP_ONLY);
        }
        roundingParams.setBorder(color, 5.0f);
        getHierarchy().setRoundingParams(roundingParams);
    }
}