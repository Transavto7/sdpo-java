<script>
export default {
    data() {
        return {
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
    methods: {
        next(result) {
            this.$store.state.inspection.sleep_status = result;
            this.$router.push('/step/7');
        }
    }
}
</script>

<template>
    <div class="step-question__outer">
        <div class="step-question">
            <h3 class="animate__animated animate__fadeInDown">Вы выспались?</h3>
            <button @click="next('Нет')" class="btn opacity  animate__animated animate__fadeInUp d-1">Нет</button>
            <button @click="next('Да')" class="btn animate__animated animate__fadeInUp d-2">Да</button>
        </div>

        <div class="step-buttons">
            <button @click="$router.push('/step/5')" class="btn opacity blue">Назад</button>
        </div>
    </div>
</template>