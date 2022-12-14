<script>
import { makePhoto, makeVideo } from '@/helpers/camera';
import { checkPulse } from '@/helpers/tonometer';
import { getTemp } from '@/helpers/thermometer';
import { getAlcometerResult } from '@/helpers/alcometer';

export default {
    data() {
        return {
            show: '',
            image64: null,
            temp: null,
            timeout: 0,
            loading: false,
        }
    },
    methods: {
        async photo() {
            this.show = 'loading';
            this.image64 = await makePhoto();
            this.show = '';
            if (this.image64) {
                this.show = 'image';
            }
        },
        async video() {
            this.show = 'loading';
            const videoData = await makeVideo();
            this.show = '';

            if (videoData.duration) {
                this.show = 'video';
            }
        },
        async thermometer() {
            this.show = 'loading';
            this.temp = await getTemp();
            this.show = '';
            
            if (this.temp) {
                this.show = 'temp';
            }
        },
        async alcometer() {
            await getAlcometerResult();
        },
    }
}
</script>

<template>
    <div class="admin__testing">
        <button :disabled="show === 'loading'" @click="photo()" class="btn blue">Тестовый снимок</button>
        <button :disabled="show === 'loading'" @click="video()" class="btn blue">Тестовое видео</button>
        <button :disabled="show === 'loading'" @click="thermometer()" class="btn blue">Изерить температуру</button>
        <button :disabled="show === 'loading'" @click="alcometer()" class="btn blue">Тест алкоголя</button>

        <div v-if="show === 'loading'" class="admin__loading">
            <div class="lds-ring"><div></div><div></div><div></div><div></div></div> 
            Загрузка
        </div>

        <div v-else class="admin__testing-result">
            <img v-if="show === 'image'" :src="'data:image/jpg;base64,' + image64">

            <div v-if="show === 'video'" class="admin__testing-video">
                <video autoplay="autoplay" controls>
                    <source src="http://localhost:8080/video" type="video/mp4">
                </video>
            </div>

            <div v-if="show === 'temp'" class="admin__testing-temp">
                {{ temp }} <span>°C</span>
            </div>
        </div>
    </div>
</template>