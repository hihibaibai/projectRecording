<template>
  <div :class="['sidebar', { 'collapsed': isCollapsed }]">
    <!-- 菜单头部 -->
    <div class="sidebar-header">
      <h2>菜单</h2>
      <button @click="toggleCollapse" class="collapse-btn">
        {{ isCollapsed ? '展开' : '折叠' }}
      </button>
    </div>

    <!-- 菜单内容 -->
    <ul class="menu">
      <li v-for="item in menuItems" :key="item.name">
        <div @click="toggleSubMenu(item)" class="menu-item">
          <span>{{ item.name }}</span>
          <span v-if="item.submenu" class="arrow">{{ item.isOpen ? '↑' : '↓' }}</span>
        </div>

        <!-- 子菜单 -->
        <ul v-if="item.submenu && item.isOpen" class="submenu">
          <li v-for="subItem in item.submenu" :key="subItem.name" class="submenu-item">
            <span>{{ subItem.name }}</span>
          </li>
        </ul>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'MySideMenu',
  data() {
    return {
      isCollapsed: false, // 控制菜单是否折叠
      menuItems: [
        { name: '首页', submenu: null },
        { name: '用户管理', submenu: [{ name: '用户列表' }, { name: '角色管理' }], isOpen: false },
        { name: '设置', submenu: null }
      ]
    };
  },
  methods: {
    // 切换菜单的展开/折叠
    toggleCollapse() {
      this.isCollapsed = !this.isCollapsed;
    },
    // 切换子菜单的显示状态
    toggleSubMenu(item) {
      if (item.submenu) {
        item.isOpen = !item.isOpen;
      }
    }
  }
};
</script>

<style scoped>
/* 侧边栏样式 */
.sidebar {
  width: 250px;
  height: 100%;
  background-color: #2c3e50;
  color: white;
  transition: width 0.3s;
  overflow: hidden;
  padding-top: 20px;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-header {
  text-align: center;
  margin-bottom: 20px;
}

.sidebar-header h2 {
  margin: 0;
}

.collapse-btn {
  background-color: #34495e;
  color: white;
  border: none;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  margin-top: 10px;
}

.menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-item {
  padding: 10px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
}

.menu-item:hover {
  background-color: #34495e;
}

.arrow {
  font-size: 12px;
}

.submenu {
  list-style: none;
  padding-left: 20px;
  margin: 0;
}

.submenu-item {
  padding: 8px 10px;
}

.submenu-item:hover {
  background-color: #34495e;
}
</style>
