<script>
import { getAlcometerResult } from '@/helpers/alcometer';
import { makeMedia } from '@/helpers/camera';

export default {
    data() {
        return {
            interval: null,
            seconds: 5
        }
    },
    methods: {
        async saveWebCam() {
            if ((JSON.parse(this.system.camera_photo) && !this.inspection.photo)
                || (JSON.parse(this.system.camera_video) && !this.inspection.video)) {
                const data = await makeMedia();
                this.inspection.photo = data?.photo;
                this.inspection.video = data?.video;
            }
        },
        async nextStep() {
            this.$router.push({ name: 'step-sleep' });
        },
        async prevStep() {
            this.$router.push({ name: 'step-thermometer' });
        }
    },
    async mounted() {
        this.saveWebCam();
        this.timerInterval = setInterval(() => {
            this.seconds--;
            if (this.seconds < 1) {
                clearInterval(this.timerInterval);
            }
        }, 1000);

        this.requestInterval = setInterval(async () => {
            const result = await getAlcometerResult();

            if (result === undefined || result === null) {
                return;
            }

            if (result === 'next') {
                return;
            }

            this.inspection.alcometer_result = Number(result) || 0;
            this.nextStep();
        }, 1000);
    },
    unmounted() {
        clearInterval(this.requestInterval);
        clearInterval(this.timerInterval);
    },
    computed: {
        inspection() {
            return this.$store.state.inspection;
        },
        system() {
            return this.$store.state.config?.system || {};
        },
        status() {
            if (this.seconds == 5) {
                return 'через ' + this.seconds + ' секунд';
            } else if (this.seconds == 1) {
                return 'через ' + this.seconds + ' секунду';
            } else if (this.seconds < 1) {
                return ' прямо сейчас!';
            } else {
                return 'через ' + this.seconds + ' секунды';
            }
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

                    Дуйте {{ status }}
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