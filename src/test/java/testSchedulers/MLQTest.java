package testSchedulers;

import org.junit.jupiter.api.Test;
import schedulers.MLQ;
import schedulers.SJF;
import utilities.PerformanceMetricGenerator;
import utilities.Process;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MLQTest {

    @Test
    public void mlqTest() throws Exception {
        Process p1 = new Process("P1", List.of(10));
        Process p2 = new Process("P2", List.of(3));
        Process p3 = new Process("P3", List.of(7));
        Process p4 = new Process("P4", List.of(4));
        p1.setArrivalTime(0);
        p2.setArrivalTime(1);
        p3.setArrivalTime(3);
        p4.setArrivalTime(5);
        MLQ mlq = new MLQ(5,73);
        mlq.loadProcesses(List.of(p1, p2, p3, p4));
        mlq.executeProcesses(false);
        PerformanceMetricGenerator pmg = mlq.generatePerformanceMetrics();

        assertEquals(100.0, pmg.getCpuUtilization(), 5);
        assertEquals(5.75, pmg.getResponseTimeAverage(), 4);
        assertEquals(10.5, pmg.getWaitingTimeAverage(), 4);
        assertEquals(16.5, pmg.getTurnaroundTimeAverage(), 4);
    }
}
