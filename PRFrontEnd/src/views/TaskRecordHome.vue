<template>
  <div class="background">
    <div class="navBar">
      <div>
        <input type="checkbox" v-model="showUnfinishedOnly" @click="filterTask">
        <span>只展示未完成</span>
      </div>
      <input type="checkbox">
      <button @click="reloadTasks">刷新</button>
      <el-button @click="setNewTask">新增任务</el-button>
      <div style="position: relative; display: inline-block">
        <input v-model="projectName" @keydown.enter="filterTask" type="text" list="projectNameDataList" onchange="this.nextElementSibling.style.display=this.value?'block':'none'"/>
        <span style="position: absolute; right: 5px; top: 50%; transform: translateY(-50%);
              cursor: pointer; display: none;"
              onclick="this.previousElementSibling.value=''; this.style.display='none'">×</span>
        <datalist id="projectNameDataList">
          <option v-for="data in projectNameDataList" :key="data" :value="data">{{data}}</option>
        </datalist>
      </div>
      <button @click="filterTask">过滤任务</button>
    </div>
    <div class="taskContainer">
      <div class="task" v-for="task in displayTaskList" :key="task.uuid + '_' + task.sortOrder" @dblclick="openTaskDetail(task)">
        <span class="drag-btn">☰</span>
        <input type="checkbox" v-model="task.finished" @click.stop="changeTaskStatus(task)">
        <div style="flex: 1">
          <span>{{ task.taskName }}</span>
        </div>
        <div style="width: 10%">
          <span>项目名:</span>
          <span>{{ task.projectName }}</span>
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
      <el-button @click="handleTaskSave">保存</el-button>
      <el-form v-model="task" >
        <el-form-item label="任务名称">
          <input v-model="task.taskName"/>
        </el-form-item>
        <el-form-item label="项目名称">
          <input id="projectName" v-model="task.projectName" type="text" list="projectNameDataList"/>
          <datalist id="projectNameDataList">
            <option v-for="data in projectNameDataList" :key="data" :value="data">{{data}}</option>
          </datalist>
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
      <el-button @click="handleRemove">删除</el-button>
    </el-drawer>
  </div>
</template>

<script>
import {getAction, postAction} from '@/network/request';
import md5 from 'blueimp-md5';
import Sortable from 'sortablejs';

