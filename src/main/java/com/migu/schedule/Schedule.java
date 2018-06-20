package com.migu.schedule;

import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.TaskInfo;
import com.migu.schedule.info.TaskInfoSon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
*类名和方法不能修改
 */
public class Schedule
{
    // 服务器节点列表
    private Map<Integer, List<TaskInfoSon>> nodeList;
    
    // 任务列表
    private Map<Integer, TaskInfoSon> taskList;
    
    public int init()
    {
        // 初始化服务器节点列表
        nodeList = new HashMap<Integer, List<TaskInfoSon>>();
        // 初始化任务列表
        taskList = new HashMap<Integer, TaskInfoSon>();
        return ReturnCodeKeys.E001;
    }
    
    public int registerNode(int nodeId)
    {
        // 如果服务节点编号小于等于0, 返回E004:服务节点编号非法
        if (nodeId <= 0)
        {
            return ReturnCodeKeys.E004;
        }
        // 如果服务节点编号已注册, 返回E005:服务节点已注册
        else if (nodeList.containsKey(nodeId))
        {
            return ReturnCodeKeys.E005;
        }
        else
        {
            // 注册成功，返回E003:服务节点注册成功
            List<TaskInfoSon> taskInfoList = new ArrayList<TaskInfoSon>();
            nodeList.put(nodeId, taskInfoList);
            return ReturnCodeKeys.E003;
        }
    }
    
    public int unregisterNode(int nodeId)
    {
        // 如果服务节点编号小于等于0, 返回E004:服务节点编号非法。
        if (nodeId <= 0)
        {
            return ReturnCodeKeys.E004;
        }
        // 如果服务节点编号未被注册, 返回E007:服务节点不存在。
        else if (!nodeList.containsKey(nodeId))
        {
            return ReturnCodeKeys.E007;
        }
        else
        {
            // 查询当前节点上正在运行的任务
            List<TaskInfoSon> taskInfoList = nodeList.get(nodeId);
            if (taskInfoList.size() > 0)
            {
                // 将正在运行的任务转成挂起状态
                for (int i = 0; i < taskInfoList.size(); i++)
                {
                    taskInfoList.get(i).setStatus(2);
                }
            }
            nodeList.remove(nodeId);
            // 注销成功，返回E006:服务节点注销成功
            return ReturnCodeKeys.E006;
        }
        
    }
    
    public int addTask(int taskId, int consumption)
    {
        // 如果任务编号小于等于0, 返回E009:任务编号非法。
        if (taskId <= 0)
        {
            return ReturnCodeKeys.E009;
        }
        // 如果相同任务编号任务已经被添加, 返回E010:任务已添加。
        else if (taskList.containsKey(taskId))
        {
            return ReturnCodeKeys.E010;
        }
        else
        {
            TaskInfoSon taskInfo = new TaskInfoSon();
            taskInfo.setTaskId(taskId);
            taskInfo.setConsumption(consumption);
            taskInfo.setStatus(2);
            taskInfo.setNodeId(-1);
            taskList.put(taskId, taskInfo);
            // 添加成功，返回E008任务添加成功。
            return ReturnCodeKeys.E008;
        }
        
    }
    
    public int deleteTask(int taskId)
    {
        // 如果任务编号小于等于0, 返回E009:任务编号非法。
        if (taskId <= 0)
        {
            return ReturnCodeKeys.E009;
        }
        // 如果指定编号的任务未被添加, 返回E012:任务不存在。
        else if (!taskList.containsKey(taskId))
        {
            return ReturnCodeKeys.E012;
        }
        else
        {
            TaskInfoSon taskInfo = taskList.get(taskId);
            // 获取任务所在的服务器节点id
            int nodeId = taskInfo.getNodeId();
            if (nodeId != -1)
            {
                List<TaskInfoSon> taskList = nodeList.get(nodeId);
                // 删除服务器中正在运行的任务
                taskList.remove(taskInfo);
            }
            // 删除任务列表中的任务
            taskList.remove(taskId);
            // 删除成功，返回E011:任务删除成功。
            return ReturnCodeKeys.E011;
        }
        
    }
    
