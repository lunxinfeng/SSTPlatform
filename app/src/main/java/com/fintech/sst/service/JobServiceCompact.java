//package com.fintech.sst.service;
//
//import android.app.job.JobInfo;
//import android.app.job.JobScheduler;
//import android.content.ComponentName;
//import android.content.Context;
//
//import com.fintech.sst.App;
//
//
//public class JobServiceCompact {
//
//    //启动心跳
//    public static void startJob(int delayMillis) {
//        JobInfo.Builder builder = new JobInfo.Builder(1001, new ComponentName(App.getAppContext(), HeartJobService.class));
//        builder.setMinimumLatency(delayMillis); //执行的最小延迟时间
//        builder.setOverrideDeadline(delayMillis);  //执行的最长延时时间
//        builder.setBackoffCriteria(delayMillis, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
//
//        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//        //是否需要设备空闲状态
//        builder.setRequiresDeviceIdle(false);
//        //是否需要充电
//        builder.setRequiresCharging(false);
////Can't call setOverrideDeadline() on a periodic job.
////        builder.setPeriodic(30 * 1000, 30 * 1000);
//
//        builder.setPersisted(true);  // 设置设备重启时，执行该任务
//
//        // Extras, work duration.
////        PersistableBundle extras = new PersistableBundle();
////        builder.setExtras(extras);
//
//        // Schedule job
//        JobScheduler tm = (JobScheduler) App.getAppContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        tm.schedule(builder.build());
//    }
//
//    //取消心跳
//    public static void cancelAllJobs(Context context) {
//        JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        tm.cancel(1001);
//    }
//
//}
