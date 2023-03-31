<script>
import { getDriver } from '../helpers/api';
import { useToast } from "vue-toastification";
import MedicSelect from '@/components/MedicSelect.vue';

export default {
   name: 'Home',
   components: { MedicSelect },
   data() {
    return {
        driver_id: '',
        error: '',
        toast: useToast(),
        loading: false,
    }
   },
   methods: {
    async start() {
        const driver = await getDriver(this.driver_id);
        if (!driver) {
            this.driver_id = '';
            this.toast.error('Водитель с указаным ID не найден');
            return;
        }

        this.$store.state.inspection.driver_id = driver.hash_id;
        this.$store.state.inspection.driver_fio = driver.fio;

        this.$router.push({ name: 'step-driver' });
    },
    async checkDriver() {
        if (this.driver_id.length < 6) {
            this.$store.state.inspection = {};
            this.error = null;
            return;
        }

        this.loading = true;
        try {
            const driver = await getDriver(this.driver_id);
            if (driver) {
                this.$store.state.inspection.driver_id = driver.hash_id;
                this.$store.state.inspection.driver_fio = driver.fio;
                this.$store.state.driver = driver;
            } else {
                this.$store.state.inspection = {};
                this.error = 'Водитель не найден';
            }
        } catch (error) {
            console.log(error.response?.data?.message);
            this.error = error.response?.data?.message || 'Неизвестная ошибка';
            this.$store.state.inspection = {};
        }
        
        this.loading = false;
    }
   },
   watch: {
        driver_id: function (val) {
            this.checkDriver();
        }
   },
   computed: {
    config() {
        return this.$store.state.config;
    },
    inspection() {
        return this.$store.state.inspection;
    }
   },
   mounted() {
    this.$refs.numbers.querySelectorAll('button').forEach((button, index) => {
        button.classList.add('animate__animated');
        button.classList.add('animate__fadeInUp');
        button.style.setProperty('animation-delay', `${0.6 + (0.05 * index)}s`);
    });
   }
}
</script>

<template>
    <medic-select />
    <div class="home">
        <div class="driver-form">
            <div v-if="inspection.driver_fio" class="driver-form__title animate__animated animate__fadeInDown d-2">
                {{ inspection.driver_fio }}
            </div>
            <div v-else class="driver-form__title animate__animated animate__fadeInDown d-2">
                Введите ваш идентификатор
            </div>
            <div class="driver-form__input">
                <input type="number" class="animate__animated animate__fadeIn d-5" v-model="driver_id" @input="checkDriver" />
            </div>

            <div class="number-buttons" ref="numbers">
                <button class="number-buttons__item" @click="driver_id += '1'">1</button>
                <button class="number-buttons__item" @click="driver_id += '2'">2</button>
                <button class="number-buttons__item" @click="driver_id += '3'">3</button>
                <button class="number-buttons__item" @click="driver_id += '4'">4</button>
                <button class="number-buttons__item" @click="driver_id += '5'">5</button>
                <button class="number-buttons__item" @click="driver_id += '6'">6</button>
                <button class="number-buttons__item" @click="driver_id += '7'">7</button>
                <button class="number-buttons__item" @click="driver_id += '8'">8</button>
                <button class="number-buttons__item" @click="driver_id += '9'">9</button>
                <button class="number-buttons__item" @click="driver_id = ''"><i class="ri-close-fill"></i></button>
                <button class="number-buttons__item" @click="driver_id += '0'">0</button>
                <button class="number-buttons__item" @click="driver_id = driver_id.slice(0, -1)"><i class="ri-delete-back-line"></i></button>
            </div>
            <button v-if="inspection.driver_id && !error" @click="start" class="btn animate__animated animate__fadeInUp">начать осмотр</button>
            <div v-else-if="error" class="driver-form__not-found animate__animated animate__fadeInUp">{{ error }}</div>
        </div>
    </div>
</template>