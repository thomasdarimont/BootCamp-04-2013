package de.wombatsoftware.TweetCamp.timer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import de.wombatsoftware.TweetCamp.qualifier.PerformanceLog;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class LoggingTimer {
    @Inject
    private Logger logger;

    private List<String> logs = new CopyOnWriteArrayList<String>();
    private List<Long> longLogs = new CopyOnWriteArrayList<Long>();

    @Asynchronous
    public void catchPerformanceLog(@Observes @PerformanceLog String log) {
	logs.add(log);
	
	try {
	    TimeUnit.SECONDS.sleep(20);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    public void catchPerformanceLongLog(@Observes @PerformanceLog Long log) {
	longLogs.add(log);
    }

    @Schedule(minute = "*", hour = "*", second="*/60", persistent = false)
    public void logPerformanceLogs() {
	logger.info("### PerformanceLogging starting up ###");

	for (String s : logs) {
	    logger.info("PerformanceLogging: " + s);
	}

	if (longLogs.size() > 0) {
	    long total = 0L;
	    for (Long l : longLogs) {
		total += l;
	    }

	    logger.info("### Total time: " + total + " | Average time: " + (total / longLogs.size()) + " ###");
	}

	logs.clear();
	longLogs.clear();

	logger.info("### PerformanceLogging finished ###");
	
//	try {
//	    Thread.sleep(1000 * 60 * 2);
//	} catch (InterruptedException e) {
//	    e.printStackTrace();
//	}
    }
}