package schedulers;

import java.util.*;

import utilities.Cpu;
import utilities.PerformanceMetricGenerator;
import utilities.Process;

/**
 * Shortest Job First CPU Scheduling Algorithm
 * @author Nick Trimmer
 */
public class SJF implements SchedulerInterface {

    /**
     * Processes for scheduler simulation
     */
    private List<Process> processes;
    /**
     * True if processes have been executed, false if otherwise
     */
    private Boolean processesExecuted;
    /**
     * Current CPU object
     */
    private Cpu cpu;

    /**
     * @inheritDoc
     */
    @Override
    public void loadProcesses(List<Process> processes) {
        this.processes = processes;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void executeProcesses(Boolean contextStream) throws Exception {
        this.cpu = new Cpu(contextStream);

        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getActiveProcessTimeRemaining));
        readyQueue.addAll(processes);
        cpu.sendToCpuIfEmpty(readyQueue.poll());
        while (!cpu.checkCompletion()) {
            cpu.cpuTick();
            if (cpu.idle() && !readyQueue.isEmpty()) {
                cpu.sendToCpuIfEmpty(readyQueue.poll());
            }

            for(Process p : cpu.getReadyProcesses()){
                if(!readyQueue.contains(p)){
                    readyQueue.add(p);
                }
            }
        }
        this.processesExecuted = true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public PerformanceMetricGenerator generatePerformanceMetrics() throws Exception {
        if (!processesExecuted) {
            throw new Exception("Must complete processes before generating metrics");
        }
        return new PerformanceMetricGenerator("Shortest Job First", processes, cpu);
    }
}