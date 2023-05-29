package schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utilities.Cpu;
import utilities.CpuInterface;
import utilities.PerformanceMetricGenerator;
import utilities.Process;

/**
 * Multilevel Feedback Queue CPU Scheduling Algorithm
 * @author Aodhan Bower
 */
public class MLFQ implements SchedulerInterface {

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

    private ArrayList<Process> queueOne;
    private ArrayList<Process> queueTwo;
    private ArrayList<Process> queueThree;
    private ArrayList<ArrayList<Process>> queueList;
    private final int TIME_QUANTUM = 5;
    private boolean contextStream;
    private Map<Process, Integer> queueMap;

    public MLFQ(){

    }


    /**
     * @inheritDoc
     */
    @Override
    public void loadProcesses(List<Process> processes) {
        this.queueOne = new ArrayList<>();
        this.queueTwo = new ArrayList<>();
        this.queueThree = new ArrayList<>();
        this.processes = new ArrayList<>();
        this.queueMap = new HashMap<>();
        queueList = new ArrayList<>();
        queueList.add(queueOne);
        queueList.add(queueTwo);
        queueList.add(queueThree);
        for(Process p : processes){
            this.processes.add(p);
            queueOne.add(p);
            queueMap.put(p,0);
        }

    }

    /**
     * @inheritDoc
     */
    @Override
    public void executeProcesses(Boolean contextStream) throws Exception {

        // YOUR ALGORITHM HERE :)
        this.cpu = new Cpu(contextStream,"mlfqContextStream");
        cpu.setProcessList(this.queueOne);
        cpu.sendToCpuIfEmpty(queueOne.remove(0));
        List<Process> activeQueue = queueOne;
        int quantum = 0;
        int quantumGoal = TIME_QUANTUM;
        while(!cpu.checkCompletion()){
            cpu.cpuTick();
            quantum++;
            activeQueue = setActiveQueue();
            if(cpu.idle() && activeQueue.size() > 0){
                cpu.sendToCpuIfEmpty(activeQueue.remove(0));
                quantumGoal = setQuantumGoal(activeQueue);
                quantum = 0;
            }
            else if(quantum == quantumGoal  && !cpu.idle()){
                Process temp = cpu.getOnCpu();
                int tmp = 0;
                if(queueMap.get(temp) + 1 < 3){
                  tmp = 1;
                }
                queueList.get(queueMap.get(temp) + tmp).add(temp);
                queueMap.put(temp,queueMap.get(temp) + tmp);
                cpu.preemptOnCpu(activeQueue.remove(0));
                quantumGoal = setQuantumGoal(activeQueue);
                quantum = 0;
            }
            for(Process p : cpu.getReadyProcesses()){
                if(!inQueue(p)){
                    queueMap.put(p, queueMap.get(p));
                    queueList.get(queueMap.get(p)).add(p);
                }
            }
        }

        this.processesExecuted = true;
    }

    private boolean inQueue(Process p){
        return queueOne.contains(p) || queueTwo.contains(p) || queueThree.contains(p);
    }

    private List<Process> setActiveQueue(){
        if(queueOne.size() > 0){
            return queueOne;
        }
        else if(queueTwo.size() > 0){
            return queueTwo;
        }
        else {
            return queueThree;
        }
    }

    private int setQuantumGoal(List<Process> activeQueue){
        if(activeQueue.equals(queueOne)){
            return TIME_QUANTUM;
        }
        else if(activeQueue.equals(queueTwo)){
            return TIME_QUANTUM * 2;
        }
        else{
            return -1;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public PerformanceMetricGenerator generatePerformanceMetrics() throws Exception {
        if (!processesExecuted) {
            throw new Exception("Must complete processes before generating metrics");
        }
        return new PerformanceMetricGenerator("Multilevel Feedback Queue", processes,cpu);
    }
}