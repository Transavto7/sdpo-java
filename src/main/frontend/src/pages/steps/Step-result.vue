<script>
import { saveInspection, replayPrint } from '@/helpers/api';

export default {
    data() {
        return {
            backTimeout: null,
            result: {}
        }
    },
    async mounted() {
        await this.save();
        // this.backTimeout = setTimeout(() => {
        //     if (this.$route.name == 'step-8') {
        //         this.$router.push('/');
        //     }
        // }, 5000);
    },
    unmounted() {
        clearTimeout(this.backTimeout);
    },
    methods: {
        getSleepStatus(status) {
            return status === 'Да' ? 'Выспались' : 'Не выспались';
        },
        getPeopleStatus(status) {
            return status === 'Да' ? 'Хорошее' : 'Плохое';
        },
        async save() {
            this.result = await saveInspection();
        },
        async replayPrint() {
            await replayPrint();
        }
    },
    computed: {
        inspection() {
            return this.$store.state.inspection;
        },
        system() {
            return this.$store.state.config?.system || {};
        }
    },
}
</script>

<template>
    <div class="step-result">
        <h3 class="animate__animated animate__fadeInDown">Результаты осмотра</h3>
        <div class="step-result__cards">
            <div v-if="system.tonometer_visible" class="step-result__card animate__animated animate__fadeInUp d-1">
                <span>Давление</span>
                {{ inspection.hasOwnProperty('tonometer') ? inspection.tonometer  : 'Неизвестно' }}
            </div>
            <div v-if="system.tonometer_visible" class="step-result__card animate__animated animate__fadeInUp d-2">
                <span>Пульс</span>
                {{ inspection.hasOwnProperty('pulse') ? inspection.pulse : 'Неизвестно' }}
            </div>
            <div v-if="system.alcometer_visible" class="step-result__card animate__animated animate__fadeInUp d-2">
                <span>Количество промилле</span>
                {{ inspection.hasOwnProperty('alcometer_result') ? inspection.alcometer_result + ' ‰' : 'Неизвестно' }}
            </div>
            <div v-if="system.thermometer_visible" class="step-result__card animate__animated animate__fadeInUp d-2">
                <span>Температура тела</span>
                {{ inspection.hasOwnProperty('t_people') ? inspection.t_people + ' °C' : 'Неизвестно' }}
            </div>
            <div v-if="system.question_sleep" class="step-result__card animate__animated animate__fadeInUp d-2">
                <span>Сонливость</span>
                {{ inspection.hasOwnProperty('sleep_status') ? getSleepStatus(inspection.sleep_status) : 'Неизвестно' }}
            </div>
            <div v-if="system.question_helth" class="step-result__card animate__animated animate__fadeInUp d-2">
                <span>Самочувствие</span>
                {{ inspection.hasOwnProperty('people_status') ? getPeopleStatus(inspection.people_status) : 'Неизвестно' }}
            </div>
        </div>
        <div class="step-result__footer">
            <span class="step-result__text animate__animated animate__fadeInUp">
                {{ result.admitted || 'ожидание ответа' }}<br>
                <p v-if="result.admitted === 'Допущен'" 
                    class="animate__animated animate__fadeInUp">
                        Счастливого пути!
                </p>
            </span>

            <div class="step-result__buttons">
                <button @click="$router.push('/')" class="btn blue animate__animated animate__fadeInUp">В начало</button>
                <button v-if="result?.admitted === 'Допущен'" 
                    @click="replayPrint()" 
                    class="btn opacity animate__animated animate__fadeInUp">Повтор печати</button>
            </div>
        </div>
    </div>
</template>