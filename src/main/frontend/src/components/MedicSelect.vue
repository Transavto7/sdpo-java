<script>
import { getMedics } from '@/helpers/api';

export default {
    data() {
        return {
            medics: {},
            status: "login",
            password: '',
            show: false,
        }
    },
    async mounted() {
        this.medics = await getMedics();
    },
    methods: {
        login() {
            if (this.password !== this.config?.medic_password) {
                return;
            }
            
            this.password = '';
            this.status = 'select';
        },
        select(user) {
            this.medic.name = user.name;
            this.medic.id = user.id;
            this.status = 'login';
        }
    },
    computed: {
        medic() {
            return this.$store.state.medic;
        },
        config() {
            return this.$store.state.config?.main;
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
    <div v-if="medic.selecting" @click="medic.selecting = false" class="medics__overlay animate__animated animate__fadeIn"></div>
    <div v-if="medic.selecting" class="medics animate__animated animate__fadeInRight">
        <div v-if="status === 'login'" class="medics__form">
            <div class="login-form__title">
                Введите пароль
            </div>

            <div class="login-form__input">
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

            <div class="medics__form-selected">
               {{ medic.name || 'Сотрудник не выбран' }}
            </div>
        </div>

        <div v-else class="medics__select">
            <div v-for="(town, name) in medics" :key="name">
                <div v-if="name" class="medics__town">
                    {{ name }}
                </div>
                <div v-if="name" v-for="(point, name) in town" :key="name">
                    <div class="medics__point">
                        {{ name }}
                    </div>

                    <button v-for="user in point" :key="user.id" class="medics__item" @click="select(user)">
                        {{ user.name }}
                    </button>
                </div>
            </div>
            <div class="medics__town">
                Название города
            </div>
            <div class="medics__point">
                Пункт выпуска
            </div>
            <button class="medics__item">
                Фамилия имя отчетство
            </button>
        </div>
    </div>
</template>