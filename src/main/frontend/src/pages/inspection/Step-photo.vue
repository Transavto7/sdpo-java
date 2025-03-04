<script>
import { setDriverPhoto, closeDriverPhoto } from '@/helpers/api/api';

export default {
    data() {
        return {
            connection: null,
            image: null,
            translation: true,
            loading: true,
        }
    },
    mounted() {
        if (this.driver.photo) {
            this.$router.push({ name: 'step-ride' });
            return;
        }

        this.connect();
    },
    unmounted() {
       this.disconnect();
    },
    computed: {
        inspection() {
            return this.$store.state.inspection;
        },
        driver() {
            return this.$store.state.driver ?? this.$store.state.employee ?? {};
        }
    },
    methods: {
        next() {
            const reader = new FileReader();
            reader.readAsDataURL(this.image); 
            reader.onloadend = () => {
                setDriverPhoto(this.driver?.hash_id, reader.result.split(',')[1]);
            }

            this.$router.push({ name: 'step-ride' })
        },
        connect() {
            this.connection = new WebSocket("ws://localhost:8080/video")

            this.connection.onmessage = (event) => {
                if (this.translation) {
                    this.loading = false;
                    const data = event.data;
                    const img = URL.createObjectURL(data);
                    this.$refs.video.src = img;
                    this.image = data;
                }
            }

            this.connection.onopen = (event) => {
                console.log("Successfully connected to the echo websocket server...")
            }
            
            this.connection.error = (error) => {
                console.log(error);
            }
        },
        async disconnect() {
            console.log('Disconnect websoket server');
            await closeDriverPhoto();
        }
    }
}
</script>

<template>
    <div class="step-photo__outer">
        <h3 class="animate__animated animate__fadeInDown">Сделайте снимок</h3>

        <div v-if="loading" class="admin__loading animate__animated animate__fadeInUp">
            <div class="lds-ring"><div></div><div></div><div></div><div></div></div> 
            Загрузка
        </div>

        <div v-if="!loading" class="step-photo__video animate__animated animate__fadeInDown" :class="{preview: !translation}">
            <img ref="video">
        </div>

        <div class="step-photo__btns" v-if="!loading">
            <button @click="translation = false" v-if="translation"
                class="btn animate__animated animate__fadeInDown">Фотография</button> 
            <button @click="next()" v-if="!translation"
                class="btn blue animate__animated animate__fadeInDown">Сохранить</button> 
            <button @click="translation = true" v-if="!translation"
                class="btn animate__animated animate__fadeInDown">Отмена</button> 
        </div>
    </div>
</template>