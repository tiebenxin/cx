package com.yanlong.im.user.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityUplaodLogBinding;

import net.cb.cb.library.inter.IUploadListener;
import net.cb.cb.library.utils.FileConfig;
import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.AppActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Liszt
 * @date 2020/7/10
 * Description 日志上传页面
 */
public class UploadLogActivity extends AppActivity {

    private ActivityUplaodLogBinding ui;
    private Calendar calendar;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_uplaod_log);
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        ui.tvTime.setText(year + "-" + month + "-" + day);
        ui.viewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker();
            }
        });

        ui.btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                uploadFile();
            }
        });
    }

    private void uploadFile() {
        File file = checkFileExist();
        if (file != null && file.exists()) {
            String date = getLogDate();
            if (!TextUtils.isEmpty(date)) {
                String zipPath = file.getParent() + "/" + date + ".zip";
                FileUtils.toZip(file, zipPath);
                File zipFile = new File(zipPath);
                if (!zipFile.exists()) {
                    return;
                }
                new UpFileAction().uploadLogFile(zipFile, date, new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {
                        System.out.println("上传日志成功--" + result);
                        ToastUtil.show("上传成功");
                    }

                    @Override
                    public void onFailed() {
                        System.out.println("上传日志--fail");
                        ToastUtil.show("上传失败");
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
            }

        } else {
            ToastUtil.show("日志文件不存在");
        }
    }

    private String getLogDate() {
        if (calendar != null) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
            return dayFormat.format(calendar.getTime());
        }
        return "";
    }

    private File checkFileExist() {
        File log = null;
        File fileDir = new File(FileConfig.PATH_LOG);
        if (fileDir.exists()) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy_MM_dd");
            if (calendar != null) {
                String day = dayFormat.format(calendar.getTime());
                String logFile = FileConfig.PATH_LOG + "log" + day + ".txt";
                log = new File(logFile);
                if (log.exists()) {
                    return log;
                }
            }
        }
        return log;
    }


    private void initTimePicker() {
        calendar = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DATE, -9);

        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(UploadLogActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                day = calendar.get(Calendar.DAY_OF_MONTH);
                ui.tvTime.setText(year + "-" + month + "-" + day);
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setDate(calendar)
                .setRangDate(start, calendar)
                .setCancelText("取消")
                .setCancelColor(Color.parseColor("#878787"))
                .setSubmitText("确定")
                .setSubmitColor(Color.parseColor("#32b152"))
                .build();

        pvTime.show();
    }
}
