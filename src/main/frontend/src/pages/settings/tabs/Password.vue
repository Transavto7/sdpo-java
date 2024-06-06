<script>
import { savePassword } from '@/helpers/settings';
import { useToast } from "vue-toastification";

export default {
    data() {
        return {
            password: '',
            toast: useToast(),
        }
    },
    methods: {
        async save() {
            await savePassword(this.password);
            this.password = '';
            this.toast.success('Пароль успешно изменен');
            this.$store.state.config.password = this.password;
        }
    },
    mounted() {
        this.$refs.numbers.querySelectorAll('button').forEach((button, index) => {
            button.classList.add('animate__animated');
            button.classList.add('animate__fadeInUp');
            button.style.setProperty('animation-delay', `${0.2 + (0.05 * index)}s`);
        });
    },
}
</script>
<template>
    <div class="admin__password">
        <div class="password-form">
            <div class="password-form__title animate__animated animate__fadeInDown">
            Введите новый пароль
            </div>

            <div class="password-form__input animate__animated animate__fadeIn d-1">
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
            <button v-if="password.length > 3" @click="save" class="btn animate__animated animate__fadeInUp">Сохранить</button>
        </div>
    </div>
</template>