    public int scheduleTask(int threshold)
    {
        // 如果调度阈值取值错误，返回E002调度阈值非法。
        if (threshold <= 0)
        {
            return ReturnCodeKeys.E002;
        }
        if (nodeList.size() > taskList.size())
        {
            TaskInfoSon[] array = new TaskInfoSon[taskList.size()];
            
            Iterator<Entry<Integer, TaskInfoSon>> it =
                taskList.entrySet().iterator();
            while (it.hasNext())
            {
                int z = 0;
                Entry<Integer, TaskInfoSon> entry =
                    (Entry<Integer, TaskInfoSon>)it.next();
                int taskID = entry.getKey();
                TaskInfoSon task = entry.getValue();
                array[z] = task;
                z++;
            }
            
            TaskInfoSon temp;
            for (int i = 0; i < array.length; i++)
            {
                for (int j = 1; j < array.length; j++)
                {
                    if (array[j - 1].getConsumption() > array[j]
                        .getConsumption())
                    {
                        temp = array[j - 1];
                        array[j - 1] = array[j];
                        array[j] = temp;
                    }
                }
            }
            
            
            
            
        }
        
        // 如果获得最佳迁移方案, 进行了任务的迁移,返回E013: 任务调度成功;
        // 如果所有迁移方案中，总会有任意两台服务器的总消耗率差值大于阈值。则认为没有合适的迁移方案,返回E014:无合适迁移方案;
        
        return ReturnCodeKeys.E000;
    }
    
    public int queryTaskStatus(List<TaskInfo> tasks)
    {
        // 如果查询结果参数tasks为null，返回E016:参数列表非法
        if (tasks == null)
        {
            return ReturnCodeKeys.E016;
        }
        else
        {
            for (int i = 0; i < taskList.size(); i++)
            {
                tasks.add(taskList.get(i));
            }
            // 查询成功, 返回E015: 查询任务状态成功;查询结果从参数Tasks返回。
            return ReturnCodeKeys.E015;
        }
    }
    
    private void scheduleTaskMana()
    {
        // 获取已注册的服务器节点数
        int nodeSize = nodeList.size();
        
        List list = new ArrayList();
        
        for (int i = 0; i < nodeSize; i++)
        {
            
        }
    }
    
    /**
     * 计算服务器消耗率的两两差值的和 <功能详细描述>
     * 
     * @param nodeList 服务器列表
     * @param map
     * @return
     * @see [类、类#方法、类#成员]
     */
    private int calculate(Map<Integer, List<TaskInfoSon>> nodeList,
        Map<Integer, Integer> map)
    {
        Iterator<Entry<Integer, List<TaskInfoSon>>> it =
            nodeList.entrySet().iterator();
        List<Integer> list = new ArrayList<Integer>();
        while (it.hasNext())
        {
            Entry<Integer, List<TaskInfoSon>> entry =
                (Entry<Integer, List<TaskInfoSon>>)it.next();
            int nodeId = entry.getKey();
            List<TaskInfoSon> tasks = entry.getValue();
            int temconsumption = 0;
            for (TaskInfoSon taskInfoSon : tasks)
            {
                temconsumption = temconsumption + taskInfoSon.getConsumption();
            }
            map.put(nodeId, temconsumption);
            list.add(temconsumption);
        }
        
        int num = 0;
        int total = 0;
        for (int i = 0; i < list.size(); i++)
        {
            num = list.get(i);
            for (int j = 0; j < list.size(); j++)
            {
                total = total + Math.abs(num - list.get(j));
            }
        }
        
        return total;
        
    }
    
    private void getAllChanges()
    {
        
        Map<Integer, List<TaskInfoSon>> map = null;
        for (int i = 0; i < nodeList.size(); i++)
        {
            map = new HashMap<Integer, List<TaskInfoSon>>();
            Iterator<Entry<Integer, TaskInfoSon>> it =
                taskList.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<Integer, TaskInfoSon> entry =
                    (Entry<Integer, TaskInfoSon>)it.next();
                int taskID = entry.getKey();
                TaskInfoSon task = entry.getValue();
                
            }
        }
        
        List<Map<Integer, List<TaskInfoSon>>> list =
            new ArrayList<Map<Integer, List<TaskInfoSon>>>();
        
    }
}
