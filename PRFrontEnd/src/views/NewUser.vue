<template>
  <div>
    <span>username</span>
    <input v-model="username"/>
    <br>
    <span>password</span>
    <input v-model="password"/>
    <br>
    <button @click="register">注册</button>
  </div>
</template>

<script>
import JSEncrypt from 'jsencrypt';
import {postAction} from '@/network/request';
import md5 from 'blueimp-md5';

let rsaOaepParams = {name: 'RSA-OAEP', modulusLength: 512, publicExponent: new Uint8Array([1, 0, 1]), hash: 'SHA-256'};
export default {
  name: 'NewUser',
  data() {
    return {
      username: '',
      password: '',
      publicKey: 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNtt62S50Xxmc0OXmT2Zibouvsimvd6z//01qIjXT0oV2QcCloBxkhmfaHYgt94+vY997HHhMqPMLvUd7vBCjLtkRiXm5CG1jyU6XYxDPG9iCYbpLGBXow228T48wWg8fI3pK6D115hQi4NxA5F9kebsTnQSL3SRlA12DsgCnlswIDAQAB',
      url:{
        register:"/user/register"
      }
    };
  },
  methods:{
    register(){
      let params = {
        username:this.username,
        password: md5(this.username + this.password)
      };
      let encrypt = new JSEncrypt();
      encrypt.setPublicKey(this.publicKey);
      console.log("textBefore",JSON.stringify(params));
      let encryptedText = encrypt.encrypt(JSON.stringify(params));
      console.log(encryptedText);
      // console.log(btoa(encryptedText));
      postAction(this.url.register, encryptedText,{"Content-Type":"text/plain"}).then(res => {
        if (res.success) {
          this.$router.back();
        }
        else {
          window.alert('注册失败，请联系管理员');
        }
      });
    }
  }
};
</script>

<style scoped>

</style>