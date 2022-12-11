<script>
import { getDriver } from '../helpers/api';
import { useToast } from "vue-toastification";

export default {
   name: 'Home',
   data() {
    return {
        driver_id: '',
        toast: useToast()
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

        this.$router.push('/step/1');
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
    <div class="driver-form">
        <div class="driver-form__title">
            Введите ваш идентификатор
        </div>
        <div class="driver-form__input">
            <input type="number" v-model="driver_id" />
        </div>

        <div class="number-buttons">
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
        <button @click="start" class="btn">начать осмотр</button>
    </div>
</template>