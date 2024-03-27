<script>
import InputNumber from "@/components/InputNumber";
export default {
  name: "InputPersonalNumberForm",
  components: {InputNumber},
  data() {
    return {
      password: '',
    }
  },
  methods: {
    pushCharIntoPassword(char) {
      this.password += char;
    },
    extractLastCharIntoPassword() {
      this.password = this.password.slice(0, -1)
    },
    clearPassword() {
      this.password = '';
    },
    emitPass() {
      this.$emit('password', this.password);
    }
  },
  watch: {
    password: function () {
      this.emitPass();
    }
  },
}
</script>

<template>
  <input-number
      @pushIntoNumber="(char) => pushCharIntoPassword(char)"
      @popIntoNumber="() => extractLastCharIntoPassword()"
      @clearNumber="() => clearPassword()"
  />
</template>