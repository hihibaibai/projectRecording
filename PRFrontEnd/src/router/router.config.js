import LoginHome from '@/views/LoginHome.vue';
import TaskRecordHome from '@/views/TaskRecordHome.vue';
import NewUser from '@/views/NewUser.vue';
import CountdownBoard from '@/views/CountdownBoard.vue';

export const routerMap = [
  {
    path: '/',
    name: 'home',
    component: TaskRecordHome,
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
  },
  {
    path: '/countdown',
    name: 'countdown',
    component: CountdownBoard
  }
];