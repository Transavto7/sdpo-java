<script>
export default {
    data() {
        return {
        }
    },
    computed: {
        inspection() {
            return this.$store.state.inspection;
        }
    },
    methods: {
        next(result) {
            this.$store.state.inspection.people_status = result;
            this.$router.push('/step/8');
        }
    }
}
</script>

<template>
    <div class="step-question__outer">
        <div class="step-question">
            <h3 class="animate__animated animate__fadeInDown">Вы хорошо себя чувствуете?</h3>
            <button @click="next('Нет')" class="btn opacity animate__animated animate__fadeInUp d-1">Нет</button>
            <button @click="next('Да')" class="btn animate__animated animate__fadeInUp d-2">Да</button>
        </div>

        <div class="step-buttons">
            <button @click="$router.push('/step/5')" class="btn opacity blue">Назад</button>
        </div>
    </div>
</template>