package co.touchlab.dogify.test.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* Copyright 2019 Google LLC.
   SPDX-License-Identifier: Apache-2.0 */
public class LiveDataTestUtil
{
    public static <T> T awaitValue(final LiveData<T> liveData, int timeoutSeconds) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T obj) {
                data[0] = obj;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        latch.await(timeoutSeconds, TimeUnit.SECONDS);
        return (T) data[0];
    }
}