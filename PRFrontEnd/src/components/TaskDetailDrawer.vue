<template>
  <el-drawer :visible.sync="drawerVisible" direction="rtl" size="70%" @close="handleClose">
    <el-button @click="handleTaskSave">保存</el-button>
    <el-button @click="handleTaskComplete" v-if="!task.finished">完成任务</el-button>
    <el-form v-model="task">
      <el-form-item label="任务名称">
        <input v-model="task.taskName"/>
      </el-form-item>
      <el-form-item label="项目名称">
        <input id="taskProjectName" v-model="task.projectName" type="text" list="taskProjectNameDataList"/>
        <datalist id="taskProjectNameDataList">
          <option v-for="data in projectNameDataList" :key="data" :value="data">{{data + '\u3000'}}</option>
        </datalist>
      </el-form-item>
      <el-form-item label="指定给">
        <input v-model="task.assignedTo"/>
      </el-form-item>
      <el-form-item label="开始时间">
        <date-time-picker v-model="task.startTime"/>
      </el-form-item>
      <el-form-item label="预计完成时间">
        <date-time-picker v-model="task.estimateEndTime"/>
      </el-form-item>
      <el-form-item label="实际完成时间">
        <date-time-picker v-model="task.endTime"/>
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
</template>

<script>
import {postAction} from '@/network/request';
import DateTimePicker from './DateTimePicker';

export default {
  name: 'TaskDetailDrawer',
  components: {
    DateTimePicker
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    taskData: {
      type: Object,
      default: function() {
        return {};
      }
    },
    projectNameDataList: {
      type: Array,
      default: function() {
        return [];
      }
    },
    saveUrl: {
      type: String,
      default: '/task/saveTask'
    },
    removeUrl: {
      type: String,
      default: '/task/removeTask'
    }
  },
  data() {
    return {
      task: {},
      url: {
        saveTask: this.saveUrl,
        removeTask: this.removeUrl
      }
    };
  },
  computed: {
    drawerVisible: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      }
    }
  },
  watch: {
    taskData: {
      handler: function(newVal) {
        let copied = Object.assign({}, newVal);
        if (copied.startTime) {
          copied.startTime = copied.startTime.replace(' ', 'T');
        }
        if (copied.estimateEndTime) {
          copied.estimateEndTime = copied.estimateEndTime.replace(' ', 'T');
        }
        if (copied.endTime) {
          copied.endTime = copied.endTime.replace(' ', 'T');
        }
        this.task = copied;
      },
      deep: true,
      immediate: true
    },
    saveUrl: function(newVal) {
      this.url.saveTask = newVal;
    },
    removeUrl: function(newVal) {
      this.url.removeTask = newVal;
    }
  },
  methods: {
    handleTaskSave() {
      if (!this.task.estimateEndTime) {
        this.$message.warning('请填写预计完成时间');
        return;
      }
      let taskToSave = Object.assign({}, this.task);
      if (taskToSave.startTime) {
        taskToSave.startTime = taskToSave.startTime.replace('T', ' ');
        if (taskToSave.startTime.length === 16) {
          taskToSave.startTime = taskToSave.startTime + ':00';
        }
      }
      if (taskToSave.estimateEndTime) {
        taskToSave.estimateEndTime = taskToSave.estimateEndTime.replace('T', ' ');
        if (taskToSave.estimateEndTime.length === 16) {
          taskToSave.estimateEndTime = taskToSave.estimateEndTime + ':00';
        }
      }
      if (taskToSave.endTime) {
        taskToSave.endTime = taskToSave.endTime.replace('T', ' ');
        if (taskToSave.endTime.length === 16) {
          taskToSave.endTime = taskToSave.endTime + ':00';
        }
      }
      postAction(this.url.saveTask, taskToSave).then(res => {
        if (res.success) {
          this.$emit('saved');
          this.drawerVisible = false;
        }
      });
    },
    handleRemove() {
      postAction(this.url.removeTask, this.task.uuid).then(res => {
        if (res.success) {
          this.$emit('removed');
          this.drawerVisible = false;
        }
      });
    },
    handleTaskComplete() {
      let taskToSave = Object.assign({}, this.task);
      taskToSave.finished = true;
      taskToSave.endTime = new Date(Date.now() + 8 * 3600 * 1000).toISOString()
      .replace("T", " ")
      .replace(/\..+/, "")
      .slice(0, 16);
      if (taskToSave.startTime) {
        taskToSave.startTime = taskToSave.startTime.replace('T', ' ');
      }
      if (taskToSave.estimateEndTime) {
        taskToSave.estimateEndTime = taskToSave.estimateEndTime.replace('T', ' ');
      }
      postAction(this.url.saveTask, taskToSave).then(res => {
        if (res.success) {
          this.$emit('completed');
          this.drawerVisible = false;
        }
      });
    },
    handleClose() {
      this.$emit('update:visible', false);
    }
  }
};
</script>
