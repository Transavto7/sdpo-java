<script>
import { saveInspection } from '@/helpers/api';

export default {
    data() {
        return {
            result: {}
        }
    },
    async mounted() {
        await this.save();
        setTimeout(() => {
            this.$router.push('/');
        }, 5000);
    },
    methods: {
        getStatus(status) {
            return status === 'Да' ? 'Выспались' : 'Не выспались';
        },
        async save() {
            this.result = await saveInspection();
        }
    },
    computed: {
        inspection() {
            return this.$store.state.inspection;
        }
    },
}
</script>

<template>
    <div class="step-result">
        <h3 class="animate__animated animate__fadeInDown">Результаты осмотра</h3>
        <div class="step-result__cards">
            <div class="step-result__card animate__animated animate__fadeInUp d-1">
                <span>Давление</span>
                {{ inspection.hasOwnProperty('tonometer') ? inspection.tonometer  : 'Неизвестно' }}
            </div>
            <div class="step-result__card animate__animated animate__fadeInUp d-2">
                <span>Пульс</span>
                {{ inspection.hasOwnProperty('pulse') ? inspection.pulse : 'Неизвестно' }}
            </div>
            <div class="step-result__card animate__animated animate__fadeInUp d-2">
                <span>Количество промилей</span>
                {{ inspection.hasOwnProperty('alcometer_result') ? inspection.alcometer_result + ' ‰' : 'Неизвестно' }}
            </div>
            <div class="step-result__card animate__animated animate__fadeInUp d-2">
                <span>Температура тела</span>
                {{ inspection.hasOwnProperty('t_people') ? inspection.t_people + ' °C' : 'Неизвестно' }}
            </div>
            <div class="step-result__card animate__animated animate__fadeInUp d-2">
                <span>Сонливость</span>
                {{ inspection.hasOwnProperty('sleep_status') ? getStatus(inspection.sleep_status) : 'Неизвестно' }}
            </div>
            <div class="step-result__card animate__animated animate__fadeInUp d-2">
                <span>Самочувствиек</span>
                {{ inspection.hasOwnProperty('people_status') ? getStatus(inspection.people_status) : 'Неизвестно' }}
            </div>
        </div>
        <div class="step-result__footer">
            <span v-if="result.admitted" class="step-result__text animate__animated animate__fadeInUp">
                {{ result.admitted || 'ожидание ответа' }}
            </span>
            <button @click="$router.push('/')" class="btn blue animate__animated animate__fadeInUp">В начало</button>
            <button class="btn opacity animate__animated animate__fadeInUp">Повтор печати</button>
        </div>
    </div>
</template>