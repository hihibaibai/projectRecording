<template>
  <div>
    <button @click="init">aaaaa</button>
  </div>
</template>

<script>
import JSEncrypt from 'jsencrypt';
// const text = "admin123456";
const text = "{\"username\":\"admin\",\"password\":\"a66abb5684c45962d887564f08346e8d\"}";
const publicKey= 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNtt62S50Xxmc0OXmT2Zibouvsimvd6z//01qIjXT0oV2QcCloBxkhmfaHYgt94+vY997HHhMqPMLvUd7vBCjLtkRiXm5CG1jyU6XYxDPG9iCYbpLGBXow228T48wWg8fI3pK6D115hQi4NxA5F9kebsTnQSL3SRlA12DsgCnlswIDAQAB';
export default {
  name: 'encryptExample',
  methods:{
    init(){
      // const keyPair =  this.generateKeyPair();
      // debugger;
      let encrypt = new JSEncrypt();
      encrypt.setPublicKey(publicKey);
      let encrypted = encrypt.encrypt(text);
      console.log(encrypted);

/*      crypto.subtle.importKey(
          'pkcs8',
          Uint8Array.from(atob(publicKey), (c) => c.charCodeAt(0)).buffer,
          {
            name: "RSA-OAEP",
            // modulusLength: 2048,
            // publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
            hash: "SHA-256",
          },
          true,
          ['encrypt']
      ).then(pubKey => {
        console.log(pubKey);
        crypto.subtle.encrypt(
            {
              name: 'RSA-OAEP',
            },
            pubKey,
            new TextEncoder().encode(text),
        ).then(res => {
          let binary ='';
          let bytes = new Uint8Array(res);
          let len = bytes.byteLength;
          for (let i = 0; i < len; i++) {
            binary += String.fromCharCode(bytes[i]);
          }
          console.log(new Uint8Array(res));
          console.log(btoa(binary));
        });
      });*/

/*      crypto.subtle.generateKey(
          {
            name: "RSA-OAEP",
            modulusLength: 2048, //密钥长度，可以是1024, 2048, 4096，建议2048以上
            publicExponent: new Uint8Array([0x01, 0x00, 0x01]), // 公共指数e，一般用65537
            hash: "SHA-256", //可以是"SHA-1", "SHA-256", "SHA-384", "SHA-512"
          },
          true,
          ["encrypt", "decrypt"]
      ).then(keyPair => {
        pubKey = keyPair.publicKey;
        crypto.subtle.encrypt(
            {
              name: 'RSA-OAEP'
            },
            keyPair.publicKey,
            new TextEncoder().encode(text),
        ).then(res => {
          console.log(res);
        });
        crypto.subtle.exportKey('spki', pubKey).then(res => {console.log(res);});
      });*/

      // this.encryptMessage(text, keyPair.publicKey);
      // const plainText =  this.decryptMessage(cipherText, keyPair.privateKey);
    },

    // async encryptMessage(message, publicKey){
    //   const encoder = new TextEncoder();
    //   const data = encoder.encode(message);
    //   debugger;
    //   return await crypto.subtle.encrypt(
    //       {
    //         name: "RSA-OAEP",
    //       },
    //       publicKey,
    //       data
    //   );
    // },

    // async decryptMessage(cipherText, privateKey){
    //   const result = await crypto.subtle.decrypt(
    //       {
    //         name: "RSA-OAEP"
    //       },
    //       privateKey,
    //       cipherText
    //   );
    //   const decoder = new TextDecoder();
    //   return decoder.decode(result);
    // },

    async generateKeyPair() {
      crypto.subtle.generateKey(
          {
            name: "RSA-OAEP",
            modulusLength: 2048, //密钥长度，可以是1024, 2048, 4096，建议2048以上
            publicExponent: new Uint8Array([0x01, 0x00, 0x01]), // 公共指数e，一般用65537
            hash: "SHA-256", //可以是"SHA-1", "SHA-256", "SHA-384", "SHA-512"
          },
          true,
          ["encrypt", "decrypt"]
      ).then(res => {
        return res;
      });
    }
  }
};
</script>

<style scoped>

</style>