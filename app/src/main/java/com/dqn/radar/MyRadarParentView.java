package com.dqn.radar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

/**
 * 雷达统计图
 * 可以 顺时针, 可以逆时针
 */
public class MyRadarParentView extends FrameLayout {
    private final static String TAG = "MyRadarParentView";

    //是否顺时针
    boolean isClockwise = false;
    //数据个数
    private int count = 5;
    private int countCircle = 6;

    //网格最大半径
    private float radius;
    //中心X
    private float centerX;
    //中心Y
    private float centerY;
    //雷达区画笔
    private Paint mainPaint;

    //阴影
    private Paint mShadowPaint;
    //文本画笔
    private Paint textPaint;
    //小圆圈
    private Paint mSmallDotPaint;
    private Paint mSmallDotBlackPaint;
    //中间透明
    private Paint mCerentAlphaPaint;
    private Paint mCerentAlphaLinePaint;
    //数据区画笔
    private Paint valuePaint;
    //标题文字
    private List<String> titles;
    //各维度分值

    //存放,蜘蛛网,所有点的集合
    private List<PointF> mPointFList;
    //存放,小空心小原点的集合
    private List<PointF> mPointFListDot;
    private List<TextView> mTvList;
    private float mR;
    private ArrayList<Double> mNumDataList;

    //小原点的半径
    private int mSmallDotReaius = 4;
    //弧度
    private float angle;

    //灰色,蜘蛛网
    private int mMainPaintColor = 0XFFCCCCCC;
    //蜘蛛网,灰色部分
    private int mShadowPaintColor = 0x66e0e1e4;
    //文字颜色
    private int mTextColor = 0xFF333333;
    //中部,橘色带有透明
    private int mCerentAplaColor = 0xAAFC7C22;
    //中部橘色,边线
    private int mCerentAplaLineColor = 0xFFFC7C22;


    public MyRadarParentView(Context context) {
        this(context, null);
    }

