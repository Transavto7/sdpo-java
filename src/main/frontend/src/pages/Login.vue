<script>
import { computed } from '@vue/runtime-core';
import { useToast } from "vue-toastification";

export default {
   name: 'Login',
   data() {
    return {
        password: '',
        toast: useToast()
    }
   },
   methods: {
    login() {
        if (!this.password) {
            this.toast.error('Введите пароль');
            return;
        }

        if (this.password !== this.config?.main?.password) {
            this.toast.error('Неверный пароль');
            this.password = '';
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
   }
}
</script>

<template>
    <div class="login-form">
        <div class="login-form__title">
            Введите пароль администратора
        </div>

        <div class="login-form__input">
            <input type="password" v-model="password" />
        </div>

        <div class="number-buttons">
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
        <button @click="login" class="btn">войти</button>
    </div>
</template>