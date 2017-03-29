/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.handu.poweroperational.utils.thinker;

import com.handu.poweroperational.main.service.PatchResultService;
import com.handu.poweroperational.utils.AppConstant;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.app.ApplicationLike;


/**
 * Created by zhangshaowen on 16/7/3.
 */
public class TinkerManager {
    private static final String TAG = AppConstant.TAG;

    private static ApplicationLike applicationLike;
    private static boolean isInstalled = false;

    public static void setTinkerApplicationLike(ApplicationLike appLike) {
        applicationLike = appLike;
    }

    public static ApplicationLike getTinkerApplicationLike() {
        return applicationLike;
    }


    public static void setUpgradeRetryEnable(boolean enable) {
        UpgradePatchRetry.getInstance(applicationLike.getApplication()).setRetryEnable(enable);
    }


    /**
     * all use default class, simply Tinker install method
     */
    public static void sampleInstallTinker(ApplicationLike appLike) {
        if (isInstalled) {
            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
            return;
        }
        TinkerInstaller.install(appLike);
        isInstalled = true;

    }

    /**
     * you can specify all class you want.
     * sometimes, you can only install tinker in some process you want!
     *
     * @param appLike
     */
    public static void installTinker(ApplicationLike appLike) {
        if (isInstalled) {
            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
            return;
        }
//        //or you can just use DefaultLoadReporter
//        LoadReporter loadReporter = new PopLoadReporter(appLike.getApplication());
//        //or you can just use DefaultPatchReporter
//        PatchReporter patchReporter = new PopPatchReporter(appLike.getApplication());
//        //or you can just use DefaultPatchListener
//        PatchListener patchListener = new PopPatchListener(appLike.getApplication());
//        //you can set your own upgrade patch if you need
//        AbstractPatch upgradePatchProcessor = new UpgradePatch();

//        TinkerInstaller.install(appLike,
//                loadReporter, patchReporter, patchListener,
//                PatchResultService.class, upgradePatchProcessor);

        TinkerInstaller.install(appLike,
                new DefaultLoadReporter(appLike.getApplication()),
                new DefaultPatchReporter(appLike.getApplication()),
                new DefaultPatchListener(appLike.getApplication()),
                PatchResultService.class,
                new UpgradePatch());

        isInstalled = true;
    }
}
