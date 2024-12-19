<template>
  <div class="background">
    <div class="navBar">
<!--      <button @click="reloadTasks">刷新</button>-->
      <el-button @click="setNewTask">新增任务</el-button>
    </div>
    <div class="taskContainer">
      <div class="task" v-for="task in taskList" :key="task.uid">
        <input type="checkbox">
        <div style="flex: 1">
          <span>{{ task.taskName }}</span>
        </div>
        <div style="width: 10%">
          <span>指派给：</span>
          <span>{{ task.assignedTo }}</span>
        </div>
<!--        <div>-->
<!--          <select v-model="task.assignedTo">-->
<!--            <option value="朱恩维">朱恩维</option>-->
<!--          </select>-->
<!--        </div>-->
        <div style="width: 15%">
          <span>创建时间：</span>
          <span>{{ task.createTime }}</span>
        </div>
        <div style="width: 15%">
          <span>完成时间：</span>
          <span>{{ task.endTime }}</span>
        </div>
      </div>
    </div>
<!--    <div>-->
<!--      <el-table :data="taskList" @row-dblclick="openTaskDetail">-->
<!--        <el-table-column prop="taskName" label="任务名称"></el-table-column>-->
<!--        <el-table-column prop="projectName" label="项目名称"></el-table-column>-->
<!--        <el-table-column prop="assignedTo" label="指定给"></el-table-column>-->
<!--        <el-table-column prop="startTime" label="开始时间"></el-table-column>-->
<!--        <el-table-column prop="estimateEndTime" label="预计完成时间"></el-table-column>-->
<!--        <el-table-column prop="endTime" label="实际完成时间"></el-table-column>-->
<!--&lt;!&ndash;        <el-table-column label="计时器">&ndash;&gt;-->
<!--&lt;!&ndash;          <template slot-scope="{}">&ndash;&gt;-->

<!--&lt;!&ndash;          </template>&ndash;&gt;-->
<!--&lt;!&ndash;        </el-table-column>&ndash;&gt;-->
<!--        <el-table-column label="操作">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button @click="handleRemove(scope.row)">删除</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--      </el-table>-->
<!--    </div>-->
    <el-drawer :visible.sync="showDrawer" direction="rtl" size="70%">
      <el-form v-model="task" >
        <el-form-item label="任务名称">
          <input v-model="task.taskName"/>
        </el-form-item>
        <el-form-item label="项目名称">
          <input v-model="task.projectName"/>
        </el-form-item>
        <el-form-item label="指定给">
          <input v-model="task.assignedTo"/>
        </el-form-item>
        <el-form-item label="开始时间">
          <input type="datetime-local" v-model="task.startTime"/>
        </el-form-item>
        <el-form-item label="预计完成时间">
          <input type="datetime-local" v-model="task.estimateEndTime"/>
        </el-form-item>
        <el-form-item label="实际完成时间">
          <input type="datetime-local" v-model="task.endTime"/>
        </el-form-item>
        <el-form-item label="任务细节">
          <textarea v-model="task.taskDetail"/>
        </el-form-item>
        <el-form-item label="备注">
          <textarea v-model="task.memo"/>
        </el-form-item>
      </el-form>
      <el-button @click="handleTaskSave">保存</el-button>
    </el-drawer>
  </div>
</template>

<script>
import {getAction, postAction} from '@/network/request';
import md5 from 'blueimp-md5';

export default {
  name: 'TaskRecordHome',
  data(){
    return {
      url: {
        taskList: '/task/getTaskList',
        getTask: '/task/getTask',
        saveTask: '/task/saveTask',
        removeTask: '/task/removeTask'
      },
      taskList:[],
      task:{},
      showDrawer:false
    };
  },
  mounted() {
    this.getTaskList();
  },
  methods:{
    getTaskList(){
      getAction(this.url.taskList).then(res => {
        // console.log(res);
        this.taskList = res.result;
        console.log(this.taskList);
      });
      this.task = {
        taskName: '',
        projectName:'',
        assignedTo:'',
        startTime:'',
        estimateEndTime:'',
        endTime:'',
        taskDetail:'',
        memo:''
      };
    },
    reloadTasks() {
      this.getTaskList();
    },
    openTaskDetail(row){
      this.showDrawer = true;
      getAction(this.url.getTask, {taskUid: row.uuid}).then(res => {
        console.log(res);
        this.task = res.result;
      });
      console.log(row);
    },
    handleTaskSave(){
      console.log(this.task);
      // postAction(this.url.saveTask, this.task).then(res => {
      //   console.log(res);
      //   if (res.success){
      //     this.showDrawer = false;
      //     this.getTaskList();
      //   }
      // });
    },
    handleRemove(row){
      postAction(this.url.removeTask, row.uuid).then(res => {
        console.log(res);
        if (res.success){
          this.showDrawer = false;
          this.getTaskList();
        }
      });
    },
    setNewTask(){
      this.task = {
        taskName: '',
        projectName:'',
        assignedTo:'',
        createTime:'',
        startTime:'',
        estimateEndTime:'',
        endTime:'',
        taskDetail:'',
        memo:''
      };
      this.showDrawer = true;
    }
  }
};
</script>

<style scoped>
.background {
  background: linear-gradient(60deg, rgb(82, 67, 170), rgb(237, 80, 180));
  width: 100%;
  height: 100%;
}
.task {
  display: flex;
  background: azure;
  border-radius: 15px;
  padding: 10px;
  margin: 10px 20px;
}

</style>