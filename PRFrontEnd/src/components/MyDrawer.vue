<template>
  <div>
    <!-- 按钮触发抽屉 -->
    <button @click="openDrawer" class="open-btn">
      打开抽屉
    </button>

    <!-- 遮罩层 -->
    <div v-if="value" @click="closeDrawer" class="overlay"></div>

    <!-- 抽屉 -->
    <div :class="['drawer', { 'open': value }]">
      <div class="drawer-header">
        <h3>{{ title }}</h3>
      </div>
      <div class="drawer-content">
        <slot/>
<!--        <p>这里是抽屉的内容，可以添加更多的内容。</p>-->
<!--        <button @click="closeDrawer" class="close-btn">关闭抽屉</button>-->
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MyDrawer',
  data() {
    return {
      isOpen: false // 控制抽屉的显示与隐藏
    };
  },
  props:{
    value:{
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '标题'
    }
  },
  watch:{
    value(newVal,oldVal){
      if (newVal){
        this.openDrawer();
      }
      else {
        this.closeDrawer();
      }
    }
  },
  methods: {
    openDrawer() {
      this.isOpen = true; // 打开抽屉
    },
    closeDrawer() {
      this.isOpen = false; // 关闭抽屉
    }
  }
};
</script>

<style scoped>
/* 按钮样式 */
.open-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 10px 20px;
  background-color: #409EFF;
  color: white;
  border: none;
  cursor: pointer;
}

.close-btn {
  padding: 5px 10px;
  background-color: #f56c6c;
  color: white;
  border: none;
  cursor: pointer;
}

/* 抽屉样式 */
.drawer {
  position: fixed;
  top: 0;
  right: -300px; /* 初始位置：隐藏在右侧 */
  width: 300px;
  height: 100%;
  background-color: white;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.3);
  transition: right 0.3s ease; /* 平滑的过渡效果 */
  z-index: 1000;
}

/* 抽屉打开时 */
.drawer.open {
  right: 0; /* 抽屉滑动到可见位置 */
}

/* 抽屉的头部 */
.drawer-header {
  background-color: #409EFF;
  color: white;
  padding: 15px;
  text-align: center;
}

/* 抽屉内容 */
.drawer-content {
  padding: 20px;
}

/* 遮罩层 */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease, visibility 0s 0.3s; /* 隐藏时的过渡效果 */
  z-index: 999;
}

/* 遮罩层显示 */
.overlay {
  visibility: visible;
  opacity: 1;
  transition: opacity 0.3s ease, visibility 0s 0s;
}
</style>
