import Vue from 'vue';
import App from './App.vue';
import router from '@/router';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
// debugger;
Vue.config.productionTip = false;

Vue.use(ElementUI);

console.log(window._config['baseBackendDomain'] );

new Vue({
  router,
  render: h => h(App),
}).$mount('#app');
