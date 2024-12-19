// 全局设置加载

console.log(process.env.NODE_ENV);
if (process.env.NODE_ENV !== 'development') {
  window._config['baseBackendDomain'] = '192.168.199.187';
  window._config['baseBackendPort'] = '11454';
}
else {
  window._config['baseBackendDomain'] = 'localhost';
  window._config['baseBackendPort'] = '10000';
}
window._config['contextPath'] = '/project-recording';