export default {
  name: 'TaskRecordHome',
  data(){
    return {
      url: {
        taskList: '/task/getTaskList',
        getTask: '/task/getTask',
        saveTask: '/task/saveTask',
        removeTask: '/task/removeTask',
      },
      displayTaskList: [],
      taskList: [],
      task: {},
      showDrawer: false,
      showUnfinishedOnly: false,
      projectName: '',
      projectNameDataList: [],
      sortable: null,
    };
  },
  watch:{
    showUnfinishedOnly:{
      handler: function(newVal, oldVal) {
        this.filterTask();
      },
    }
  },
  mounted() {
    this.getTaskList();
    this.rowDrag();
  },
  beforeDestroy() {
    if (this.sortable) {
      this.sortable.destroy();
    }
  },
  methods:{
    getTaskList(){
      getAction(this.url.taskList).then(res => {
        // console.log(res);
        this.taskList = res.result;
        // console.log(this.taskList);
        this.projectNameDataList = this.taskList.map(i => {return i.projectName;});
        this.projectNameDataList = Array.from(new Set(this.projectNameDataList));
        this.filterTask();
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
    filterTask() {
      let projectName = this.projectName;
      // console.log(projectName);
      // console.log(this.taskList);
      // console.log(this.showUnfinishedOnly);
      this.displayTaskList = this.taskList.filter(i => {return i.projectName.includes(projectName);});
      if (this.showUnfinishedOnly) {
        this.displayTaskList = this.displayTaskList.filter(i => {return i.finished !== this.showUnfinishedOnly;});
      }
    },
    reloadTasks() {
      this.getTaskList();
    },
    openTaskDetail(row){
      getAction(this.url.getTask, {taskUid: row.uuid}).then(res => {
        console.log(res);
        this.task = res.result;
        this.showDrawer = true;
      });
      console.log(row);
    },
    handleTaskSave(){
      // console.log(this.task);
      postAction(this.url.saveTask, this.task).then(res => {
        console.log(res);
        if (res.success){
          this.showDrawer = false;
          this.getTaskList();
        }
      });
    },
    handleRemove(row){
      postAction(this.url.removeTask, this.task.uuid).then(res => {
        console.log(res);
        if (res.success){
          this.showDrawer = false;
          this.getTaskList();
        }
      });
    },
    changeTaskStatus(task){
      task.finished = !task.finished;
      if (task.finished) {
        task.endTime = new Date().toISOString()
        .replace("T", " ")
        .replace(/\..+/, "")
        .slice(0, 16);
      }
      else {
        task.endTime = '';
      }
      postAction(this.url.saveTask, task).then(res => {
        console.log(res);
        if (res.success){
          // this.showDrawer = false;
          // this.getTaskList();
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
    },
    rowDrag() {
      const taskContainer = document.querySelector('.taskContainer');
      if (taskContainer) {
        this.sortable = Sortable.create(taskContainer, {
          handle: '.drag-btn',
          ghostClass: 'sortable-ghost',
          onEnd: ({ newIndex, oldIndex }) => {
            this.changeSortNumber(newIndex, oldIndex);
          }
        });
      }
    },
    changeSortNumber(newIndex, oldIndex) {
      newIndex = Number(newIndex);
      oldIndex = Number(oldIndex);
      
      // 获取被移动的任务
      const movedTask = this.displayTaskList[oldIndex];
      
      // 找到该任务在taskList中的位置
      const oldTaskListIndex = this.taskList.findIndex(t => t.uuid === movedTask.uuid);
      
      // 找到目标位置的任务在taskList中的位置
      const targetTask = this.displayTaskList[newIndex];
      const newTaskListIndex = this.taskList.findIndex(t => t.uuid === targetTask.uuid);
      
      // 在taskList中移动任务
      const currRow = this.taskList.splice(oldTaskListIndex, 1)[0];
      this.taskList.splice(newTaskListIndex, 0, currRow);
      
      // 更新taskList中受影响任务的sortOrder
      const minIndex = Math.min(oldTaskListIndex, newTaskListIndex);
      const maxIndex = Math.max(oldTaskListIndex, newTaskListIndex);
      
      for (let i = minIndex; i <= maxIndex; i++) {
        this.$set(this.taskList[i], 'sortOrder', i);
      }
      
      // 重新过滤生成displayTaskList
      this.filterTask();
      
      // 保存受影响的任务
      this.saveTaskOrder(minIndex, maxIndex);
      
      // 强制Vue重新渲染
      this.$forceUpdate();
      
      // 重新初始化Sortable以同步DOM状态
      this.$nextTick(() => {
        if (this.sortable) {
          this.sortable.destroy();
        }
        this.rowDrag();
      });
    },
    saveTaskOrder(minIndex, maxIndex) {
      // 保存受影响范围内的所有任务
      for (let i = minIndex; i <= maxIndex; i++) {
        const task = this.taskList[i];
        postAction(this.url.saveTask, task).then(res => {
          // console.log('保存排序成功:', res);
        }).catch(err => {
          console.error('保存排序失败:', err);
        });
      }
    }
  }
};
</script>

<style scoped>
.background {
  background: linear-gradient(60deg, rgb(82, 67, 170), rgb(237, 80, 180));
  //background: rgb(128, 128, 128);
  width: 100%;
  height: 100%;
}
.task {
  display: flex;
  background: azure;
  border-radius: 15px;
  padding: 10px;
  margin: 10px 20px;
  align-items: center;
}
.drag-btn {
  cursor: move;
  margin-right: 10px;
  font-size: 20px;
  color: #666;
  user-select: none;
}
.taskContainer .task.sortable-ghost {
  opacity: 0.4;
  background-color: #dfecfb;
}
.navBar {
  height: 10%;
}
.taskContainer {
  width: 100%;
  height: 90%;
  overflow-y: auto;
}
</style>