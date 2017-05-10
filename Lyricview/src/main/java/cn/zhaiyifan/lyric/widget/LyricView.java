package cn.zhaiyifan.lyric.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import cn.zhaiyifan.lyric.LyricUtils;
import cn.zhaiyifan.lyric.model.Lyric;

/**
 * A Scrollable TextView which use lyric stream as input and display it.
 * <p/>
 * Created by yifan on 5/13/14.
 */
public class LyricView extends TextView implements Runnable {
    public Lyric lyric;
    public final static String ERROR_CONTENT = "Không tìm thấy lời của bài hát này";
    private static final int DY = 70;
    private Thread thread;
    private Paint mCurrentPaint;
    private Paint mPaint;
    private float mMiddleX;
    private float mMiddleY;
    private int mHeight;
    private long ts;
    private float mCurrentIndexY;

    private int mLyricIndex = 0;
    private int mLyricSentenceLength;

    public LyricView(Context context) {
        this(context, null);
    }

    public LyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);

        int backgroundColor = Color.TRANSPARENT;
        int highlightColor = Color.WHITE;
        int normalColor = Color.WHITE;

        setBackgroundColor(backgroundColor);

        // Non-highlight part
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(45);
        mPaint.setColor(normalColor);

        // highlight part, current lyric
        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setColor(highlightColor);
        mCurrentPaint.setTextSize(60);
        mCurrentPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        mPaint.setTextAlign(Paint.Align.CENTER);
        mCurrentPaint.setTextAlign(Paint.Align.CENTER);

    }

    private int drawText(Canvas canvas, Paint paint, String text, float startY) {
        int line = 0;
        float textWidth = mPaint.measureText(text);
        final int width = getWidth() - 85;
        if (textWidth > width) {
            int length = text.length();
            int startIndex = 0;
            int endIndex = Math.min((int) ((float) length * (width / textWidth)), length - 1);
            int perLineLength = endIndex - startIndex;

            LinkedList<String> lines = new LinkedList<>();
            lines.add(text.substring(startIndex, endIndex));
            while (endIndex < length - 1) {
                startIndex = endIndex;
                endIndex = Math.min(startIndex + perLineLength, length - 1);
                lines.add(text.substring(startIndex, endIndex));
            }
            int linesLength = lines.size();
            for (String str : lines) {
                ++line;
                float y;
                if (startY < mMiddleY) {
                    y = startY - (linesLength - line) * DY;
                } else{
                    y = startY + (line - 1) * DY;
                }
                canvas.drawText(str, mMiddleX, y, paint);

            }
        } else {
            ++line;
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, mMiddleX, startY, paint);
        }
        return line;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyric == null){
            return;
        }

        if(lyric.sentenceList.size() < 1)
            return;

        if (lyric.sentenceList.get(0).content.equals(ERROR_CONTENT)) {
            drawText(canvas, mPaint, ERROR_CONTENT, mMiddleY);
            return;
        }
        List<Lyric.Sentence> sentenceList = lyric.sentenceList;
        if (sentenceList == null || sentenceList.isEmpty() || mLyricIndex < 0) {
            return;
        }

        float currY1, currY2;

            // Current line with highlighted color
        currY1 = mCurrentIndexY - DY;
        if (mCurrentIndexY < mMiddleY){
            mCurrentIndexY += DY  * drawText(
                    canvas, mCurrentPaint, sentenceList.get(mLyricIndex).content, mCurrentIndexY);
            currY2 = mCurrentIndexY;
        } else{
            currY2 = mCurrentIndexY + DY * drawText(
                    canvas, mCurrentPaint, sentenceList.get(mLyricIndex).content, mCurrentIndexY);
        }


        // Draw sentences afterwards
        int size = sentenceList.size();
        for (int i = mLyricIndex + 1; i < size; i++) {
            if (currY2 > mHeight) {
                break;
            }
            // Draw and Move down
            currY2 += DY * drawText(canvas, mPaint, sentenceList.get(i).content, currY2);
            // canvas.translate(0, DY);
        }

        currY1 -= 10;
        // Draw sentences before current one
        for (int i = mLyricIndex - 1; i >= 0; i--) {
            if (currY1 < 0) {
                break;
            }
            // Draw and move upwards
                currY1 -= DY * drawText(canvas, mPaint, sentenceList.get(i).content, currY1);
            // canvas.translate(0, DY);
        }


    }

    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        mMiddleX = w * 0.5f; // remember the center of the screen
        mHeight = h;
        mMiddleY = h * 0.5f;
    }

    public long updateIndex(long time) {
        // Current index is last sentence
        if (mLyricIndex >= mLyricSentenceLength - 1) {
            mLyricIndex = mLyricSentenceLength - 1;
            return -1;
        }

        // Get index of sentence whose timestamp is between its startTime and currentTime.
        mLyricIndex = LyricUtils.getSentenceIndex(lyric, time, mLyricIndex, lyric.offset);

        // New current index is last sentence
        if (mLyricIndex >= mLyricSentenceLength - 1) {
            mLyricIndex = mLyricSentenceLength - 1;
            return -1;
        }

        return lyric.sentenceList.get(mLyricIndex + 1).fromTime + lyric.offset;
    }

    public synchronized void setLyric(Lyric lyric, boolean resetIndex) {
        this.lyric = lyric;
        if (lyric != null) {
            mLyricSentenceLength = this.lyric.sentenceList.size();
        }
        if (resetIndex) {
            mLyricIndex = 0;
        }
    }

    public void setLyricIndex(int index) {
        mLyricIndex = index;
        mCurrentIndexY = DY;
    }

    public String getCurrentSentence() {
        if (mLyricIndex >= 0 && mLyricIndex < mLyricSentenceLength) {
            return lyric.sentenceList.get(mLyricIndex).content;
        }
        return null;
    }

    public synchronized void setLyric(Lyric lyric) {
        setLyric(lyric, true);
    }

    public void play(long time) {
        mStop = false;
        updateTime(time);
        thread = new Thread(this);
        thread.start();
    }

    public void pause(){
        mStop = true;
    }
    public void updateTime(long time){
        ts = updateIndex(time);
    }

    public void stop() {
        mStop = true;
        invalidate();
    }

    private boolean mStop = false;
    private boolean mIsForeground = true;
    private long mNextSentenceTime = -1;

    private Handler mHandler = new Handler();

    @Override
    public void run() {
        while (mLyricIndex != -2) {
            if (mStop) {
                return;
            }
            if (lyric.sentenceList.size() <=0) {
                mStop = true;
//                return;
            } else if (lyric.sentenceList.size() >0 && lyric.sentenceList.get(0).content.equals(ERROR_CONTENT)){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
                mStop = true;
                return;
            }
            if (mNextSentenceTime != ts){
                mNextSentenceTime = ts;
                // Redraw only when window is visible
                if (mIsForeground) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                }
            }
            if (mLyricIndex == lyric.sentenceList.size() - 1) {
                mStop = true;
            }
        }
    }
}