<script>
import { makePhoto, makeVideo } from '@/helpers/camera';
import { getPressure } from '@/helpers/tonometer';
import { getTemp } from '@/helpers/thermometer';
import { getAlcometerResult } from '@/helpers/alcometer';
import { print } from '@/helpers/printer';

export default {
    data() {
        return {
            show: '',
            image64: null,
            temp: null,
            alcometerPpm: null,
            pressure: null,
            timeout: 0,
            loading: false,
            interval: null,
        }
    },
    methods: {
        async photo() {
            this.show = 'loading';
            clearInterval(this.interval);
            this.image64 = await makePhoto();
            this.show = '';
            if (this.image64) {
                this.show = 'image';
            }
        },
        async video() {
            this.show = 'loading';
            clearInterval(this.interval);
            const videoData = await makeVideo();
            this.show = '';

            if (videoData.duration) {
                this.show = 'video';
            }
        },
        async thermometer() {
            this.show = 'loading';
            clearInterval(this.interval);
            this.interval = setInterval(async () => {
                this.temp = await getTemp();
                this.show = '';
                
                if (this.temp === 'next') {
                    return;
                }

                this.show = 'temp';
                clearInterval(this.interval);
            }, 1000);
            
        },
        async alcometer() {
           this.show = 'loading';
           clearInterval(this.interval);
           this.interval = setInterval(async () => {
                const result = await getAlcometerResult();

                if (result === 'next') {
                    return;
                }

                this.alcometerPpm = Number(result) || 0;
                this.show = 'alcometer';
                clearInterval(this.interval);
            }, 1000);
        },
        async printer() {
            this.show = 'loading';
            this.alcometerPpm = await print();
            this.show = '';
        },
        async tonometer() {
            this.show = 'loading';
            clearInterval(this.interval);
            this.interval = setInterval(async () => {
                const result = await getPressure();

                if (result === 'next') {
                    return;
                }

                this.pressure = result;

                clearInterval(this.interval);
                this.show = 'pressure';
            }, 1000);
        },
    }
}
</script>

<template>
    <div class="admin__testing">
        <button :disabled="show === 'loading'" @click="photo()" class="btn blue animate__animated animate__fadeInUp">Тестовый снимок</button>
        <button :disabled="show === 'loading'" @click="video()" class="btn blue animate__animated animate__fadeInUp d-1">Тестовое видео</button>
        <button :disabled="show === 'loading'" @click="thermometer()" class="btn blue animate__animated animate__fadeInUp d-2">Тест пирометра</button>
        <button :disabled="show === 'loading'" @click="alcometer()" class="btn blue animate__animated animate__fadeInUp d-3">Тест алкометра</button>
        <button :disabled="show === 'loading'" @click="printer()" class="btn blue animate__animated animate__fadeInUp d-4">Тестовая печать</button>
        <button :disabled="show === 'loading'" @click="tonometer()" class="btn blue animate__animated animate__fadeInUp d-5">Тест тонометра</button>

        <div v-if="show === 'loading'" class="admin__loading animate__animated animate__fadeInUp">
            <div class="lds-ring"><div></div><div></div><div></div><div></div></div> 
            Загрузка
        </div>

        <div v-else class="admin__testing-result">
            <img class="animate__animated animate__fadeInUp" v-if="show === 'image'" :src="'data:image/jpg;base64,' + image64">

            <div v-if="show === 'video'" class="admin__testing-video animate__animated animate__fadeInUp">
                <video autoplay="autoplay" controls>
                    <source src="http://localhost:8080/video" type="video/mp4">
                </video>
            </div>

            <div v-if="show === 'temp'" class="admin__testing-temp animate__animated animate__fadeInUp">
                {{ temp }} <span>°C</span>
            </div>

            <div v-if="show === 'alcometer'" class="admin__testing-temp animate__animated animate__fadeInUp">
                {{ alcometerPpm === undefined ? 'Не удалось получить результат' : alcometerPpm + ' ‰' }}
            </div>
            
            <div v-if="show === 'pressure'" class="admin__testing-pressure animate__animated animate__fadeInUp">
                <div class="admin__testing-pressure-item">
                    <span>Систолическое давление</span>
                    {{ pressure?.systolic ? pressure.systolic + ' мм. рт.ст' : 'Результатов нет' }}
                </div>
                <div class="admin__testing-pressure-item">
                    <span>Диастолическое давление</span>
                    {{ pressure?.diastolic ? pressure.diastolic + ' мм. рт.ст' : 'Результатов нет' }}
                </div>
                <div class="admin__testing-pressure-item">
                    <span>Пульс</span>
                    {{ pressure?.pulse ? pressure.pulse + ' уд/мин' : 'Результатов нет' }}
                </div>
            </div>
        </div>
    </div>
</template>