    public MyRadarParentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRadarParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //显示文字的textview
        mTvList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TextView tv = new TextView(context);
            tv.setTextSize(14);
            tv.setTextColor(mTextColor);
            tv.setText("");
            addView(tv);
            mTvList.add(tv);
        }

        //问题: 自定义view,放入item中, onDraw()方法不执行
        setWillNotDraw(false);

        init();
        initNewData();
    }

    private void init() {
        //雷达区画笔初始化
        mainPaint = new Paint();

        mainPaint.setColor(mMainPaintColor);
        mainPaint.setAntiAlias(true);
        mainPaint.setStrokeWidth(2);
        mainPaint.setStyle(Paint.Style.STROKE);

        //阴影画笔
        mShadowPaint = new Paint();
        mShadowPaint.setColor(mShadowPaintColor);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);

        //文本画笔初始化
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40);
        textPaint.setStrokeWidth(2);
        textPaint.setAntiAlias(true);

        //中间透明
        mCerentAlphaPaint = new Paint();
        mCerentAlphaPaint.setColor(mCerentAplaColor);
        mCerentAlphaPaint.setAntiAlias(true);
        mCerentAlphaPaint.setStyle(Paint.Style.FILL);

        //中间透明边线
        mCerentAlphaLinePaint = new Paint();
        mCerentAlphaLinePaint.setColor(mCerentAplaLineColor);
        mCerentAlphaLinePaint.setAntiAlias(true);
        mCerentAlphaLinePaint.setStrokeWidth(3);
        mCerentAlphaLinePaint.setStyle(Paint.Style.STROKE);

        //小原点
        mSmallDotPaint = new Paint();
        mSmallDotPaint.setColor(Color.RED);
        mSmallDotPaint.setAntiAlias(true);
        mSmallDotPaint.setStrokeWidth(3);
        mSmallDotPaint.setStyle(Paint.Style.STROKE);

        //小原点背景
        mSmallDotBlackPaint = new Paint();
        mSmallDotBlackPaint.setColor(Color.WHITE);
        mSmallDotBlackPaint.setAntiAlias(true);
        mSmallDotBlackPaint.setStyle(Paint.Style.FILL);


        //数据区（分数）画笔初始化
        valuePaint = new Paint();
        valuePaint.setColor(Color.RED);
        valuePaint.setAntiAlias(true);
        valuePaint.setStyle(Paint.Style.FILL);

        titles = new ArrayList<>();

        /*titles.add("语文1111111");
        titles.add("数学222222222");
        titles.add("英语3333333333");
        titles.add("政治44444444444");
        titles.add("历史55555555555");
        titles.add("66666");
        titles.add("7777");
        titles.add("888888");
        titles.add("99999999999");
        titles.add("10000000000000000");*/

        titles.add("");
        titles.add("");
        titles.add("");
        titles.add("");
        titles.add("");
        titles.add("");
        titles.add("");
        titles.add("");
        titles.add("");
        titles.add("");

        count = titles.size();
        mNumDataList = new ArrayList<>();
        /* mNumDataList.add(144);
        mNumDataList.add(42);
        mNumDataList.add(43);
        mNumDataList.add(43);
        mNumDataList.add(52);
        mNumDataList.add(58);
        mNumDataList.add(58);
        mNumDataList.add(70);
        mNumDataList.add(74);
        mNumDataList.add(120);*/


        mPointFList = new ArrayList<>();
        //creatPoint();
        mPointFListDot = new ArrayList<>();
        //creatPointDot();

    }

    /**
     * 创建点
     */
    private void creatPoint() {

        mPointFList.clear();
        for (int i = 1; i < countCircle; i++) {
            float curR = mR * i;

            for (int j = 0; j < count; j++) {
                PointF pointF = new PointF();
                float childAngle = angle * j;

                if (!isClockwise) {
                    //逆时针转
                    childAngle = -childAngle;
                }


                //LogUtils.e(TAG, "当前角度 childAngle=" + childAngle);
                if (j == 0) {
                    float x = (float) (centerX + curR * Math.sin(childAngle));
                    float y = (float) (centerY - curR * Math.cos(childAngle));

                    pointF.x = x;
                    pointF.y = y;
                    //LogUtils.e(TAG," x="+x+", y="+y);
                } else {
                    //根据半径，计算出蜘蛛丝上每个点的坐标

                    float x1 = (float) (centerX + curR * Math.sin(childAngle));
                    float y1 = (float) (centerY - curR * Math.cos(childAngle));
                    pointF.x = x1;
                    pointF.y = y1;
                    //LogUtils.e(TAG," x="+x1+", y="+y1);
                }

                mPointFList.add(pointF);
                //LogUtils.e(TAG,"Polygon 位置="+i+",j="+j+", pointF.x="+pointF.x+", pointF.y="+pointF.y);
            }

        }
    }

    /**
     * 创建小原点
     */
    private void creatPointDot() {

        if (mNumDataList.size() == 0) {
            return;
        }

        //最大
        Double maxData = 0.0;
        for (int i = 0; i < mNumDataList.size(); i++) {
            if (mNumDataList.get(i) > maxData) {
                maxData = mNumDataList.get(i);
            }
        }

        // 半径最长, 数值最大, 算比例
        Double scale = radius / maxData;

        mPointFListDot.clear();

        for (int j = 0; j < mNumDataList.size(); j++) {
            PointF pointF = new PointF();
            float childAngle = angle * j;

            if (!isClockwise) {
                childAngle = -childAngle;
            }

            //算每个点的半径长度
            Double radius = mNumDataList.get(j) * scale;
            float x1 = (float) (centerX + radius * Math.sin(childAngle));
            float y1 = (float) (centerY - radius * Math.cos(childAngle));
            pointF.x = x1;
            pointF.y = y1;
            mPointFListDot.add(pointF);
        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        MyLogUtils.e(TAG, "onSizeChanged");
        radius = Math.min(w, h) / 2 * 0.7f;
        centerX = w / 2;
        centerY = h / 2;
        initNewData();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        MyLogUtils.e(TAG, "dispatchDraw");
        //canvas.save();

        //canvas.drawLine(centerX - 400, centerY, centerX + 400, centerY, mainPaint);
        //canvas.drawLine(centerX, centerY - 400, centerX, centerY + 400, mainPaint);

        drawPolygon(canvas);//绘制蜘蛛网
        drawShadow(canvas);//绘制阴影
        drawLines(canvas);//绘制直线
        drawCerentAlpha(canvas);//绘制中部透明
        drawSmallDot(canvas);//绘制小原点
        //drawTitle(canvas);//绘制标题
        //drawTitle();//绘制标题
        //drawRegion(canvas);//绘制覆盖区域

        //canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        MyLogUtils.e(TAG, "onDraw");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        MyLogUtils.e(TAG, "onLayout");


        if (mPointFList.size() == 0) {
            return;
        }
        MyLogUtils.e(TAG, "onLayout 绘制");
        drawTitle();


    }

    /**
     * 绘制多边形
     *
     * @param canvas
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();

        for (int i = 0; i < mPointFList.size(); i++) {
            PointF pointF = mPointFList.get(i);
            //LogUtils.e(TAG,"Polygon , pointF.x="+pointF.x+", pointF.y="+pointF.y);
            if (i % count == 0) {
                path.close();
                path.moveTo(pointF.x, pointF.y);
            } else {
                path.lineTo(pointF.x, pointF.y);
            }
        }

        path.close();
        canvas.drawPath(path, mainPaint);

    }

    /**
     * 绘制多边形
     *
     * @param canvas
     */
    private void drawShadow(Canvas canvas) {
        Path path = new Path();
        Path path02 = new Path();

        if (mPointFList.size() == 0) {
            return;
        }


        for (int i = 0; i < mPointFList.size(); i++) {
            PointF pointF = mPointFList.get(i);
            //path.reset();
            //LogUtils.e(TAG, "位置=" + i + ", pointF.x=" + pointF.x + ", pointF.y=" + pointF.y);
            if (i == 0) {
                path.moveTo(pointF.x, pointF.y);
            }

            if (i == count) {
                //
                //path.moveTo(pointF01.x,pointF01.y);
                path.close();
                path.moveTo(pointF.x, pointF.y);
            } else if (i < 2 * count && i > 0) {
                path.lineTo(pointF.x, pointF.y);
            }

            if (i == 2 * count) {
                path02.moveTo(pointF.x, pointF.y);
            }

            if (i == 3 * count) {
                path02.close();
                path02.moveTo(pointF.x, pointF.y);
            } else if (i < 4 * count && i > 2 * count) {
                path02.lineTo(pointF.x, pointF.y);
            }
        }
        path.close();
        path02.close();
        path.setFillType(Path.FillType.EVEN_ODD);
        path02.setFillType(Path.FillType.EVEN_ODD);
        canvas.drawPath(path, mShadowPaint);
        canvas.drawPath(path02, mShadowPaint);
    }

    /**
     * 绘制直线
     */
    private void drawLines(Canvas canvas) {
        if (mPointFList.size() == 0) {
            return;
        }
        int size = mPointFList.size();
        Path path = new Path();
        path.reset();
        for (int i = 0; i < count; i++) {
            path.moveTo(centerX, centerY);
            PointF pointF = mPointFList.get(size - (count - i));
            path.lineTo(pointF.x, pointF.y);
        }


        path.close();
        canvas.drawPath(path, mainPaint);
    }

    /**
     * 绘制小原点,中间的透明
     *
     * @param canvas
     */
    private void drawCerentAlpha(Canvas canvas) {
        if (mPointFListDot.size() == 0) {
            return;
        }
        Path path = new Path();
        Path path02 = new Path();

        for (int i = 0; i < mPointFListDot.size(); i++) {
            PointF pointF = mPointFListDot.get(i);
            if (i == 0) {
                path.moveTo(pointF.x, pointF.y);
                path02.moveTo(pointF.x, pointF.y);
            } else {
                path.lineTo(pointF.x, pointF.y);
                path02.lineTo(pointF.x, pointF.y);
            }
        }

        path.close();
        path02.close();
        canvas.drawPath(path, mCerentAlphaPaint);
        canvas.drawPath(path02, mCerentAlphaLinePaint);

        //canvas.draw

    }

    /**
     * 绘制小原点
     *
     * @param canvas
     */
    private void drawSmallDot(Canvas canvas) {
        if (mPointFListDot.size() == 0) {
            return;
        }

        for (int i = 0; i < mPointFListDot.size(); i++) {
            PointF pointF = mPointFListDot.get(i);

            canvas.drawCircle(pointF.x, pointF.y, mSmallDotReaius, mSmallDotPaint);
            canvas.drawCircle(pointF.x, pointF.y, mSmallDotReaius, mSmallDotBlackPaint);
        }

        //path.lineTo(x2, y2);

        //canvas.draw

    }

    /**
     * 绘制标题文字
     *
     * @param canvas
     */
    private void drawTitle(Canvas canvas) {

        if (titles.size() == 0) {
            return;
        }

        if (count != titles.size()) {
            return;
        }

        if (mPointFList.size() == 0) {
            return;
        }


        int size = mPointFList.size();
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;//标题高度


        int textDisPadding = 30;
        for (int i = 0; i < count; i++) {


            PointF pointF = mPointFList.get(size - (count - i));
            //canvas.drawText(titles.get(i), pointF.x, pointF.y - fontHeight / 5, textPaint);
            String nameStr = titles.get(i);
            float nameStrWidth = textPaint.measureText(nameStr);

            float nameStrWidthHalf = nameStrWidth * 1f / 2;
            if (pointF.x >= centerX && pointF.y <= centerY) {
                //第一象限
                //
                if (i == 0 && isFirstCerentBoolean()) {
                    canvas.drawText(nameStr,
                            pointF.x,
                            pointF.y - fontHeight / 5 - textDisPadding,
                            textPaint);
                } else {
                    canvas.drawText(nameStr,
                            pointF.x + nameStrWidthHalf + textDisPadding,
                            pointF.y - fontHeight / 5,
                            textPaint);
                }

            } else if (pointF.x >= centerX && pointF.y > centerY) {
                //第二象限
                if (isBottomCerentBoolean(i)) {
                    canvas.drawText(nameStr,
                            pointF.x,
                            pointF.y + fontHeight + textDisPadding,
                            textPaint);
                } else {
                    canvas.drawText(nameStr,
                            pointF.x + nameStrWidthHalf + textDisPadding,
                            pointF.y + fontHeight,
                            textPaint);
                }

            } else if (pointF.x < centerX && pointF.y >= centerY) {
                //第三象限
                canvas.drawText(nameStr,
                        pointF.x - nameStrWidthHalf - textDisPadding,
                        pointF.y + fontHeight,
                        textPaint);
            } else if (pointF.x < centerX && pointF.y < centerY) {
                //第四象限
                canvas.drawText(nameStr,
                        pointF.x - nameStrWidthHalf - textDisPadding,
                        pointF.y - fontHeight / 5,
                        textPaint);
            }

        }

    }

    /**
     * 绘制标题文字
     */
    private void drawTitle() {
        for (int i = 0; i < mTvList.size(); i++) {
            mTvList.get(i).setText("");
            mTvList.get(i).setOnClickListener(null);
        }


        if (titles.size() == 0) {
            return;
        }

        if (count != titles.size()) {
            return;
        }

        if (mPointFList.size() == 0) {
            return;
        }


        int size = mPointFList.size();
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;//标题高度

        //默认间距,防止Ui,距离太近
        int textDisPadding = 30;

        for (int i = 0; i < count; i++) {

            TextView textView = mTvList.get(i);

            PointF pointF = mPointFList.get(size - (count - i));

            //扭shizhe时针
            //canvas.drawText(titles.get(i), pointF.x, pointF.y - fontHeight / 5, textPaint);
            String nameStr = titles.get(i);

            float nameStrWidth = textPaint.measureText(nameStr);

            float nameStrWidthHalf = nameStrWidth * 1f / 2;

            textView.setText(nameStr);

            float mNomalStartX = pointF.x - nameStrWidthHalf;
            float mNomalendX = pointF.x + nameStrWidthHalf;

            if (pointF.x >= centerX && pointF.y <= centerY) {
                //第一象限
                //
                if (i == 0 && isFirstCerentBoolean()) {

                    int l = (int) (mNomalStartX);
                    int t = (int) (pointF.y - fontHeight - textDisPadding);
                    int r = (int) (mNomalendX);
                    int b = (int) (pointF.y - textDisPadding);

                    textView.layout(l, t, r, b);
                } else {


                    int l = (int) (pointF.x + textDisPadding);
                    int t = (int) (pointF.y - fontHeight);
                    int r = (int) (l + nameStrWidth);
                    int b = (int) (t + fontHeight);

                    textView.layout(l, t, r, b);
                }

            } else if (pointF.x >= centerX && pointF.y > centerY) {
                //第二象限
                if (isBottomCerentBoolean(i)) {
                    /*canvas.drawText(nameStr,
                            pointF.x,
                            pointF.y + fontHeight + textDisPadding,
                            textPaint);*/

                    int l = (int) (pointF.x - nameStrWidthHalf);
                    int t = (int) (pointF.y + textDisPadding);
                    int r = (int) (l + nameStrWidth);
                    int b = (int) (t + fontHeight);

                    textView.layout(l, t, r, b);
                } else {
                   /* canvas.drawText(nameStr,
                            pointF.x + nameStrWidthHalf + textDisPadding ,
                            pointF.y + fontHeight ,
                            textPaint);*/


                    int l = (int) (pointF.x + textDisPadding);
                    int t = (int) (pointF.y);
                    int r = (int) (l + nameStrWidth);
                    int b = (int) (t + fontHeight);

                    textView.layout(l, t, r, b);
                }

            } else if (pointF.x < centerX && pointF.y >= centerY) {
                //第三象限
                /*canvas.drawText(nameStr,
                        pointF.x - nameStrWidthHalf - textDisPadding ,
                        pointF.y + fontHeight ,
                        textPaint);*/

                int l = (int) (pointF.x - nameStrWidth - textDisPadding);
                int t = (int) (pointF.y);
                int r = (int) (l + nameStrWidth);
                int b = (int) (t + fontHeight);

                textView.layout(l, t, r, b);
            } else if (pointF.x < centerX && pointF.y < centerY) {
                //第四象限
                int l = (int) (pointF.x - nameStrWidth - textDisPadding);
                int t = (int) (pointF.y - fontHeight);
                int r = (int) (l + nameStrWidth);
                int b = (int) (t + fontHeight);

                textView.layout(l, t, r, b);
            }

            textView.setTag(i);
            textView.setOnClickListener(tvOnClicker);

        }

    }

    /**
     * 第一个数据在中心
     *
     * @return
     */
    private boolean isFirstCerentBoolean() {
        return count == 3 || count == 5 || count == 6 || count == 7 || count == 8 || count == 9 || count == 10;
    }

    /**
     * 底部中间
     *
     * @param i
     * @return
     */
    private boolean isBottomCerentBoolean(int i) {

        if (count == 6 || count == 8 || count == 10) {
            return i == count / 2;
        }
        return false;
    }


    //设置各门得分
    public void setNumData(List<Double> numData) {
        if (numData == null || numData.size() == 0) {
            return;
        }
        this.mNumDataList.clear();
        this.mNumDataList.addAll(numData);
        initNewData();
        postInvalidate();
    }


    public void setStrData(ArrayList<String> data) {
        if (data == null || data.size() == 0) {
            return;
        }

        this.titles.clear();
        this.titles.addAll(data);

        initNewData();
        postInvalidate();
    }

    /**
     * 是否顺时针
     */
    public void setclockwise(boolean isClockwise) {
        this.isClockwise = isClockwise;
    }

    /**
     * 计算点的数据
     */
    private void initNewData() {

        //一旦size发生改变，重新绘制
        count = titles.size();

        //1度=1*PI/180   360度=2*PI   那么我们每旋转一次的角度为2*PI/内角个数
        //中心与相邻两个内角相连的夹角角度
        angle = (float) (2 * Math.PI / count);
        //LogUtils.e(TAG, "当前角度 angle=" + angle);
        //每个蛛丝之间的间距
        mR = radius / (countCircle - 1);
        //LogUtils.e(TAG, "当前间距 r=" + r);

        creatPoint();
        creatPointDot();
        postInvalidate();
    }


    //文字点击监听
    OnClickListener tvOnClicker = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener == null) {
                return;
            }

            mListener.onClick((Integer) v.getTag());
        }
    };
    private OnListener mListener;

    public interface OnListener {
        void onClick(int position);
    }

    public void setListener(OnListener listener) {
        mListener = listener;
    }
}