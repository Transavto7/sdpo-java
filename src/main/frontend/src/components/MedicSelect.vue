<script>
import { getMedics, saveMedic } from '@/helpers/api';

export default {
    data() {
        return {
            medics: {},
            towns: [],
            status: "login",
            password: '',
        }
    },
    async mounted() {
        this.medics = await getMedics();
        this.towns = Object.keys(this.medics).sort();
    },
    methods: {
        login() {
            if (this.password !== this.config?.medic_password) {
                return;
            }
            
            this.password = '';
            this.status = 'select';
        },
        async select(user) {
            this.$store.state.config.main.selected_medic = {
                name: user.name,
                id: user.id,
                eds: user.eds
            }
            
            this.status = 'login';
            await saveMedic(this.medic);
        }
    },
    computed: {
        medic() {
            return this.$store.state.config?.main?.selected_medic || {};
        },
        config() {
            return this.$store.state.config?.main;
        },
        visible() {
            return this.$store.state.selectingMedic;
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
    <div v-if="visible" 
        @click="$store.state.selectingMedic = false" 
        class="medics__overlay animate__animated animate__fadeIn">
    </div>

    <div v-if="visible" class="medics animate__animated animate__fadeInRight">
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
            <div v-for="(name, index) in towns" :key="index">
                <div v-if="name" class="medics__town">
                    {{ name }}
                </div>
                <div v-if="name" v-for="(point, name) in medics[name]" :key="name">
                    <div class="medics__point">
                        {{ name }}
                    </div>

                    <button v-for="user in point" :key="user.id" class="medics__item" @click="select(user)">
                        {{ user.name }}
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>