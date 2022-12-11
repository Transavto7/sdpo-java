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
    <div class="step-1__outer">
        <div class="step-1">
            <div class="step-1__item">
                <div class="step-1__label">
                    Ваш идентификатор
                </div>
                <div class="step-1__id">
                    <span v-for="number in inspection?.driver_id?.split('') || []" :key="number">
                        {{ number }}
                    </span>
                </div>
            </div>

            <div class="step-1__item">
                <div class="step-1__label">
                    Ваше полное имя
                </div>
                <div class="step-1__name">
                    {{ inspection.driver_fio }}
                </div>
            </div>
        </div>

        <div class="step-buttons">
            <button @click="$router.push('/')" class="btn opacity blue">Назад</button>
            <button @click="$router.push('/step/2')" class="btn">Продолжить</button>
        </div>
    </div>
</template>