package schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utilities.Cpu;
import utilities.CpuInterface;
import utilities.PerformanceMetricGenerator;
import utilities.Process;

/**
 * Multilevel Queue CPU Scheduling Algorithm
 * @author Aodhan Bower
 */
public class MLQ implements SchedulerInterface {

    /**
     * Processes for scheduler simulation
     */
    private List<Process> processes;

    /**
     * True if processes have been executed, false if otherwise
     */
    private Boolean processesExecuted;

    /**
     * @inheritDoc
     */

    private List<Process> foreground;
    private List<Process> background;
    private final int quantum;

    private CpuInterface cpu;
    private int cycleTime;

    public MLQ(int quantum, int cycleTime){
        this.quantum = quantum;
        this.cycleTime = cycleTime;

    }
    @Override
    public void loadProcesses(List<Process> processes) {
        this.processes = new ArrayList<>();
        this.foreground = new ArrayList<>();
        this.background = new ArrayList<>();
        for(Process p : processes){
            this.processes.add(p);
            if(processes.indexOf(p) < 4){
                foreground.add(p);
            }
            else{
                background.add(p);
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void executeProcesses(Boolean contextStream) throws Exception {

        //if contextStream we will print snapshots every time onCpu changes
        if(contextStream){
            this.cpu = new Cpu(true,"mlqContextSwitches");
        }
        else{
            this.cpu = new Cpu();
        }

        //initialize a map that will store which queue each process belongs in.
        HashMap<Process, List<Process>> processListMap = new HashMap<>();
        for(Process p : foreground){
            cpu.addProcess(p);
            processListMap.put(p,foreground);
        }
        for(Process p : background){
            cpu.addProcess(p);
            processListMap.put(p,background);
        }

        //put the first item on the cpu and initialize quantum counter and time counter to 0
        cpu.sendToCpuIfEmpty(foreground.remove(0));
        int quantumCount = 0;
        int timeCounter = 0;

        //while all processes are not complete
        while(!cpu.checkCompletion()){
            //tick the cpu and increase the quantum
            cpu.cpuTick();
            quantumCount++;

            /* if the timeCounter is below 56 we want to do a foreground process
            in line with our 80/20 split, which for our numbers is actually about 76.7% foreground. but if foreground queue is empty we will
            skip over it for efficiency's sake */
            if(timeCounter < (cycleTime/5)*4 && foreground.size() > 0){
                if(cpu.idle()){
                    cpu.sendToCpuIfEmpty(foreground.remove(0));
                }
                else if(quantumCount >= quantum){
                    Process temp = cpu.getOnCpu();
                    cpu.preemptOnCpu(foreground.remove(0));
                    quantumCount = 0;
                    processListMap.get(temp).add(temp);
                }
            }
            /*
            we are here if either it is the 10 ticks allotted to background
            OR foreground queue is empty. if there are items in the background queue
            we will use fcfs process on them
             */
            else if(background.size() > 0){
                if(cpu.idle()){
                    cpu.sendToCpuIfEmpty(background.remove(0));
                }
            }
            /* this only activates if it's the background's turn but background queue
            is empty. in that case so long as foreground queue isn't empty we will go back to drawing from it
             */
            else if(foreground.size() > 0){
                if(cpu.idle()){
                    cpu.sendToCpuIfEmpty(foreground.remove(0));
                }
            }

            //increment time and reset it back to 0 if it is at our cycleTime, which is 73.
            timeCounter++;
            if(timeCounter == cycleTime){
                timeCounter = 0;
            }
            //check the cpu ready processes and make sure that anything that returned from io is also
            //returned to our external queues.
            for(Process p : cpu.getReadyProcesses()){
                if(!processListMap.get(p).contains(p)){
                    processListMap.get(p).add(p);
                }
            }
        }
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
        return new PerformanceMetricGenerator("Multilevel Queue", processes,cpu);
    }

}