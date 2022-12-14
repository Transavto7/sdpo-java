<script>
import { computed } from '@vue/runtime-core';

export default {
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
        this.$router.push('/admin');
    }
   },
   computed: {
    config() {
       return this.$store.state.config;
    }
   },
   mounted() {
    this.$refs.numbers.querySelectorAll('button').forEach((button, index) => {
        button.classList.add('animate__animated');
        button.classList.add('animate__fadeInUp');
        button.style.setProperty('animation-delay', `${0.6 + (0.05 * index)}s`);
    });
   },
   watch: {
        password: function (val) {
            this.login();
        }
   },
}
</script>

<template>
    <div class="login-form">
        <div class="login-form__title animate__animated animate__fadeInDown d-4">
            Введите пароль администратора
        </div>

        <div class="login-form__input animate__animated animate__fadeIn d-5">
            <input type="password" v-model="password" />
        </div>

        <div class="number-buttons" ref="numbers">
            <button class="number-buttons__item" @click="password += '1'">1</button>
            <button class="number-buttons__item" @click="password += '2'">2</button>
            <button class="number-buttons__item" @click="password += '3'">3</button>
            <button class="number-buttons__item" @click="password += '4'">4</button>
            <button class="number-buttons__item" @click="password += '5'">5</button>
            <button class="number-buttons__item" @click="password += '6'">6</button>
            <button class="number-buttons__item" @click="password += '7'">7</button>
            <button class="number-buttons__item" @click="password += '8'">8</button>
            <button class="number-buttons__item" @click="password += '9'">9</button>
            <button class="number-buttons__item" @click="password = ''"><i class="ri-close-fill"></i></button>
            <button class="number-buttons__item" @click="password += '0'">0</button>
            <button class="number-buttons__item" @click="password = password.slice(0, -1)"><i class="ri-delete-back-line"></i></button>
        </div>
    </div>
</template>