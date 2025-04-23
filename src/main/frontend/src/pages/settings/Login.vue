<script>
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";

export default {
  components: {InputPersonalNumberForm},
   name: 'Login',
   data() {
    return {
        password: '',
    }
   },
   methods: {
    login() {
        if (this.password !== this.config?.main?.password) {
            return;
        }

        this.$store.state.admin = true;
        this.$router.push('/settings');
    },
     updatePassword(inputPassword) {
       this.password = inputPassword;
     },
   },
   computed: {
    config() {
       return this.$store.state.config;
    }
   },
   watch: {
        password: function (val) {
            this.login();
        }
   },
}
</script>

<template>
    <div class="login">
        <div class="login-form">
        <div class="login-form__title animate__animated animate__fadeInDown d-4">
            Введите пароль администратора
        </div>

        <div class="login-form__input animate__animated animate__fadeIn d-5">
            <input type="password" v-model="password" />
        </div>

          <input-personal-number-form
              @password=" (inputPassword) => updatePassword(inputPassword)"
          />
    </div>
    </div>
</template>