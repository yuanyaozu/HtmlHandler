package com.example.joker.htmlhandler;

import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Stack;

public class HtmlTagHandler implements Html.TagHandler {
    public static final String TAG_CUSTOM_FONT = "customfont";

    private Stack<HashMap<String, String>> queut = new Stack<>();
    final Stack<Integer> startIndex = new Stack<>();
    final Stack<SpanRecordEntity> recordEntities = new Stack<>();


    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

        if (tag.equalsIgnoreCase(TAG_CUSTOM_FONT)) {
            processAttributes(xmlReader);
            if (opening) {
                startFont(tag, output, xmlReader);
            } else {
                endFont(tag, output, xmlReader);
            }
        }
    }


    //开始解释标签
    public void startFont(String tag, Editable output, XMLReader xmlReader) {
        int length = output.length();
        startIndex.push(length);
    }

    //结束解释标签
    public void endFont(String tag, Editable output, XMLReader xmlReader) {
        int endLegth = output.length();

        try {
            Integer startIndex = HtmlTagHandler.this.startIndex.pop();
            HashMap<String, String> attributes = queut.pop();
            //获取想要的标签属性，给富文本设置属性
            String color = attributes.get("color");
            String size = attributes.get("size");
            if (this.startIndex.size() > 0){
                recordEntities.push(new SpanRecordEntity(startIndex,endLegth,color,size));
            }else {
                if (!TextUtils.isEmpty(color)) {
                    output.setSpan(new ForegroundColorSpan(Color.parseColor(color)), startIndex, endLegth, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (!TextUtils.isEmpty(size)) {
                    size = size.split("px")[0];
                    output.setSpan(new AbsoluteSizeSpan((Integer.parseInt(size)), true), startIndex, endLegth, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                while (recordEntities.size() > 0){
                    SpanRecordEntity spanRecordEntity = recordEntities.pop();
                    int recordStartIndex = spanRecordEntity.getStartIndex();
                    int recordEndIndex = spanRecordEntity.getEndIndex();
                    String recordColor = spanRecordEntity.getColor();
                    String spandSize = spanRecordEntity.getTextSize();
                    if (!TextUtils.isEmpty(recordColor)) {
                        output.setSpan(new ForegroundColorSpan(Color.parseColor(recordColor)), recordStartIndex, recordEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (!TextUtils.isEmpty(spandSize)) {
                        spandSize = spandSize.split("px")[0];
                        output.setSpan(new AbsoluteSizeSpan((Integer.parseInt(spandSize)), true), recordStartIndex, recordEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }catch (Exception e){
        }
    }

    private void processAttributes(final XMLReader xmlReader) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);

            HashMap<String, String> attributes = new HashMap<>();
            for (int i = 0; i < len; i++) {
                attributes.put(data[i * 5 + 1], data[i * 5 + 4]);
            }
            queut.push(attributes);
        } catch (Exception e) {

        }
    }

    class SpanRecordEntity{
        int startIndex;
        int endIndex;
        String color;
        String textSize;

        public SpanRecordEntity(int startIndex, int endIndex, String color, String textSize) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.color = color;
            this.textSize = textSize;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public String getColor() {
            return color;
        }

        public String getTextSize() {
            return textSize;
        }
    }

}