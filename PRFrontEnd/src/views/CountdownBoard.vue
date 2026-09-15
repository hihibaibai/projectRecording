<template>
  <div class="countdown-board">
    <div class="header">
      <button @click="goBack" class="back-btn">返回</button>
      <span class="title">倒计时看板</span>
    </div>
    <div class="grid-container">
      <div v-for="task in sortedTasks" :key="task.uuid" class="grid-item" :class="{ 'overtime': isOvertime(task) }" @click="handleCardClick(task)">
        <div class="project-name">{{ task.projectName }}</div>
        <div class="task-name">{{ task.taskName }}</div>
        <div class="countdown-time">{{ formatCountdown(task) }}</div>
        <div class="overtime-label" v-if="isOvertime(task)">已超时</div>
      </div>
    </div>
    <task-detail-drawer :visible.sync="showDrawer" :task-data="currentTask" :project-name-data-list="projectNameDataList" @saved="onTaskChanged" @removed="onTaskChanged" @completed="onTaskChanged"></task-detail-drawer>
  </div>
</template>

<script>
import {getAction} from '@/network/request';
import TaskDetailDrawer from '@/components/TaskDetailDrawer.vue';

export default {
  name: 'CountdownBoard',
  components: {
    TaskDetailDrawer
  },
  data() {
    return {
      url: {
        taskList: '/task/getTaskList',
        getTask: '/task/getTask'
      },
      taskList: [],
      now: new Date(),
      timer: null,
      showDrawer: false,
      currentTask: {},
      projectNameDataList: [],
      lastClickTime: 0,
      lastClickTaskUuid: null
    };
  },
  computed: {
    sortedTasks() {
      let filtered = this.taskList.filter(task => {
        return task.estimateEndTime && !task.finished;
      });
      filtered.sort((a, b) => {
        return new Date(a.estimateEndTime) - new Date(b.estimateEndTime);
      });
      return filtered;
    }
  },
  mounted() {
    this.getTaskList();
    this.timer = setInterval(() => {
      this.now = new Date();
    }, 1000);
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  methods: {
    getTaskList() {
      getAction(this.url.taskList).then(res => {
        this.taskList = res.result;
      });
    },
    isOvertime(task) {
      return this.now.getTime() > new Date(task.estimateEndTime).getTime();
    },
    formatCountdown(task) {
      let endTime = new Date(task.estimateEndTime);
      let diff = Math.abs(this.now.getTime() - endTime.getTime());
      let totalSeconds = Math.floor(diff / 1000);
      let days = Math.floor(totalSeconds / 86400);
      let hours = Math.floor((totalSeconds % 86400) / 3600);
      let minutes = Math.floor((totalSeconds % 3600) / 60);
      let seconds = totalSeconds % 60;
      let dayStr = days.toString().padStart(2, '0') + '天 ';
      let timeStr = hours.toString().padStart(2, '0') + ':' +
        minutes.toString().padStart(2, '0') + ':' +
        seconds.toString().padStart(2, '0');
      return dayStr + timeStr;
    },
    goBack() {
      this.$router.push('/');
    },
    openTaskDetail(task) {
      getAction(this.url.getTask, {taskUid: task.uuid}).then(res => {
        this.currentTask = res.result;
        this.showDrawer = true;
      });
    },
    handleCardClick(task) {
      let clickTime = Date.now();
      if (this.lastClickTaskUuid === task.uuid && clickTime - this.lastClickTime < 300) {
        this.lastClickTaskUuid = null;
        this.openTaskDetail(task);
      }
      else {
        this.lastClickTaskUuid = task.uuid;
        this.lastClickTime = clickTime;
      }
    },
    onTaskChanged() {
      this.getTaskList();
    }
  }
};
</script>

<style scoped>
.countdown-board {
  background: linear-gradient(60deg, rgb(82, 67, 170), rgb(237, 80, 180));
  width: 100%;
  height: 100%;
  overflow-y: auto;
}
.header {
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 20px;
}
.back-btn {
  padding: 8px 16px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
  background: rgba(255, 255, 255, 0.9);
  color: #5243aa;
  font-size: 14px;
  font-weight: bold;
}
.title {
  margin-left: 20px;
  font-size: 24px;
  font-weight: bold;
  color: white;
  text-shadow: -1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000;
}
.grid-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(4, 1fr);
  gap: 15px;
  padding: 20px;
  height: calc(100% - 60px);
}
.grid-item {
  background: rgba(255, 255, 255, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px;
  backdrop-filter: blur(10px);
  position: relative;
  cursor: pointer;
  user-select: none;
}
.project-name {
  width: 100%;
  text-align: left;
  font-size: 20px;
  font-weight: bold;
  color: white;
  text-shadow: -1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000;
  margin-bottom: 5px;
}
.grid-item.overtime {
  background: rgba(255, 50, 50, 0.3);
  border-color: rgba(255, 100, 100, 0.6);
}
.task-name {
  font-size: 20px;
  font-weight: bold;
  color: white;
  text-shadow: -1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000;
  margin-bottom: 15px;
  text-align: center;
  word-break: break-all;
}
.countdown-time {
  font-size: 42px;
  font-weight: bold;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  font-family: 'Courier New', monospace;
}
.overtime-label {
  margin-top: 10px;
  font-size: 18px;
  font-weight: bold;
  color: #ffcccc;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}
</style>
