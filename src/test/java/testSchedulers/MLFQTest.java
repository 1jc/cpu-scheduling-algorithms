package testSchedulers;

import org.junit.jupiter.api.Test;
import schedulers.MLFQ;
import schedulers.MLQ;
import utilities.PerformanceMetricGenerator;
import utilities.Process;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MLFQTest {

    @Test
    public void mlfqTest() throws Exception {
        Process p1 = new Process("P1", List.of(10));
        Process p2 = new Process("P2", List.of(3));
        Process p3 = new Process("P3", List.of(7));
        Process p4 = new Process("P4", List.of(4));
        p1.setArrivalTime(0);
        p2.setArrivalTime(1);
        p3.setArrivalTime(3);
        p4.setArrivalTime(5);
        MLFQ mlfq = new MLFQ();
        mlfq.loadProcesses(List.of(p1, p2, p3, p4));
        mlfq.executeProcesses(false);
        PerformanceMetricGenerator pmg = mlfq.generatePerformanceMetrics();

        assertEquals(100.0, pmg.getCpuUtilization(), 5);
        assertEquals(6.5, pmg.getResponseTimeAverage(), 4);
        assertEquals(11.75, pmg.getWaitingTimeAverage(), 4);
        assertEquals(17.75, pmg.getTurnaroundTimeAverage(), 4);
    }
}