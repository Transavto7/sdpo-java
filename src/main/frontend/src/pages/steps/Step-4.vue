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
    }
}
</script>

<template>
    <div class="step-4__outer">
        <div class="step-4">
            <h3>Измерение температуры тела</h3>
            <img src="@/assets/images/pirometer.png">
        </div>

        <div class="step-buttons">
            <button @click="$router.push('/step/3')" class="btn opacity blue">Назад</button>
            <button @click="$router.push('/step/5')" v-if="this.$store.state.inspection.type_view" class="btn">Продолжить</button>
        </div>
    </div>
</template>