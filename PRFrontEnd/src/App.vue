<template>
  <div id="app">
    <div style="position: absolute; right: 0;top: 0" >
      <button @click="logout" v-if="showLogout()">注销</button>
      <button @click="register">注册</button>
    </div>

<!--    <encrypt-example></encrypt-example>-->
    <router-view></router-view>
  </div>
</template>

<script>
// import TaskManagementHome from '@/components/TaskManagementHome.vue';
import encryptExample from '@/views/encryptExample.vue';

export default {
  name: 'App',
  components: {
    encryptExample
  },
  data(){
    return {
      // showLogout: false,
    };
  },
  methods:{
    logout(){
      window._global['token'] = null;
      this.showLogout();
      location.reload();
    },
    register(){
      this.$router.push('reg');
    },
    showLogout(){
      if (window._global['token'] == null){
        return false;
      }
      return true;
    }
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  width: 100%;
  height: 100%;
}
html {
  width: 100%;
  height: 100%;
}
body {
  width: 100%;
  height: 100%;
  margin: 0;
}
</style>
