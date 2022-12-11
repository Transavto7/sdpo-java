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
    <div class="step-2__outer">
        <div class="step-2">
            <h3>Выберите тип осмотра</h3>
            <div class="step-2__select">
                <button class="step-2__item"
                    :class="{active: $store.state.inspection.type_view === 'Предрейсовый/Предсменный'}"
                    @click="$store.state.inspection.type_view = 'Предрейсовый/Предсменный'"
                >
                    Предрейсовый / Предсменный
                </button>
                <button class="step-2__item" 
                    :class="{active: $store.state.inspection.type_view === 'Послерейсовый/Послесменный'}"
                    @click="$store.state.inspection.type_view = 'Послерейсовый/Послесменный'"
                >
                    Послерейсовый / Послесменный
                </button>
            </div>
        </div>

        <div class="step-buttons">
            <button @click="$router.push('/step/1')" class="btn opacity blue">Назад</button>
            <button @click="$router.push('/step/3')" v-if="this.$store.state.inspection.type_view" class="btn">Продолжить</button>
        </div>
    </div>
</template>