import Vue from 'vue';
import Router from 'vue-router';
import {routerMap} from '@/router/router.config';

Vue.use(Router);

export default new Router({
  mode: 'history',
  base: window._config['contextPath'],
  scrollBehavior: () => ({y: 0}),
  routes: routerMap
});