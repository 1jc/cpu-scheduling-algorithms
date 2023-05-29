import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import schedulers.*;
import utilities.PerformanceMetricGenerator;
import utilities.Process;

public class Main {

    public static void main(String[] args) throws Exception {
        List<Process> processes = new ArrayList<>();
        loadProcesses(processes);
        float[][] dataForCSV = {new float[7], new float[7], new float[7], new float[7]};

        // SHORTEST JOB FIRST (NON PREEMPTIVE)
        SJF sjf = new SJF();
        sjf.loadProcesses(processes);
        sjf.executeProcesses(false);
        PerformanceMetricGenerator sjfPMG = sjf.generatePerformanceMetrics();
        populateData(dataForCSV, 0, sjfPMG);
        System.out.println("SHORTEST JOB FIRST:");
        System.out.println(sjfPMG);
        System.out.println();
        resetProcess(processes);

        // SHORTEST REMAINING TIME FIRST (PREEMPTIVE)
        SJF srtf = new SJF();
        srtf.loadProcesses(processes);
        srtf.executeProcessesPreemptive(false);
        PerformanceMetricGenerator srtfPMG = srtf.generatePerformanceMetrics();
        populateData(dataForCSV, 1, srtfPMG);
        System.out.println("SHORTEST REMAINING TIME FIRST:");
        System.out.println(srtfPMG);
        System.out.println();
        resetProcess(processes);

        // ROUND ROBIN
        RR rr = new RR();
        rr.loadProcesses(processes);
        rr.executeProcesses(false);
        PerformanceMetricGenerator rrPMG = rr.generatePerformanceMetrics();
        populateData(dataForCSV, 2, rrPMG);
        System.out.println("ROUND ROBIN:");
        System.out.println(rrPMG);
        System.out.println();
        resetProcess(processes);

        // MULTILEVEL QUEUE
        MLQ mlq = new MLQ(5,73);
        mlq.loadProcesses(processes);
        mlq.executeProcesses(false);
        PerformanceMetricGenerator mlqPMG = mlq.generatePerformanceMetrics();
        populateData(dataForCSV, 3, mlqPMG);
        System.out.println("MULTILEVEL QUEUE:");
        System.out.println(mlqPMG);
        System.out.println();
        resetProcess(processes);

        // MULTILEVEL FEEDBACK QUEUE
        MLFQ mlfq = new MLFQ();
        mlfq.loadProcesses(processes);
        mlfq.executeProcesses(false);
        PerformanceMetricGenerator mlfqPMG = mlfq.generatePerformanceMetrics();
        populateData(dataForCSV, 4, mlfqPMG);
        System.out.println("MULTILEVEL FEEDBACK QUEUE:");
        System.out.println(mlfqPMG);
        System.out.println();
        resetProcess(processes);


        // PRIORITY
        Priority pri = new Priority();
        pri.loadProcesses(processes);
        pri.executeProcesses(false);
        PerformanceMetricGenerator priPMG = pri.generatePerformanceMetrics();
        populateData(dataForCSV, 5, priPMG);
        System.out.println("PRIORITY:");
        System.out.println(priPMG);
        System.out.println();
        resetProcess(processes);


        //FIRST-COME FIRST SERVED
        FCFS fcfs = new FCFS();
        fcfs.loadProcesses(processes);
        fcfs.executeProcesses(false);
        PerformanceMetricGenerator fcfsPMG = fcfs.generatePerformanceMetrics();
        populateData(dataForCSV, 6, fcfsPMG);
        System.out.println("FIRST-COME FIRST SERVED:");
        System.out.println(fcfsPMG);
        System.out.println();
        resetProcess(processes);



        writeToCSV(dataForCSV);
    }

    /**
     * Loads process data specific to our simulation into an
     * ArrayList of Processes
     * @param processes the collection of processes to be loaded
     *                  with the data
     */
    private static void loadProcesses(List<Process> processes) {
        Process p1 = new Process("P1", Arrays.asList(5, 27, 3, 31, 5, 43, 4, 18, 6, 22, 4, 26, 3, 24, 4));
        Process p2 = new Process("P2", Arrays.asList(4, 48, 5, 44, 7, 42, 12, 37, 9, 76, 4, 41, 9, 31, 7, 43, 8));
        Process p3 = new Process("P3", Arrays.asList(8, 33, 12, 41, 18, 65, 14, 21, 4, 61, 15, 18, 14, 26, 5, 31, 6));
        Process p4 = new Process("P4", Arrays.asList(3, 35, 4, 41, 5, 45, 3, 51, 4, 61, 5, 54, 6, 82, 5, 77, 3));
        Process p5 = new Process("P5", Arrays.asList(16, 24, 17, 21, 5, 36, 16, 26, 7, 31, 13, 28, 11, 21, 6, 13, 3, 11, 4));
        Process p6 = new Process("P6", Arrays.asList(11, 22, 4, 8, 5, 10, 6, 12, 7, 14, 9, 18, 12, 24, 15, 30, 8));
        Process p7 = new Process("P7", Arrays.asList(14, 46, 17, 41, 11, 42, 15, 21, 4, 32, 7, 19, 16, 33, 10));
        Process p8 = new Process("P8", Arrays.asList(4, 14, 5, 33, 6, 51, 14, 73, 16, 87, 6));

        p1.setPriority(3);
        p2.setPriority(6);
        p3.setPriority(5);
        p4.setPriority(4);
        p5.setPriority(1);
        p6.setPriority(2);
        p7.setPriority(8);
        p8.setPriority(7);

        p1.setArrivalTime(0);
        p2.setArrivalTime(0);
        p3.setArrivalTime(0);
        p4.setArrivalTime(0);
        p5.setArrivalTime(0);
        p6.setArrivalTime(0);
        p7.setArrivalTime(0);
        p8.setArrivalTime(0);

        processes.add(p1);
        processes.add(p2);
        processes.add(p3);
        processes.add(p4);
        processes.add(p5);
        processes.add(p6);
        processes.add(p7);
        processes.add(p8);
    }

    private static void resetProcess(List<Process> processes) {
        for (Process process : processes) {
            process.reset();
        }
    }

    private static void populateData(float[][] matrix, int index, PerformanceMetricGenerator pmg) {
        matrix[0][index] = pmg.getCpuUtilization();
        matrix[1][index] = pmg.getWaitingTimeAverage();
        matrix[2][index] = pmg.getTurnaroundTimeAverage();
        matrix[3][index] = pmg.getResponseTimeAverage();
    }

    private static void writeToCSV(float[][] matrix) {
        String[] algorithms = {"Shortest Job First", "Shortest Remaining Time First", "Round Robin",
        "Multilevel Queue", "Multilevel Feedback Queue", "Priority", "First Come First Served"};
        try {
            FileWriter[] fileWriters = {new FileWriter("report/output/cpuUtil.csv"),
                                        new FileWriter("report/output/avgWait.csv"),
                                        new FileWriter("report/output/avgTurn.csv"),
                                        new FileWriter("report/output/avgResp.csv")};

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    fileWriters[i].write(algorithms[j]);
                    fileWriters[i].write(",");
                }
                fileWriters[i].write("\n");
                for (int j = 0; j < matrix[i].length; j++) {
                    fileWriters[i].write(String.valueOf(matrix[i][j]));
                    fileWriters[i].write(",");
                }
                fileWriters[i].flush();
                fileWriters[i].close();
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }
}
