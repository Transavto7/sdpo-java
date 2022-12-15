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
    }
}
</script>

<template>
    <div class="step-4__outer">
        <div class="step-4">
            <h3 class="animate__animated animate__fadeInUp">Измерение температуры тела</h3>
            <img class="animate__animated animate__fadeInUp d-1" src="@/assets/images/pirometer.png">
        </div>

        <div class="step-buttons">
            <button @click="$router.push('/step/3')" class="btn opacity blue">Назад</button>
            <button @click="$router.push('/step/5')" v-if="JSON.parse(system.thermometer_skip)" class="btn">Продолжить</button>
        </div>
    </div>
</template>