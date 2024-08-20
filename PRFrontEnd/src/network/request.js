import axios from 'axios';
// import Vue from 'vue';
import router from '@/router/index';
import '../config/networkSetting'; // 引用生效设置

// debugger;
const ax = axios.create({
  baseURL: 'http://' + window._config['baseBackendDomain'] + ':' +
      window._config['baseBackendPort'] +
      window._config['contextPath'],
});

ax.interceptors.request.use((request) => {
  if (window._global['token']){
    request.headers['Login-Token'] = window._global['token'];
  }
  // console.log(request.headers);
  return request;
}, (error) =>{
  console.log(error);
});

//接收之后做的事，全局的
ax.interceptors.response.use((response) => {
  return response.data; // 只需要返回data部分就行
},(error) => {
  // console.log(error);
  switch (error.response.status) {
    case 401:
      router.push('login');
      break;
  }
  return error;
});



export function getAction(url, params) {

  return ax({
    url: url,
    method: 'get',
    params: params,
    // headers: header,
  });
}

export function postAction(url, params, headers) {
  return ax({
    url: url,
    method: 'post',
    data: params,
    headers: headers
  });
}

export {
  ax as axios,
};