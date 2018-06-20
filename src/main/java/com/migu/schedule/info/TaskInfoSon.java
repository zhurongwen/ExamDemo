package com.migu.schedule.info;

public class TaskInfoSon extends TaskInfo
{
    //资源消耗率
    private int consumption;
    //任务状态，1是运行，2是挂起
    private int status;
    public int getStatus()
    {
        return status;
    }
    public void setStatus(int status)
    {
        this.status = status;
    }
    public int getConsumption()
    {
        return consumption;
    }
    public void setConsumption(int consumption)
    {
        this.consumption = consumption;
    }
}
