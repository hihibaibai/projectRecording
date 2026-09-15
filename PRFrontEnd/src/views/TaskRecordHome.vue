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
          <option v-for="data in projectNameDataList" :key="data" :value="data">{{data + '\u3000'}}</option>
        </datalist>
      </div>
      <button @click="filterTask">过滤任务</button>
      <button @click="goToCountdown">倒计时看板</button>
    </div>
    <div class="taskContainer">
      <div class="task" v-for="task in displayTaskList" :key="task.uuid + '_' + task.sortOrder" @dblclick="openTaskDetail(task)" @contextmenu.prevent="showContextMenu($event, task)">
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
    <task-detail-drawer :visible.sync="showDrawer" :task-data="task" :project-name-data-list="projectNameDataList" @saved="onTaskSaved" @removed="onTaskSaved" @completed="onTaskSaved"></task-detail-drawer>
    <div v-if="contextMenuVisible" class="context-menu" :style="{left: contextMenuX + 'px', top: contextMenuY + 'px'}">
      <div class="context-menu-item" @click="openTimerWindow">开始任务</div>
    </div>
    <div v-if="timerWindowVisible" class="timer-window">
      <div class="timer-header">
        <span>{{ currentTask.taskName }}</span>
        <span class="close-btn" @click="closeTimerWindow">×</span>
      </div>
      <div class="timer-display">{{ formatTime(elapsedTime) }}</div>
      <div class="timer-controls">
        <button @click="startTimer" :disabled="isTimerRunning">开始计时</button>
        <button @click="stopTimer" :disabled="!isTimerRunning">结束计时</button>
      </div>
      <div class="timer-records">
        <div class="record-title">计时记录</div>
        <div class="record-list">
          <div v-for="(record, index) in timerRecords" :key="index" class="record-item">
            <span class="record-start">{{ record.startTime }}</span>
            <span class="record-duration">{{ formatTime(record.duration) }}</span>
            <span class="record-end">{{ record.endTime }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {getAction, postAction} from '@/network/request';
import md5 from 'blueimp-md5';
import Sortable from 'sortablejs';
import TaskDetailDrawer from '@/components/TaskDetailDrawer.vue';

export default {
  name: 'TaskRecordHome',
  components: {
    TaskDetailDrawer
  },
  data(){
    return {
      url: {
        taskList: '/task/getTaskList',
        getTask: '/task/getTask',
        saveTask: '/task/saveTask',
        removeTask: '/task/removeTask',
        getTimerRecords: '/taskTimerRecord/getTimerRecords',
        startTimer: '/taskTimerRecord/startTimer',
        endTimer: '/taskTimerRecord/endTimer',
      },
      displayTaskList: [],
      taskList: [],
      task: {},
      showDrawer: false,
      showUnfinishedOnly: false,
      projectName: '',
      projectNameDataList: [],
      sortable: null,
      contextMenuVisible: false,
      contextMenuX: 0,
      contextMenuY: 0,
      currentTask: {},
      timerWindowVisible: false,
      elapsedTime: 0,
      isTimerRunning: false,
      timerInterval: null,
      timerRecords: [],
      currentTimerRecord: {},
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
      if (!this.task.estimateEndTime) {
        this.$message.warning('请填写预计完成时间');
        return;
      }
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
        task.endTime = new Date(Date.now() + 8 * 3600 * 1000).toISOString()
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
    },
    showContextMenu(event, task) {
      this.contextMenuVisible = true;
      this.contextMenuX = event.clientX;
      this.contextMenuY = event.clientY;
      this.currentTask = task;
      document.addEventListener('click', this.hideContextMenu);
    },
    hideContextMenu() {
      this.contextMenuVisible = false;
      document.removeEventListener('click', this.hideContextMenu);
    },
    openTimerWindow() {
      this.timerWindowVisible = true;
      this.contextMenuVisible = false;
      this.loadTimerRecords();
    },
    loadTimerRecords() {
      getAction(this.url.getTimerRecords, {taskUuid: this.currentTask.uuid}).then(res => {
        if (res.result && Array.isArray(res.result)) {
          this.timerRecords = res.result.map(item => {
            const startTime = new Date(item.startTime);
            const endTime = item.endTime ? new Date(item.endTime) : new Date();
            const duration = Math.floor((endTime.getTime() - startTime.getTime()) / 1000);
            return {
              uuid: item.uuid,
              startTime: this.formatTimeHHmm(startTime),
              endTime: this.formatTimeHHmm(endTime),
              duration: duration
            };
          });
        }
        else {
          this.timerRecords = [];
        }
      }).catch(err => {
        console.error('加载计时记录失败:', err);
        this.timerRecords = [];
      });
    },
    formatTimeHHmm(date) {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return hours + ':' + minutes;
    },
    closeTimerWindow() {
      this.timerWindowVisible = false;
      if (this.isTimerRunning) {
        this.stopTimer();
      }
    },
    startTimer() {
      this.isTimerRunning = true;
      this.elapsedTime = 0;
      const record = {
        taskUuid: this.currentTask.uuid
      };
      postAction(this.url.startTimer, record).then(res => {
        this.currentTimerRecord = res.result;
      });
      this.timerInterval = setInterval(() => {
        this.elapsedTime++;
      }, 1000);
    },
    stopTimer() {
      this.isTimerRunning = false;
      clearInterval(this.timerInterval);
      postAction(this.url.endTimer, this.currentTimerRecord).then(res => {
        this.loadTimerRecords();
      });
    },
    formatTime(seconds) {
      const minutes = Math.floor(seconds / 60);
      const secs = seconds % 60;
      return minutes.toString().padStart(2, '0') + ':' + secs.toString().padStart(2, '0');
    },
    goToCountdown() {
      this.$router.push('/countdown');
    },
    onTaskSaved() {
      this.getTaskList();
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
.context-menu {
  position: fixed;
  background: white;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}
.context-menu-item {
  padding: 8px 16px;
  cursor: pointer;
}
.context-menu-item:hover {
  background: #f0f0f0;
}
.timer-window {
  position: fixed;
  right: 20px;
  top: 20px;
  width: 300px;
  background: linear-gradient(135deg, rgba(82, 67, 170, 0.1), rgba(237, 80, 180, 0.1));
  border: 2px solid #5243aa;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(82, 67, 170, 0.3);
  z-index: 999;
  padding: 20px;
  backdrop-filter: blur(10px);
}
.timer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-weight: bold;
  font-size: 16px;
  color: white;
  text-shadow: -1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000;
}
.close-btn {
  cursor: pointer;
  font-size: 24px;
  color: white;
  text-shadow: -1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000;
}
.close-btn:hover {
  color: rgba(255, 255, 255, 0.8);
}
.timer-display {
  font-size: 48px;
  text-align: center;
  margin: 20px 0;
  font-weight: bold;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}
.timer-controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.timer-controls button {
  flex: 1;
  margin: 0 5px;
  padding: 10px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
  background: linear-gradient(135deg, #5243aa, #6b5bb8);
  color: white;
  font-size: 14px;
  transition: all 0.3s;
}
.timer-controls button:hover:not(:disabled) {
  background: linear-gradient(135deg, #6b5bb8, #5243aa);
  transform: translateY(-2px);
}
.timer-controls button:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}
.timer-controls button:nth-child(2) {
  background: linear-gradient(135deg, #ed50b4, #f069c4);
}
.timer-controls button:nth-child(2):hover:not(:disabled) {
  background: linear-gradient(135deg, #f069c4, #ed50b4);
}
.timer-records {
  border-top: 1px solid #eee;
  padding-top: 15px;
}
.record-title {
  font-weight: bold;
  margin-bottom: 10px;
  color: white;
  text-shadow: -1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000;
}
.record-list {
  max-height: 200px;
  overflow-y: auto;
}
.record-item {
  display: flex;
  justify-content: space-between;
  padding: 8px;
  font-size: 13px;
  background: linear-gradient(135deg, rgba(82, 67, 170, 0.05), rgba(237, 80, 180, 0.05));
  border-radius: 4px;
  margin-bottom: 5px;
  border: 1px solid rgba(82, 67, 170, 0.2);
}
.record-item:last-child {
  margin-bottom: 0;
}
.record-start {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  text-shadow: -1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000;
}
.record-duration {
  color: rgba(255, 255, 255, 1);
  font-weight: bold;
  text-shadow: -1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000;
}
.record-end {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  text-shadow: -1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000;
}
</style>