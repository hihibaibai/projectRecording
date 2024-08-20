import LoginHome from '@/views/LoginHome.vue';
import TaskManagementHome from '@/views/TaskManagementHome.vue';
import NewUser from '@/views/NewUser.vue';

export const routerMap = [
  {
    path: '/',
    name: 'home',
    component: TaskManagementHome,
    // redirect: '/'
  },
  {
    path: '/login',
    name: 'login',
    component: LoginHome,
    // redirect: '/login'
  },
  {
    path: '/reg',
    name: 'reg',
    component: NewUser
  }
];