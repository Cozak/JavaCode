/*
* Copyright 2014 Dell Inc.
* ALL RIGHTS RESERVED.
*
* This software is the confidential and proprietary information of
* Dell Inc. ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only in
* accordance with the terms of the license agreement you entered
* into with Dell Inc.
*
* DELL INC. MAKES NO REPRESENTATIONS OR WARRANTIES
* ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS
* OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
* WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
* PARTICULAR PURPOSE, OR NON-INFRINGEMENT. DELL SHALL
* NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
* AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
* THIS SOFTWARE OR ITS DERIVATIVES.
*/

package com.dell.prototype.apm.instrumentation;

import com.dell.prototype.apm.model.Metric;
import com.dell.prototype.apm.model.MetricValue;
import com.dell.prototype.apm.model.javaee.JVM;
import com.sun.management.OperatingSystemMXBean;

import javax.management.MBeanServer;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;

public class JVMCollector {

    private static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private static RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    private static OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static List<GarbageCollectorMXBean> gcMbeans = ManagementFactory.getGarbageCollectorMXBeans();

    public static JVM collect() {
        JVM jvm = new JVM(runtime.getName(), runtime.getVmVersion(), operatingSystemMXBean.getArch());

        Metric<JVM> usedHeap = new Metric<JVM>("usedHeap", jvm);
        MetricValue usedHeapValue = new MetricValue();
        usedHeapValue.setValue(memoryMXBean.getHeapMemoryUsage().getUsed());
        usedHeap.addMetricValue(usedHeapValue);
        jvm.setUsedHeap(usedHeap);

        Metric<JVM> committedHeap = new Metric<JVM>("committedHeap", jvm);
        MetricValue committedHeapValue = new MetricValue();
        committedHeapValue.setValue(memoryMXBean.getHeapMemoryUsage().getCommitted());
        committedHeap.addMetricValue(usedHeapValue);
        jvm.setCommittedHeap(committedHeap);

        Metric<JVM> threadsCount = new Metric<JVM>("threadsCount", jvm);
        MetricValue threadsCountValue = new MetricValue();
        threadsCountValue.setValue(threadMXBean.getThreadCount());
        threadsCount.addMetricValue(usedHeapValue);
        jvm.setThreadsCount(threadsCount);

        Metric<JVM> avgThreadCpuTime = new Metric<JVM>("avgThreadCpuTime", jvm);
        MetricValue avgThreadCpuTimeValue = new MetricValue();
        avgThreadCpuTimeValue.setValue(operatingSystemMXBean.getProcessCpuTime());
        avgThreadCpuTime.addMetricValue(avgThreadCpuTimeValue);
        jvm.setAvgThreadCpuTime(avgThreadCpuTime);


        double totalCollectionCount = 0d;
        for (GarbageCollectorMXBean gcMbean : gcMbeans) {
            totalCollectionCount += gcMbean.getCollectionCount();
        }
        double gcRate = totalCollectionCount / (runtime.getUptime() * 1000);
        Metric<JVM> gcOverhead = new Metric<JVM>("gcOverhead", jvm);
        MetricValue gcOverheadValue = new MetricValue();
        gcOverheadValue.setValue(gcRate);
        gcOverhead.addMetricValue(gcOverheadValue);
        jvm.setGcOverhead(gcOverhead);
        return jvm;
    }
}

