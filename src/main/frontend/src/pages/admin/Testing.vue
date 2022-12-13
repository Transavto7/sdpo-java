<script>
import { makePhoto, makeVideo } from '@/helpers/camera';
import { checkPulse } from '@/helpers/tonometer';

export default {
    data() {
        return {
            show: '',
            image64: null,
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
                this.show = 'timer';
                this.timeout = videoData.duration;

                setInterval(() => {
                    this.timeout--;

                    if (this.timeout == 0) {
                        this.show = 'loading';
                    }

                    if (this.timeout <= -30) {
                        this.show = 'video';
                        clearInterval(this);
                    }
                }, 1000);
            }
        },
        async tonometer() {
            this.show = 'loading';
            const pulse = await checkPulse();
            console.log(pulse);
            this.show = '';
            
            if (pulse) {
                // 
            }
        }
    }
}
</script>

<template>
    <div class="admin__testing">
        <button @click="photo()" class="btn blue">Тестовый снимок</button>
        <button @click="video()" class="btn blue">Тестовый видео</button>
        <button @click="tonometer()" class="btn blue">Тестовый танометра</button>

        <div v-if="show === 'loading'" class="admin__loading">
            <div class="lds-ring"><div></div><div></div><div></div><div></div></div> 
            Загрузка
        </div>

        <div v-else class="admin__testing-result">
            <img v-if="show === 'image'" :src="'data:image/jpg;base64,' + image64">
            
            <div v-if="show === 'timer'" class="admin__testing-timer">
                Подождите
                <span>{{ timeout }}</span>
            </div>

            <div v-if="show === 'video'" class="admin__testing-video">
                <video autoplay="autoplay" controls>
                    <source src="http://localhost:8080/video" type="video/mp4">
                </video>
            </div>
        </div>
    </div>
</template>