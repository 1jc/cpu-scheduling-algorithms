package schedulers;

import java.util.*;
import utilities.Cpu;
import utilities.CpuInterface;
import utilities.PerformanceMetricGenerator;
import utilities.Process;

/**
 * Round Robin CPU Scheduling Algorithm
 * author Bella Mester
 */
public class RR implements SchedulerInterface {

    /**
     * Processes for scheduler simulation
     */
    private List<Process> processes;
    /**
     * Total time scheduler is active for all processes
     */
    private float totalExecutionTime;
    /**
     * True if processes have been executed, false if otherwise
     */
    private Boolean processesExecuted;

    private CpuInterface cpu;

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
    public void executeProcesses(Boolean contextSwitch) throws Exception {
        cpu = new Cpu(true,"roundRobinContextSwitching");

        // Load the processes into the CPU
        cpu.setProcessList(processes);

        // Execute processes using Round Robin algorithm
        while (!cpu.checkCompletion()) {
            cpu.cpuTick();

            if (contextSwitch) {
                if (cpu.idle()) {
                    cpu.nextProcessToCpuPreempt();
                } else if (cpu.getOnCpu().getActiveProcessTimeRemaining() <= 0) {
                    cpu.nextProcessToCpuPreempt();
                } else {
                    cpu.preemptOnCpu(cpu.getOnCpu());
                    cpu.nextProcessToCpuPreempt();
                }
            } else {
                if (cpu.idle()) {
                    cpu.nextProcessToCpuIfIdle();
                }
            }
        }

        totalExecutionTime = cpu.getTime();
        processesExecuted = true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public PerformanceMetricGenerator generatePerformanceMetrics() throws Exception {
        if (!processesExecuted) {
            throw new Exception("Must complete processes before generating metrics");
        }
        return new PerformanceMetricGenerator("Round Robin", processes, cpu);
    }
}
