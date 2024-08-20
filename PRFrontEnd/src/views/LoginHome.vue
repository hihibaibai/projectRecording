<template>
  <div>
    <span>username</span>
    <input v-model="username"/>
    <br>
    <span>password</span>
    <input v-model="password" @keydown.enter="login"/>
    <br>
    <button @click="login">登录</button>
  </div>
</template>

<script>
import {getAction} from '@/network/request';
import md5 from 'blueimp-md5';

export default {
  name: 'LoginHome',
  data(){
    return {
      username: '',
      password: '',
      url: {
        login: '/user/login',
      },
    };
  },
  methods:{
    login(){
      let token = md5(this.username + this.password);
      console.log(this.username + this.password);
      console.log(token);
      getAction(this.url.login, {token:token}).then(res => {
        if (res.success) {
          window._global['token'] = res.result;
          this.$router.back();
        }
        else {
          window.alert('登录失败，请检查用户名密码');
        }
      });
    }
  },
};
</script>

<style scoped>

</style>