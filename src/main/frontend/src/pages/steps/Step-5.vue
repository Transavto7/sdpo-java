<script>
import { getAlcometerResult, closeAlcometer } from '@/helpers/alcometer';
import { makePhoto, makeVideo } from '@/helpers/camera';

export default {
    data() {
        return {
            interval: null,
        }
    },
    methods: {
       async saveWebCam() {
            if (JSON.parse(this.system.camera_photo) && !this.inspection.photo) {
                this.inspection.photo = await makePhoto();
            }
            if (JSON.parse(this.system.camera_video) && !this.inspection.video) {
                this.inspection.video = await makeVideo();
            }
        },
        async nextStep() {
            clearInterval(this.interval);
            this.$router.push('/step/6');
            closeAlcometer();
        },
        async prevStep() {
            clearInterval(this.interval);
            this.$router.push('/step/4');
            closeAlcometer();
        }
    },
    async mounted() {
        this.saveWebCam();

        this.interval = setInterval(async () => {
            const result = await getAlcometerResult();

            if (result === 'next') {
                return;
            }

            this.inspection.alcometer_result = Number(result) || 0;
            this.nextStep();
        }, 1000);
    },
    unmounted() {
        clearInterval(this.interval);
    },
    computed: {
        inspection() {
            return this.$store.state.inspection;
        },
        system() {
            return this.$store.state.config?.system || {};
        }
    }
}
</script>
<template>
    <div class="step-5__outer">
        <div class="step-5">
            <h3 class="animate__animated animate__fadeInDown">Проверка на алкоголь</h3>

            <div class="step-5__items">
                <div class="step-5__item animate__animated animate__fadeInUp d-1">
                    <span>1</span>
                    <img style="padding-right: 20px" width="100" src="@/assets/images/alco_guide.png">
                    Проверьте, что вставлена воронка
                </div>
                <div class="step-5__item animate__animated animate__fadeInUp d-2">
                    <span>2</span>
                    <img width="100" src="@/assets/images/alco_guide_2.png">
                    Держите алкотестер на расстоянии 2-3 см от рта
                </div>
                <div class="step-5__text  animate__animated animate__fadeInUp d-2">
                    Дождитесь ГОТОВ на экране алкометра<br><br>
                    Начните дуть с умеренной силой до окончания<br>
                    звукового сигнала.<br><br>
                </div>
            </div>
            <p class="alert red">
                <i class="ri-alarm-warning-fill"></i>
                НЕ ПРИКАСАТЬСЯ К АЛКОТЕСТЕРУ ГУБАМИ
            </p>
        </div>
        <div class="step-buttons">
            <button @click="prevStep()" class="btn opacity blue">Назад</button>
            <button @click="nextStep()" v-if="JSON.parse(system.alcometer_skip)" class="btn">Продолжить</button>
        </div>
    </div>
</template>